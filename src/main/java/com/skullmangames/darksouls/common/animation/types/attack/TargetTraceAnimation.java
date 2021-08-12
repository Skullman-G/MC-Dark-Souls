package com.skullmangames.darksouls.common.animation.types.attack;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.LivingData.EntityState;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;

public class TargetTraceAnimation extends AttackAnimation
{
	public TargetTraceAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			@Nullable Collider collider, String index, String path)
	{
		this(id, convertTime, antic, preDelay, contact, recovery, affectY, Hand.MAIN_HAND, collider, index, path);
	}
	
	public TargetTraceAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			Hand hand, @Nullable Collider collider, String index, String path)
	{
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public TargetTraceAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases)
	{
		super(id, convertTime, affectY, path, phases);
	}
	
	@Override
	protected Vector3f getCoordVector(LivingData<?> entitydata)
	{
		EntityState state = this.getState(entitydata.getAnimator().getPlayer().getElapsedTime());
		Vector3f vec3 = super.getCoordVector(entitydata);
		if(state.getLevel() < 3)
		{
			LivingEntity orgEntity = entitydata.getOriginalEntity();
			LivingEntity target = entitydata.getTarget();
			float multiplier = (orgEntity instanceof PlayerEntity) ? 2.0F : 1.0F;
			
			if (target != null)
			{
				float distance = Math.max(Math.min(orgEntity.distanceTo(target) - orgEntity.getBbWidth() - target.getBbWidth(), multiplier), 0.0F);
				vec3.setX(vec3.x() * distance);
				vec3.setZ(vec3.z() * distance);
			}
			else
			{
				vec3.setX(vec3.x() * 0.5F);
				vec3.setZ(vec3.z() * 0.5F);
			}
		}
		
		return vec3;
	}
}