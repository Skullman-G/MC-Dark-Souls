package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.HollowLordranSoldier;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

import net.minecraft.resources.ResourceLocation;
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
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_LORDRAN_SOLDIER_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_LORDRAN_SOLDIER_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.BLOCKING, Animations.HOLLOW_LORDRAN_SOLDIER_BLOCK);
		animatorClient.addLivingAnimation(LivingMotion.AIMING, Animations.BIPED_CROSSBOW_AIM);
		animatorClient.addLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category, ResourceLocation moveset)
	{
		this.orgEntity.goalSelector.addGoal(0, new DrinkingEstusGoal(this));
		
		if (category == WeaponCategory.CROSSBOW && this.orgEntity instanceof CrossbowAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new CrossbowAttackGoal<HollowLordranSoldier, HollowLordranSoldierCap>(this));
		}
		else
		{
			if (moveset == WeaponMovesets.STRAIGHT_SWORD)
			{
				this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
						.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_LA))
						.addAttack(new AttackInstance(1, 2.5F, 4.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_DA))
						.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_HEAVY_THRUST))
						.addAttack(new AttackInstance(0, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SWORD_THRUST_COMBO))
						.addAttack(new AttackInstance(2, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH))
						.addDodge(Animations.BIPED_JUMP_BACK));
			}
			else if (moveset == WeaponMovesets.SPEAR)
			{
				this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 1.0F, true, true, true)
						.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_THRUSTS))
						.addAttack(new AttackInstance(0, 3.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SPEAR_SWINGS))
						.addAttack(new AttackInstance(2, 2.0F, Animations.HOLLOW_LORDRAN_SOLDIER_SHIELD_BASH))
						.addDodge(Animations.BIPED_JUMP_BACK));
			}
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
}
