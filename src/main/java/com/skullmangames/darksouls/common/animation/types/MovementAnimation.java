package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.config.IngameConfig;

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
		this(id, IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path);
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