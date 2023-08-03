package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.entity.BlackKnight;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackGoal;
import com.skullmangames.darksouls.common.entity.ai.goal.AttackInstance;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.WeaponCategory;

public class BlackKnightCap extends HumanoidCap<BlackKnight>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BLACK_KNIGHT_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BLACK_KNIGHT_WALKING);
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BLACK_KNIGHT_RUNNING);
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BLACK_KNIGHT_BLOCK);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		if (category != WeaponCategory.GREATSWORD) return;
		this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
				.addAttack(new AttackInstance(5, 4.0F, Animations.BLACK_KNIGHT_SWORD_LA_SHORT))
				.addAttack(new AttackInstance(4, 4.0F, Animations.BLACK_KNIGHT_SWORD_LA_LONG))
				.addAttack(new AttackInstance(6, 4.0F, Animations.BLACK_KNIGHT_SWORD_HA))
				.addAttack(new AttackInstance(1, 5.0F, Animations.BLACK_KNIGHT_SWORD_DA))
				.addAttack(new AttackInstance(2, 4.0F, Animations.BLACK_KNIGHT_SHIELD_ATTACK))
				.addDodge(Animations.BIPED_JUMP_BACK));
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public ShieldHoldType getShieldHoldType()
	{
		return ShieldHoldType.VERTICAL_REVERSE;
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.BLACK_KNIGHT_DEATH;
	}
}
