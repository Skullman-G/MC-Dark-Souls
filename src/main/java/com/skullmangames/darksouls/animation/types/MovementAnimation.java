package com.skullmangames.darksouls.animation.types;

import com.skullmangames.darksouls.animation.Pose;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.config.ConfigurationIngame;

public class MovementAnimation extends StaticAnimation
{
	public MovementAnimation(int id, float convertTime, boolean isRepeat, String path)
	{
		super(id, convertTime, isRepeat, path);
	}

	public MovementAnimation(String path)
	{
		super(path);
	}

	public MovementAnimation(float convertTime, boolean repeatPlay, String path)
	{
		super(convertTime, repeatPlay, path);
	}

	public MovementAnimation(int id, boolean repeatPlay, String path)
	{
		this(id, ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path);
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		if (entitydata.getAnimator().isReverse())
		{
			time = this.getTotalTime() - time;
		}
		return super.getPoseByTime(entitydata, time);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		float movementSpeed = 1.0F;

		if (Math.abs(entitydata.getOriginalEntity().animationSpeed - entitydata.getOriginalEntity().animationSpeedOld) < 0.007F)
		{
			movementSpeed *= (entitydata.getOriginalEntity().animationSpeed * 1.16F);
		}

		return movementSpeed;
	}
}