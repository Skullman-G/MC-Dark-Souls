package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.StandStillGoal;
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
			this.orgEntity.goalSelector.addGoal(0, new StandStillGoal(this));
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
		Entity attacker = damageSource.getSource();
		if (attacker == null) return false;
		float attackAngle = ((float)Math.toDegrees(Math.atan2(this.getX() - attacker.getX(), this.getZ() - attacker.getZ())) + 360F) % 360F;
		float yRot = this.getYRot() - 180;
		if (yRot < -180) yRot += 360F;
		float dir = Math.abs(-yRot - attackAngle);
		if (!(dir <= 60 || dir >= 300) || !this.isBlocking()) return false;

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
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return getHumanoidDeathAnimation(this, dmgSource);
	}
	
	public static DeathAnimation getHumanoidDeathAnimation(LivingCap<?> entityCap, ExtendedDamageSource dmgSource)
	{
		Entity attacker = dmgSource.getSource();
		float attackAngle = ((float)Math.toDegrees(Math.atan2(entityCap.getX() - attacker.getX(), entityCap.getZ() - attacker.getZ())) + 360F) % 360F;
		float yRot = entityCap.getYRot() - 180;
		if (yRot < -180) yRot += 360F;
		float dir = Math.abs(-yRot - attackAngle);
		
		if (dmgSource.getStunType() == StunType.FLY)
		{
			return dir <= 315 && dir >= 225 ? Animations.BIPED_DEATH_FLY_LEFT
					: dir <= 225 && dir >= 135 ? Animations.BIPED_DEATH_FLY_BACK
					: dir <= 135 && dir >= 45 ? Animations.BIPED_DEATH_FLY_RIGHT
					: Animations.BIPED_DEATH_FLY_FRONT;
		}
		else if (dmgSource.getStunType() == StunType.SMASH)
		{
			return Animations.BIPED_DEATH_SMASH;
		}
		else
		{
			return Animations.BIPED_DEATH;
		}
	}
	
	@Override
	public StaticAnimation getHitAnimation(ExtendedDamageSource dmgSource)
	{
		return getHumanoidHitAnimation(this, dmgSource);
	}
	
	public static StaticAnimation getHumanoidHitAnimation(LivingCap<?> entityCap, ExtendedDamageSource dmgSource)
	{
		Entity attacker = dmgSource.getSource();
		StunType stunType = dmgSource.getStunType();
		if (attacker == null) return null;
		else
		{
			float attackAngle = ((float)Math.toDegrees(Math.atan2(entityCap.getX() - attacker.getX(), entityCap.getZ() - attacker.getZ())) + 360F) % 360F;
			float yRot = entityCap.getYRot() - 180;
			if (yRot < -180) yRot += 360F;
			float dir = Math.abs(-yRot - attackAngle);
			if ((dir <= 60 || dir >= 300) && entityCap.isBlocking() && stunType != StunType.DISARMED)
			{
				if (stunType == StunType.FLY)
				{
					if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_HIT_BLOCKED_FLY_RIGHT;
					return Animations.BIPED_HIT_BLOCKED_FLY_LEFT;
				}
				else
				{
					if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_HIT_BLOCKED_RIGHT;
					return Animations.BIPED_HIT_BLOCKED_LEFT;
				}
			}
			else
			{
				switch (stunType)
				{
					case DISARMED:
						if (entityCap.getOriginalEntity().getUsedItemHand() == InteractionHand.MAIN_HAND) return Animations.BIPED_DISARM_SHIELD_RIGHT;
						return Animations.BIPED_DISARM_SHIELD_LEFT;
						
					case LIGHT:
						return dir <= 315 && dir >= 225 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_RIGHT : Animations.BIPED_HIT_LIGHT_RIGHT
								: dir <= 225 && dir >= 135 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_BACK : Animations.BIPED_HIT_LIGHT_BACK
								: dir <= 135 && dir >= 45 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_LEFT : Animations.BIPED_HIT_LIGHT_LEFT
								: entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_FRONT : Animations.BIPED_HIT_LIGHT_FRONT;
						
					case HEAVY:
						return dir <= 315 && dir >= 225 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_RIGHT : Animations.BIPED_HIT_HEAVY_RIGHT
								: dir <= 225 && dir >= 135 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_BACK : Animations.BIPED_HIT_HEAVY_BACK
								: dir <= 135 && dir >= 45 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_LEFT : Animations.BIPED_HIT_HEAVY_LEFT
								: entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_FRONT : Animations.BIPED_HIT_HEAVY_FRONT;
						
					case SMASH:
						return Animations.BIPED_HIT_SMASH;
						
					case FLY:
						return dir <= 315 && dir >= 225 ? Animations.BIPED_HIT_FLY_LEFT
								: dir <= 225 && dir >= 135 ? Animations.BIPED_HIT_FLY_BACK
								: dir <= 135 && dir >= 45 ? Animations.BIPED_HIT_FLY_RIGHT
								: Animations.BIPED_HIT_FLY_FRONT;
						
					default:
						return null;
				}
			}
		}
	}
	
	@Override
	public <M extends Model>M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}
}