package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class HitAnimation extends ActionAnimation
{
	public HitAnimation(int id, float convertTime, String path, String armature)
	{
		super(id, convertTime, false, false, path, armature, false);
	}

	@Override
	public LivingData.EntityState getState(float time)
	{
		return LivingData.EntityState.HIT;
	}
}