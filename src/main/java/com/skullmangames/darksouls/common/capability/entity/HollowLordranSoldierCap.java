package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackPatternGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.ChasingGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.StrafingGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

import net.minecraft.world.entity.monster.CrossbowAttackMob;

public class HollowLordranSoldierCap extends HumanoidCap<HollowLordranSoldier>
{
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isClientSide()) this.orgEntity.setCanPickUpLoot(false);
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_LORDRAN_SOLDIER_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_LORDRAN_SOLDIER_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.BLOCKING, Animations.HOLLOW_LORDRAN_SOLDIER_BLOCK);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		if (category == WeaponCategory.CROSSBOW && this.orgEntity instanceof CrossbowAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new CrossbowAttackGoal(this));
		}
		else
		{
			if (category == WeaponCategory.STRAIGHT_SWORD)
			{
				this.orgEntity.goalSelector.addGoal(2, new ChasingGoal(this, false));
				
				this.orgEntity.goalSelector.addGoal(1, new AttackPatternGoal(this, 0.0F, true)
						.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA))
						.addAttack(new AttackInstance(1, 2.5F, 4.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_DA))
						.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST))
						.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO))
						.addAttack(new AttackInstance(2, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH)));
			}
			else if (category == WeaponCategory.SPEAR)
			{
				this.orgEntity.goalSelector.addGoal(2, new ChasingGoal(this, true));
				
				this.orgEntity.goalSelector.addGoal(1, new AttackPatternGoal(this, 0.0F, true)
						.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS))
						.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS))
						.addAttack(new AttackInstance(2, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH)));
			}
			this.orgEntity.goalSelector.addGoal(0, new StrafingGoal(this));
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
}
