package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.AnastaciaOfAstora;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.world.entity.Entity;

public class AnastaciaOfAstoraCap extends HumanoidCap<AnastaciaOfAstora>
{
	@Override
	public void initAnimator(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ANASTACIA_IDLE);
		animatorClient.setCurrentMotionsToDefault();
	}

	@Override
	public void updateMotion()
	{
		this.currentMotion = LivingMotion.IDLE;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType, Entity attacker)
	{
		return Animations.ANASTACIA_IDLE;
	}
}
