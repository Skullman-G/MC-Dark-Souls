package com.skullmangames.darksouls.common.animation.types.attack;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.util.physics.Collider;

public class MountAttackAnimation extends AttackAnimation
{
	public MountAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, String index, String path, String armature)
	{
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, armature);
	}
	
	@Override
	protected Vector3f getCoordVector(LivingData<?> entitydata)
	{
		return new Vector3f(0, 0, 0);
	}
}
