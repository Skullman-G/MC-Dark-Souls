package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;
import com.skullmangames.darksouls.common.entity.HollowLordranWarrior;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSReqSpawnInfo;

public class HollowLordranWarriorCap extends HumanoidCap<HollowLordranWarrior>
{
	@Override
	public void postInit()
	{
		super.postInit();
		
		if (!this.isClientSide())
		{
			if (!this.orgEntity.canPickUpLoot()) this.orgEntity.setCanPickUpLoot(this.isArmed());
		}
		else ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getId()));
	}
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.HOLLOW_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.HOLLOW_LORDRAN_WARRIOR_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.HOLLOW_LORDRAN_WARRIOR_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.HORSEBACK, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.BLOCKING, Animations.HOLLOW_LORDRAN_SOLDIER_BLOCK);
		animatorClient.addLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		this.orgEntity.goalSelector.addGoal(0, new DrinkingEstusGoal(this));
		
		if (category == WeaponCategory.STRAIGHT_SWORD)
		{
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LIGHT_ATTACKS))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_BARRAGE))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LORDRAN_WARRIOR_TH_LA))
					.addAttack(new AttackInstance(4, 6.0F, 7.0F, Animations.HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
		else if (category == WeaponCategory.AXE)
		{
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LORDRAN_WARRIOR_AXE_LA))
					.addAttack(new AttackInstance(4, 2.0F, Animations.HOLLOW_LORDRAN_WARRIOR_AXE_TH_LA))
					.addAttack(new AttackInstance(4, 6.0F, 7.0F, Animations.HOLLOW_LORDRAN_WARRIOR_DASH_ATTACK))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
}
