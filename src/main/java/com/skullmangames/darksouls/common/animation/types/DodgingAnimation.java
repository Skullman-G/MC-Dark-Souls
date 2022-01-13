package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class DodgingAnimation extends ActionAnimation
{
	public DodgingAnimation(float convertTime, boolean affectVelocity, String path, float width, float height, String armature)
	{
		this(convertTime, 0.0F, affectVelocity, path, width, height, armature);
	}
	
	public DodgingAnimation(float convertTime, float delayTime, boolean affectVelocity, String path, float width, float height, String armature)
	{
		super(convertTime, delayTime, affectVelocity, path, armature);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return 1.0F;
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		if(time < this.delayTime)
		{
			return LivingData.EntityState.PRE_DELAY;
		}
		else
		{
			return LivingData.EntityState.INVINCIBLE;
		}
	}
}