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

import net.minecraft.world.InteractionHand;

public class BlackKnightCap extends HumanoidCap<BlackKnight>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BLACK_KNIGHT_IDLE.get());
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BLACK_KNIGHT_WALK.get());
		animatorClient.putLivingAnimation(LivingMotion.RUNNING, Animations.BLACK_KNIGHT_RUN.get());
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL.get());
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE.get());
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BLACK_KNIGHT_BLOCK.get());
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK.get());
		animatorClient.setCurrentMotionsToDefault();
	}
	
	@Override
	public void setAttackGoals(WeaponCategory category)
	{
		if (category != WeaponCategory.GREATSWORD) return;
		this.orgEntity.goalSelector.addGoal(1, new AttackGoal(this, 0.0F, true, false, true)
				.addAttack(new AttackInstance(5, 4.0F, Animations.BLACK_KNIGHT_SWORD_LA_SHORT.get()))
				.addAttack(new AttackInstance(4, 4.0F, Animations.BLACK_KNIGHT_SWORD_LA_LONG.get()))
				.addAttack(new AttackInstance(6, 4.0F, Animations.BLACK_KNIGHT_SWORD_HA.get()))
				.addAttack(new AttackInstance(1, 5.0F, Animations.BLACK_KNIGHT_SWORD_DA.get()))
				.addAttack(new AttackInstance(2, 4.0F, Animations.BLACK_KNIGHT_SHIELD_ATTACK.get()))
				.addDodge(Animations.BIPED_JUMP_BACK.get()));
	}
	
	@Override
	public int getSoulReward()
	{
		return 1800;
	}

	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public ShieldHoldType getShieldHoldType(InteractionHand hand)
	{
		return ShieldHoldType.VERTICAL_REVERSE;
	}
	
	@Override
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.BLACK_KNIGHT_DEATH.get();
	}
}
