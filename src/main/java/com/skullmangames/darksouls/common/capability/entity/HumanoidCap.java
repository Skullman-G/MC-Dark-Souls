package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;

public abstract class HumanoidCap<T extends Mob> extends MobCap<T>
{
	@Override
	public void postInit()
	{
		if (!this.isClientSide() && !this.orgEntity.isNoAi())
		{
			super.resetCombatAI();
			WeaponCap heldItem = ModCapabilities.getWeaponCap(this.orgEntity.getMainHandItem());
			
			if(!(this.orgEntity.getControllingPassenger() != null && this.orgEntity.getControllingPassenger() instanceof Mob) && this.isArmed())
			{
				this.setAttackGoals(heldItem.getWeaponCategory());
			}
		}
	}
	
	@Override
	public StaticAnimation getDeflectAnimation()
	{
		return Animations.HOLLOW_DEFLECTED;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAttackGoals(WeaponCategory category)
	{
		if (category == WeaponCategory.BOW && this.orgEntity instanceof RangedAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new BowAttackGoal(this, 40, 15.0F));
		}
		else if (category == WeaponCategory.CROSSBOW && this.orgEntity instanceof CrossbowAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new CrossbowAttackGoal(this));
		}
	}
	
	public boolean isArmed()
	{
		return ModCapabilities.getWeaponCap(this.orgEntity.getMainHandItem()) != null;
	}
	
	public void onMount(boolean isMount, Entity ridingEntity)
	{
		if(orgEntity == null)
		{
			return;
		}
		
		this.resetCombatAI();
		
		if(!isMount && this.isArmed())
		{
			this.setAttackGoals(ModCapabilities.getWeaponCap(this.orgEntity.getMainHandItem()).getWeaponCategory());
		}
	}
	
	@Override
	public boolean blockingAttack(ExtendedDamageSource damageSource)
	{
		if (!this.isBlocking()) return false;
		if (damageSource == null) return true;

		this.increaseStamina(-damageSource.getStaminaDamage());
		if (this.getStamina() > 0.0F) return super.blockingAttack(damageSource);
		
		damageSource.setStunType(StunType.DISARMED);
		this.cancelUsingItem();
		return true;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return getHumanoidHitAnimation(this, stunType);
	}
	
	public static StaticAnimation getHumanoidHitAnimation(LivingCap<?> entityCap, StunType stunType)
	{
		if (entityCap.getOriginalEntity().getControllingPassenger() != null)
		{
			return Animations.BIPED_HIT_ON_MOUNT;
		}
		else if (entityCap.isBlocking() && stunType != StunType.DISARMED)
		{
			if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_BLOCK_HIT;
			return Animations.BIPED_BLOCK_HIT_MIRROR;
		}
		else
		{
			switch (stunType)
			{
				case DEFAULT:
					return Animations.BIPED_HIT_SHORT;
					
				case DISARMED:
					if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_DISARM_SHIELD_RIGHT;
					return Animations.BIPED_DISARM_SHIELD_LEFT;
					
				case SMASH_FRONT:
					return Animations.BIPED_HIT_DOWN_FRONT;
					
				case SMASH_BACK:
					return Animations.BIPED_HIT_DOWN_BACK;
					
				default:
					return null;
			}
		}
	}
	
	@Override
	public <M extends Model>M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}
}