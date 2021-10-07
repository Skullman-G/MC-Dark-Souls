package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class DeathAnimation extends HitAnimation
{
	public DeathAnimation(int id, float convertTime, String path, String armature)
	{
		super(id, convertTime, path, armature);
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata)
	{
		if (entitydata.getOriginalEntity().deathTime > 0) entitydata.getOriginalEntity().deathTime = 0;
		super.onUpdate(entitydata);
	}
}
