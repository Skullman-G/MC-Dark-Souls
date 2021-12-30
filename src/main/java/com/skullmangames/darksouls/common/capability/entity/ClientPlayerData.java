package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability.AttackType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSHuman;
import com.skullmangames.darksouls.network.client.CTSHumanity;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.client.CTSSouls;
import com.skullmangames.darksouls.network.client.CTSStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerData extends RemoteClientPlayerData<ClientPlayerEntity>
{
	private LivingEntity rayTarget;
	private Minecraft minecraft = Minecraft.getInstance();
	
	@Override
	public void onEntityConstructed(ClientPlayerEntity entity)
	{
		super.onEntityConstructed(entity);
		ClientManager.INSTANCE.setPlayerData(this);
		ClientManager.INSTANCE.inputManager.setGamePlayer(this);
	}
	
	@Override
	public void onEntityJoinWorld(ClientPlayerEntity entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		
		if (minecraft.options.getCameraType() == PointOfView.THIRD_PERSON_BACK) ClientManager.INSTANCE.switchToThirdPerson();
		else ClientManager.INSTANCE.switchToFirstPerson();
	}
	
	@Override
	public void updateMotion()
	{
		super.updateMotion();

		if (!this.getClientAnimator().prevAiming())
		{
			if (this.currentMixMotion == LivingMotion.AIMING)
			{
				this.orgEntity.getUseItemRemainingTicks();
				ClientManager.INSTANCE.renderEngine.zoomIn();
			}
		}
	}
	
	@Override
	public void updateOnClient()
	{
		super.updateOnClient();
		
		RayTraceResult rayResult = this.minecraft.hitResult;

		if (rayResult.getType() == RayTraceResult.Type.ENTITY)
		{
			Entity hit = ((EntityRayTraceResult)rayResult).getEntity();
			if (hit instanceof LivingEntity)
				this.rayTarget = (LivingEntity)hit;
		}

		if (this.rayTarget != null)
		{
			if(!this.rayTarget.isAlive())
			{
				this.rayTarget = null;
			}
			else if(this.getOriginalEntity().distanceToSqr(this.rayTarget) > 64.0D)
			{
				this.rayTarget = null;
			}
			else if(MathUtils.getAngleBetween(this.getOriginalEntity(), this.rayTarget) > 1.5707963267948966D)
			{
				this.rayTarget = null;
			}
		}
	}
	
	public void performDodge()
	{
		if (this.isFirstPerson()) return;
		this.animator.playAnimation(Animations.BIPED_DODGE, 0);
		ModNetworkManager.sendToServer(new CTSPlayAnimation(Animations.BIPED_DODGE, 0, false, false));
		
		if (this.isCreativeOrSpectator()) return;
		this.increaseStamina(-4.0F);
		ModNetworkManager.sendToServer(new CTSStamina(this.stamina));
	}
	
	public void performAttack(AttackType type)
	{
		AttackAnimation animation = null;
		if (!this.minecraft.options.getCameraType().isFirstPerson() && this.orgEntity.getMainHandItem().getItem() == Items.AIR)
		{
			switch (type)
			{
				default:
				case LIGHT:
					List<AttackAnimation> animations = new ArrayList<AttackAnimation>(Arrays.asList(Animations.FIST_LIGHT_ATTACK));
					int combo = animations.indexOf(this.getClientAnimator().baseLayer.animationPlayer.getPlay());
					if (combo + 1 < animations.size()) combo += 1;
					else combo = 0;
					animation = animations.get(combo);
					break;
					
				case DASH:
					animation = Animations.FIST_DASH_ATTACK;
					break;
					
				case HEAVY:
					animation = Animations.FIST_HEAVY_ATTACK;
					break;
			}
		}
		else
		{
			WeaponCapability weapon = this.getHeldWeaponCapability(Hand.MAIN_HAND);
			if (weapon == null) return;
			animation = weapon.getAttack(type, this);
		}
		
		if (animation == null) return;
		this.animator.playAnimation(animation, 0.0F);
		ModNetworkManager.sendToServer(new CTSPlayAnimation(animation, 0.0F, false, false));
		
		if (this.isCreativeOrSpectator()) return;
		this.increaseStamina(-4.0F);
		ModNetworkManager.sendToServer(new CTSStamina(this.stamina));
	}
	
	@Override
	protected void playReboundAnimation()
	{
		this.getClientAnimator().playReboundAnimation();
		ClientManager.INSTANCE.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime)
	{
		ModNetworkManager.sendToServer(new CTSPlayAnimation(id, modifyTime, false, true));
	}
	
	@Override
	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap)
	{
		super.onHeldItemChange(mainHandCap, offHandCap);
		if (mainHandCap != null) mainHandCap.onHeld(this);
	}
	
	@Override
	public void aboutToDeath() {}
	
	public void initFromOldOne(ClientPlayerData old)
	{
		this.setStunArmor(old.getStunArmor());
	}
	
	@Override
	public LivingEntity getTarget()
	{
		return this.rayTarget;
	}
	
	@Override
	public boolean isFirstPerson()
	{
		return this.minecraft.options.getCameraType() == PointOfView.FIRST_PERSON;
	}
	
	@Override
	public void setHumanity(int value)
	{
		if (this.humanity == value) return;
		super.setHumanity(value);
		ModNetworkManager.sendToServer(new CTSHumanity(this.humanity));
	}
	
	@Override
	public void setHuman(boolean value)
	{
		if (this.human == value) return;
		super.setHuman(value);
		ModNetworkManager.sendToServer(new CTSHuman(this.human));
	}
	
	@Override
	public void setSouls(int value)
	{
		if (this.souls == value) return;
		super.setSouls(value);
		ModNetworkManager.sendToServer(new CTSSouls(this.souls));
	}
}