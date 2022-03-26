package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;

public class MovementAnimation extends StaticAnimation
{
	public MovementAnimation(float convertTime, boolean isRepeat, String path, String armature)
	{
		super(true, convertTime, isRepeat, path, armature, true);
	}

	public MovementAnimation(boolean repeatPlay, String path, String armature)
	{
		this(IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, armature);
	}
	
	@Override
	public Pose getPoseByTime(LivingCap<?> entitydata, float time)
	{
		if (entitydata.getAnimator().isReverse())
		{
			time = this.getTotalTime() - time;
		}
		return super.getPoseByTime(entitydata, time);
	}
	
	@Override
	public float getPlaySpeed(LivingCap<?> entitydata)
	{
		float movementSpeed = 1.0F;

		if (Math.abs(entitydata.getOriginalEntity().animationSpeed - entitydata.getOriginalEntity().animationSpeedOld) < 0.007F)
		{
			movementSpeed *= (entitydata.getOriginalEntity().animationSpeed * 1.16F);
		}

		return movementSpeed;
	}
}