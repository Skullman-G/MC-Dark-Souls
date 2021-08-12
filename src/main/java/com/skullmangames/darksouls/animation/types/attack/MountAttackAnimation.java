package com.skullmangames.darksouls.animation.types.attack;

import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.physics.Collider;

import net.minecraft.util.math.vector.Vector3f;

public class MountAttackAnimation extends AttackAnimation
{
	public MountAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, String index, String path)
	{
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
	}
	
	protected Vector3f getCoordVector(LivingData<?> entitydata)
	{
		return new Vector3f(0, 0, 0);
	}
}
