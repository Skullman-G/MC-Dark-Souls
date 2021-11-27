package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class DisarmAnimation extends ActionAnimation
{
	public DisarmAnimation(float convertTime, String path, String armature)
	{
		super(convertTime, false, path, armature);
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.DISARMED;
	}
}
