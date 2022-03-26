package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public class InvincibleAnimation extends ActionAnimation
{
	public InvincibleAnimation(float convertTime, String path, String armature)
	{
		super(convertTime, false, path, armature);
	}
	
	@Override
	public LivingCap.EntityState getState(float time)
	{
		return LivingCap.EntityState.INVINCIBLE;
	}
}
