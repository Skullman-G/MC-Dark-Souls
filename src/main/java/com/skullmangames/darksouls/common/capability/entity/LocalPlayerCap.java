package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
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
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.phys.EntityHitResult;

@OnlyIn(Dist.CLIENT)
public class LocalPlayerCap extends AbstractClientPlayerCap<LocalPlayer>
{
	private LivingEntity rayTarget;
	private Minecraft minecraft = Minecraft.getInstance();
	
	@Override
	public void onEntityConstructed(LocalPlayer entity)
	{
		super.onEntityConstructed(entity);
		ClientManager.INSTANCE.setPlayerCap(this);
		ClientManager.INSTANCE.inputManager.setGamePlayer(this);
		ModNetworkManager.connection = new ModClientPlayNetHandler();
	}
	
	@Override
	public void onEntityJoinWorld(LocalPlayer entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		
		if (minecraft.options.getCameraType() == CameraType.THIRD_PERSON_BACK) ClientManager.INSTANCE.switchToThirdPerson();
		else ClientManager.INSTANCE.switchToFirstPerson();
	}
	
	@Override
	public void onLoad(CompoundTag nbt)
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
		
		HitResult rayResult = this.minecraft.hitResult;

		if (rayResult.getType() == HitResult.Type.ENTITY)
		{
			Entity hit = ((EntityHitResult)rayResult).getEntity();
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
		WeaponCap weapon = ModCapabilities.getWeaponCap(this.orgEntity.getItemInHand(InteractionHand.MAIN_HAND));
		if (weapon != null)
		{
			weapon.performAttack(type, this);
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
			
			if (animation == null) return;
			this.animator.playAnimation(animation, 0.0F);
			ModNetworkManager.sendToServer(new CTSPlayAnimation(animation, 0.0F, false, false));
		}
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
		return this.minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
	}
	
	@Override
	public void setHuman(boolean value)
	{
		if (this.human == value) return;
		super.setHuman(value);
		if (value) ModNetworkManager.connection.setTitle(new TranslatableComponent("gui.darksouls.humanity_restored_message"), 10, 50, 10);
	}
}