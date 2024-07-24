package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.entity.ai.goal.BowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

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
	public boolean canBeBackstabbed()
	{
		return !this.getEntityState().isInvincible();
	}
	
	@Override
	public boolean canBePunished()
	{
		return this.getEntityState() == EntityState.PUNISHABLE;
	}
	
	@Override
	public StaticAnimation getDeflectAnimation()
	{
		return Animations.HOLLOW_DEFLECTED.get();
	}
	
	@Override
	public boolean canBeParried()
	{
		return true;
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
			WeaponCap heldItem = ModCapabilities.getWeaponCap(this.orgEntity.getMainHandItem());
			this.setAttackGoals(heldItem.getWeaponCategory());
		}
	}
	
	@Override
	public boolean blockingAttack(ExtendedDamageSource damageSource)
	{
		Entity attacker = damageSource.getSource();
		if (attacker == null || !damageSource.wasBlocked() || !this.isBlocking()) return false;

		Shield shield = (Shield)this.getHeldMeleeWeaponCap(this.orgEntity.getUsedItemHand());
		this.increaseStamina(-damageSource.getStaminaDamage() * (1 - shield.getStability()));
		if (this.getStamina() > 0.0F) return super.blockingAttack(damageSource);
		
		damageSource.getDamages().foreach((type, amount) ->
		{
			damageSource.getDamages().put(type, amount * (1 - shield.getDefense(type.coreType())));
		});
		
		damageSource.setWasBlocked(true);
		this.playSound(shield.getBlockSound());
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
		if (attacker == null) return Animations.BIPED_DEATH.get();
		float dir = dmgSource.getAttackAngle(entityCap.orgEntity);
		
		switch (dmgSource.getStunType())
		{
			case FLY:
				return dir <= 315 && dir >= 225 ? Animations.BIPED_DEATH_FLY_LEFT.get()
						: dir <= 225 && dir >= 135 ? Animations.BIPED_DEATH_FLY_BACK.get()
						: dir <= 135 && dir >= 45 ? Animations.BIPED_DEATH_FLY_RIGHT.get()
						: Animations.BIPED_DEATH_FLY_FRONT.get();
			
			case SMASH: return Animations.BIPED_DEATH_SMASH.get();
			
			case BACKSTABBED: return Animations.BIPED_DEATH_BACKSTAB.get();
			
			case PUNISHED: return Animations.BIPED_DEATH_PUNISH.get();
				
			default: return Animations.BIPED_DEATH.get();
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
		boolean twoHanding = entityCap.isTwohanding();
		InteractionHand usedHand = entityCap.getOriginalEntity().getUsedItemHand();
		boolean horizontalBlocking = entityCap.getShieldHoldType(usedHand).isBlockingHorizontally();
		if (attacker == null) return null;
		else
		{
			float dir = dmgSource.getAttackAngle(entityCap.orgEntity);
			// Blocked
			if (dmgSource.wasBlocked() && stunType != StunType.DISARMED)
			{
				if (stunType == StunType.FLY)
				{
					if (twoHanding)
					{
						return Animations.BIPED_HIT_BLOCKED_TH_SWORD_FLY.get();
					}
					if (horizontalBlocking)
					{
						return usedHand == InteractionHand.MAIN_HAND
								? Animations.BIPED_HIT_BLOCKED_FLY_RIGHT.get() : Animations.BIPED_HIT_BLOCKED_FLY_LEFT.get();
					}
					return usedHand == InteractionHand.MAIN_HAND
							? Animations.BIPED_HIT_BLOCKED_VERTICAL_FLY_RIGHT.get() : Animations.BIPED_HIT_BLOCKED_VERTICAL_FLY_LEFT.get();
				}
				else
				{
					if (twoHanding)
					{
						return Animations.BIPED_HIT_BLOCKED_TH_SWORD.get();
					}
					if (horizontalBlocking)
					{
						return usedHand == InteractionHand.MAIN_HAND
								? Animations.BIPED_HIT_BLOCKED_RIGHT.get() : Animations.BIPED_HIT_BLOCKED_LEFT.get();
						
					}
					return usedHand == InteractionHand.MAIN_HAND
							? Animations.BIPED_HIT_BLOCKED_VERTICAL_RIGHT.get() : Animations.BIPED_HIT_BLOCKED_VERTICAL_LEFT.get();
				}
			}
			// Hit
			else
			{
				switch (stunType)
				{
					case DISARMED:
						if (usedHand == InteractionHand.MAIN_HAND) return Animations.BIPED_DISARMED_RIGHT.get();
						return Animations.BIPED_DISARMED_LEFT.get();
						
					case LIGHT:
						return dir <= 315 && dir >= 225 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_RIGHT.get() : Animations.BIPED_HIT_LIGHT_RIGHT.get()
								: dir <= 225 && dir >= 135 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_BACK.get() : Animations.BIPED_HIT_LIGHT_BACK.get()
								: dir <= 135 && dir >= 45 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_LEFT.get() : Animations.BIPED_HIT_LIGHT_LEFT.get()
								: entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_LIGHT_FRONT.get() : Animations.BIPED_HIT_LIGHT_FRONT.get();
						
					case HEAVY:
						return dir <= 315 && dir >= 225 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_RIGHT.get() : Animations.BIPED_HIT_HEAVY_RIGHT.get()
								: dir <= 225 && dir >= 135 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_BACK.get() : Animations.BIPED_HIT_HEAVY_BACK.get()
								: dir <= 135 && dir >= 45 ? entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_LEFT.get() : Animations.BIPED_HIT_HEAVY_LEFT.get()
								: entityCap.isMounted() ? Animations.BIPED_HORSEBACK_HIT_HEAVY_FRONT.get() : Animations.BIPED_HIT_HEAVY_FRONT.get();
						
					case SMASH:
						return Animations.BIPED_HIT_SMASH.get();
						
					case FLY:
						return dir <= 315 && dir >= 225 ? Animations.BIPED_HIT_FLY_LEFT.get()
								: dir <= 225 && dir >= 135 ? Animations.BIPED_HIT_FLY_BACK.get()
								: dir <= 135 && dir >= 45 ? Animations.BIPED_HIT_FLY_RIGHT.get()
								: Animations.BIPED_HIT_FLY_FRONT.get();
								
					case BACKSTABBED:
						return Animations.BIPED_HIT_BACKSTAB.get();
						
					case PUNISHED:
						return Animations.BIPED_HIT_PUNISH.get();
						
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