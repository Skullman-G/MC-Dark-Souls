package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.entity.AbstractFireKeeper;
import com.skullmangames.darksouls.core.init.Animations;

public class FireKeeperCap extends HumanoidCap<AbstractFireKeeper>
{
	@Override
	public void updateMotion()
	{
		super.commonMotionUpdate();
	}
	
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE.get());
		animatorClient.putLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK.get());
		animatorClient.putLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL.get());
		animatorClient.putLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE.get());
		animatorClient.setCurrentMotionsToDefault();
	}
}
