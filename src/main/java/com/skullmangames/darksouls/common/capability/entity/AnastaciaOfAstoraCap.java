package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

public class AnastaciaOfAstoraCap extends HumanoidCap<AnastaciaOfAstora>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ANASTACIA_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentMotionsToDefault();
	}

	@Override
	public void updateMotion()
	{
		this.currentMotion = LivingMotion.IDLE;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return Animations.ANASTACIA_IDLE;
	}
}
