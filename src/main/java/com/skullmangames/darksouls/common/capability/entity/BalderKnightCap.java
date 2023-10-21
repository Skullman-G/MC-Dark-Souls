package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.BalderKnight;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.common.entity.ai.goal.CrossbowAttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.DrinkingEstusGoal;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.world.entity.monster.CrossbowAttackMob;

public class BalderKnightCap extends HumanoidCap<BalderKnight>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BALDER_KNIGHT_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BALDER_KNIGHT_WALK);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BALDER_KNIGHT_RUN);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BALDER_KNIGHT_BLOCK);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		this.orgEntity.goalSelector.addGoal(0, new DrinkingEstusGoal(this));
		
		if (category == WeaponCategory.CROSSBOW && this.orgEntity instanceof CrossbowAttackMob)
		{
			this.orgEntity.goalSelector.addGoal(0, new CrossbowAttackGoal<BalderKnight, BalderKnightCap>(this));
		}
		else if (category == WeaponCategory.STRAIGHT_SWORD)
		{
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_LA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_HA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_DA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SHIELD_HA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_SIDE_SWORD_FAST_LA))
					.addDodge(Animations.BIPED_JUMP_BACK));
		}
		else if (category == WeaponCategory.THRUSTING_SWORD)
		{
			this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_RAPIER_LA))
					.addAttack(new AttackInstance(1, 2.0F, Animations.BALDER_KNIGHT_RAPIER_HA))
					.addAttack(new AttackInstance(1, 2.5F, 4.0F, Animations.BALDER_KNIGHT_RAPIER_DA))
					.addDodge(Animations.BIPED_JUMP_BACK)
					.addParry(Animations.BALDER_KNIGHT_RAPIER_BLOCK, Animations.BALDER_KNIGHT_RAPIER_PARRY));
		}
	}
	
	@Override
	public void onParrySuccess()
	{
		super.onParrySuccess();
		this.playAnimationSynchronized(Animations.PUNISH_THRUST, 0.0F);
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public int getSoulReward()
	{
		return 160;
	}
	
	@Override
	public ShieldHoldType getShieldHoldType()
	{
		return ShieldHoldType.VERTICAL_REVERSE;
	}
}
