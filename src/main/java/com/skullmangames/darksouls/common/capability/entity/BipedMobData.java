package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.Faction;
import com.skullmangames.darksouls.common.entity.ai.goal.ArcherGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.world.Difficulty;

public abstract class BipedMobData<T extends MobEntity> extends MobData<T>
{
	public BipedMobData(Faction faction)
	{
		super(faction);
	}

	@Override
	public void postInit()
	{
		if (!this.isClientSide() && !this.orgEntity.isNoAi())
		{
			super.resetCombatAI();
			Item heldItem = this.orgEntity.getMainHandItem().getItem();
			
			if (heldItem instanceof ShootableItem && this.orgEntity instanceof IRangedAttackMob)
			{
				this.setAIAsRange();
			}
			else if(this.orgEntity.getControllingPassenger() != null && this.orgEntity.getControllingPassenger() instanceof MobEntity)
			{
				this.setAIAsMounted(this.orgEntity.getControllingPassenger());
			}
			else if (isArmed())
			{
				this.setAIAsArmed();
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

	public void setAIAsUnarmed()
	{

	}

	public void setAIAsArmed()
	{
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
				.addAttack(new AttackInstance(1, 1.0F, Animations.STRAIGHT_SWORD_LIGHT_ATTACK)));
	}
	
	public void setAIAsMounted(Entity ridingEntity)
	{
		if (isArmed())
		{
			orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, 0.0F, true)
					.addAttack(new AttackInstance(1, 1.0F, Animations.SWORD_MOUNT_ATTACK)));

			if (ridingEntity instanceof AbstractHorseEntity)
			{
				orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setAIAsRange()
	{
		int cooldown = this.orgEntity.level.getDifficulty() != Difficulty.HARD ? 40 : 20;
		orgEntity.goalSelector.addGoal(1, new ArcherGoal(this, this.orgEntity, 1.0D, cooldown, 15.0F));
	}
	
	public boolean isArmed()
	{
		Item heldItem = this.orgEntity.getMainHandItem().getItem();
		return heldItem instanceof SwordItem || heldItem instanceof ToolItem || heldItem instanceof TridentItem;
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
				this.setAIAsArmed();
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
				case LONG:
					return Animations.BIPED_HIT_LONG;
					
				case SHORT:
					return Animations.BIPED_HIT_SHORT;
					
				case HOLD:
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
}