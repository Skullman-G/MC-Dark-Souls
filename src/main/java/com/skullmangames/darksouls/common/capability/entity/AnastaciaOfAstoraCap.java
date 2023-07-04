package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;

public class AnastaciaOfAstoraCap extends HumanoidCap<AnastaciaOfAstora>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.putLivingAnimation(LivingMotion.IDLE, Animations.ANASTACIA_IDLE);
		animatorClient.setCurrentMotionsToDefault();
	}

	@Override
	public void updateMotion()
	{
		this.baseMotion = LivingMotion.IDLE;
	}
	
	@Override
	public StaticAnimation getHitAnimation(ExtendedDamageSource dmgSource)
	{
		return Animations.ANASTACIA_IDLE;
	}
}
