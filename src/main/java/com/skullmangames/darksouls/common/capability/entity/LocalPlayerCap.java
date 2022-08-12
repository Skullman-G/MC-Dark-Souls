package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPerformDodge;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
import com.skullmangames.darksouls.network.play.ModClientPlayNetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LocalPlayerCap extends AbstractClientPlayerCap<ClientPlayerEntity>
{
	private LivingEntity rayTarget;
	private Minecraft minecraft = Minecraft.getInstance();
	
	@Override
	public void onEntityConstructed(ClientPlayerEntity entity)
	{
		super.onEntityConstructed(entity);
		ClientManager.INSTANCE.setPlayerCap(this);
		ClientManager.INSTANCE.inputManager.setGamePlayer(this);
		ModNetworkManager.connection = new ModClientPlayNetHandler();
	}
	
	@Override
	public void onEntityJoinWorld(ClientPlayerEntity entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		
		if (minecraft.options.getCameraType() == PointOfView.THIRD_PERSON_BACK) ClientManager.INSTANCE.switchToThirdPerson();
		else ClientManager.INSTANCE.switchToFirstPerson();
	}
	
	@Override
	public void onLoad(CompoundNBT nbt)
	{
		super.onLoad(nbt);
		GameOverlayManager.canAnimateSouls = true;
		GameOverlayManager.lastSouls = this.getSouls();
		GameOverlayManager.lerpSouls = this.getSouls();
	}
	
	@Override
	public void updateMotion()
	{
		super.updateMotion();

		if (!this.getClientAnimator().isAiming())
		{
			if (this.currentMixMotions.containsValue(LivingMotion.AIMING))
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
	
	public void performDodge(boolean moving)
	{
		if (this.isFirstPerson()) return;
		ModNetworkManager.sendToServer(new CTSPerformDodge(moving));
	}
	
	public void performAttack(AttackType type)
	{
		AttackAnimation animation = null;
		MeleeWeaponCap weapon = this.getHeldWeaponCapability(Hand.MAIN_HAND);
		if (weapon != null)
		{
			animation = weapon.getAttack(type, this);
		}
		else
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
		
		if (animation == null) return;
		this.animator.playAnimation(animation, 0.0F);
		ModNetworkManager.sendToServer(new CTSPlayAnimation(animation, 0.0F, false, false));
	}
	
	@Override
	protected void playReboundAnimation()
	{
		this.getClientAnimator().playReboundAnimation();
		ClientManager.INSTANCE.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier, AnimationPacketProvider packetProvider)
	{
		ModNetworkManager.sendToServer(new CTSPlayAnimation(animation.getId(), convertTimeModifier, false, true));
	}
	
	@Override
	public void onHeldItemChange(ItemCapability mainHandCap, ItemCapability offHandCap)
	{
		super.onHeldItemChange(mainHandCap, offHandCap);
		if (mainHandCap != null) mainHandCap.onHeld(this);
	}
	
	@Override
	public void onDeath() {}
	
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
	public void setHuman(boolean value)
	{
		if (this.human == value) return;
		super.setHuman(value);
		if (value) ModNetworkManager.connection.setTitle(new TranslationTextComponent("gui.darksouls.humanity_restored_message"), 10, 50, 10);
	}
	
	@Override
	public void setSouls(int value)
	{
		if (this.souls == value) return;
		super.setSouls(value);
	}
}