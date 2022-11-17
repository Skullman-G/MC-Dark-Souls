package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damage;
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
		
		IShield shield = (IShield)this.getHeldWeaponCapability(this.orgEntity.getUsedItemHand());
		for (Damage damage : damageSource.getDamages())
		{
			damage.setAmount(damage.getAmount() * (1 - shield.getDefense(damage.getType())));
		}
		
		damageSource.setStunType(StunType.DISARMED);
		this.cancelUsingItem();
		return true;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType, Entity attacker)
	{
		return getHumanoidHitAnimation(this, attacker, stunType);
	}
	
	public static StaticAnimation getHumanoidHitAnimation(LivingCap<?> entityCap, Entity attacker, StunType stunType)
	{
		if (attacker == null) return null;
		else if (entityCap.isBlocking() && stunType != StunType.DISARMED)
		{
			if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_BLOCK_HIT;
			return Animations.BIPED_BLOCK_HIT_MIRROR;
		}
		else
		{
			float attackAngle = ((float)Math.toDegrees(Math.atan2(entityCap.getX() - attacker.getX(), entityCap.getZ() - attacker.getZ())) + 360F) % 360F;
			float yRot = entityCap.getYRot() - 180;
			if (yRot < -180) yRot += 360F;
			float dir = Math.abs(-yRot - attackAngle);
			switch (stunType)
			{
				case DISARMED:
					if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_DISARM_SHIELD_RIGHT;
					return Animations.BIPED_DISARM_SHIELD_LEFT;
					
				case LIGHT:
					return dir <= 315 && dir >= 225 ? entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_RIGHT : Animations.BIPED_HIT_LIGHT_RIGHT
							: dir <= 225 && dir >= 135 ? entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_BACK : Animations.BIPED_HIT_LIGHT_BACK
							: dir <= 135 && dir >= 45 ? entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_LEFT : Animations.BIPED_HIT_LIGHT_LEFT
							: entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_FRONT : Animations.BIPED_HIT_LIGHT_FRONT;
					
				case HEAVY:
					return dir <= 315 && dir >= 225 ? entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_RIGHT : Animations.BIPED_HIT_HEAVY_RIGHT
							: dir <= 225 && dir >= 135 ? entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_BACK : Animations.BIPED_HIT_HEAVY_BACK
							: dir <= 135 && dir >= 45 ? entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_LEFT : Animations.BIPED_HIT_HEAVY_LEFT
							: entityCap.isRidingHorse() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_FRONT : Animations.BIPED_HIT_HEAVY_FRONT;
					
				case SMASH:
					return Animations.BIPED_HIT_SMASH;
					
				case FLY:
					return dir <= 315 && dir >= 225 ? Animations.BIPED_HIT_FLY_LEFT
							: dir <= 225 && dir >= 135 ? Animations.BIPED_HIT_FLY_BACK
							: dir <= 135 && dir >= 45 ? Animations.BIPED_HIT_FLY_RIGHT
							: Animations.BIPED_HIT_FLY;
					
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