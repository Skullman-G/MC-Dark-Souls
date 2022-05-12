package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Models;

public class MovementAnimation extends StaticAnimation
{
	public MovementAnimation(float convertTime, boolean isRepeat, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, isRepeat, path, model);
	}

	public MovementAnimation(boolean repeatPlay, String path, Function<Models<?>, Model> model)
	{
		this(IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, model);
	}
	
	@Override
	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		if (entityCap.getAnimator().isReverse())
		{
			time = this.getTotalTime() - time;
		}
		return super.getPoseByTime(entityCap, time, partialTicks);
	}
	
	@Override
	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		float movementSpeed = 1.0F;

		if (Math.abs(entityCap.getOriginalEntity().animationSpeed - entityCap.getOriginalEntity().animationSpeedOld) < 0.007F)
		{
			movementSpeed *= (entityCap.getOriginalEntity().animationSpeed * 1.16F);
		}

		return movementSpeed;
	}
}