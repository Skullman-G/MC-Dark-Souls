package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.Faction;
import com.skullmangames.darksouls.common.entity.ai.goal.ArcherGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.Difficulty;

public abstract class HumanoidData<T extends Mob> extends MobData<T>
{
	public HumanoidData(Faction faction)
	{
		super(faction);
	}

	@Override
	public void postInit()
	{
		if (!this.isClientSide() && !this.orgEntity.isNoAi())
		{
			super.resetCombatAI();
			WeaponCap heldItem = ModCapabilities.getWeaponCapability(this.orgEntity.getMainHandItem());
			
			if (heldItem.getWeaponCategory() == WeaponCategory.RANGED_WEAPON && this.orgEntity instanceof RangedAttackMob)
			{
				this.setAIAsRange();
			}
			else if(this.orgEntity.getControllingPassenger() != null && this.orgEntity.getControllingPassenger() instanceof Mob)
			{
				this.setAIAsMounted(this.orgEntity.getControllingPassenger());
			}
			else if (this.isArmed())
			{
				this.setAIAsArmed(heldItem.getWeaponCategory());
			}
			else
			{
				this.setAIAsUnarmed();
			}
		}
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		animatorClient.mixLayerLeft.setJointMask("Root", "Torso");
	}

	public void setAIAsUnarmed() {}

	public void setAIAsArmed(WeaponCategory category) {}
	
	public void setAIAsMounted(Entity ridingEntity)
	{
		if (isArmed())
		{
			orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
					.addAttack(new AttackInstance(1, 1.0F, Animations.SWORD_MOUNT_ATTACK)));

			if (ridingEntity instanceof AbstractHorse)
			{
				orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, 1.0D));
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAIAsRange()
	{
		int cooldown = this.orgEntity.level.getDifficulty() != Difficulty.HARD ? 40 : 20;
		orgEntity.goalSelector.addGoal(1, new ArcherGoal(this, 1.0D, cooldown, 15.0F));
	}
	
	public boolean isArmed()
	{
		return ModCapabilities.getWeaponCapability(this.orgEntity.getMainHandItem()) != null;
	}
	
	public void onMount(boolean isMount, Entity ridingEntity)
	{
		if(orgEntity == null)
		{
			return;
		}
		
		this.resetCombatAI();
		
		if(isMount)
		{
			this.setAIAsMounted(ridingEntity);
		}
		else
		{
			if(this.isArmed())
			{
				this.setAIAsArmed(ModCapabilities.getWeaponCapability(this.orgEntity.getMainHandItem()).getWeaponCategory());
			}
			else
			{
				this.setAIAsUnarmed();
			}
		}
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		if (orgEntity.getControllingPassenger() != null)
		{
			return Animations.BIPED_HIT_ON_MOUNT;
		}
		else
		{
			switch (stunType)
			{
				case DEFAULT:
					return Animations.BIPED_HIT_SHORT;
					
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