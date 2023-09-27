package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.BalderKnight;
import com.skullmangames.darksouls.core.init.Animations;

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
		animatorClient.putLivingAnimation(LivingMotion.BLOCKING, Animations.BIPED_BLOCK_HORIZONTAL);
		animatorClient.putLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.setCurrentMotionsToDefault();
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
