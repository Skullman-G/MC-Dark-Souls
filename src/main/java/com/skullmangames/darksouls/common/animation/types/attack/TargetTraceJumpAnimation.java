package com.skullmangames.darksouls.common.animation.types.attack;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.LivingData.EntityState;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;

public class TargetTraceJumpAnimation extends AttackAnimation
{
	public TargetTraceJumpAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider, String index, float weaponOffset, String path, String armature)
	{
		this(convertTime, antic, preDelay, contact, recovery, affectY, Hand.MAIN_HAND, collider, index, weaponOffset, path, armature);
	}
	
	public TargetTraceJumpAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand, @Nullable Collider collider, String index, float weaponOffset, String path, String armature)
	{
		this(convertTime, affectY, path, armature, new TargetTracePhase(antic, preDelay, contact, recovery, hand, index, collider, weaponOffset));
	}
	
	public TargetTraceJumpAnimation(float convertTime, boolean affectY, String path, String armature, TargetTracePhase... phases)
	{
		super(convertTime, affectY, path, armature, phases);
	}
	
	@Override
	protected Vector3f getCoordVector(LivingData<?> entitydata)
	{
		float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
		EntityState state = this.getState(elapsedTime);
		Vector3f vec3 = super.getCoordVector(entitydata);
		if(state.getContactLevel() < 3)
		{
			LivingEntity orgEntity = entitydata.getOriginalEntity();
			LivingEntity target = entitydata.getTarget();
			float multiplier = (orgEntity instanceof PlayerEntity) ? 2.0F : 1.0F;
			
			if (target != null)
			{
				float colliderOffset = orgEntity.getBbWidth() - target.getBbWidth();
				float weaponOffset = this.getPhaseByTime(elapsedTime).weaponOffset;
				float offset = weaponOffset < colliderOffset ? colliderOffset : weaponOffset;
				float distance = Math.max(Math.min(orgEntity.distanceTo(target) - offset, multiplier), 0.0F);
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
	
	@Override
	public TargetTracePhase getPhaseByTime(float elapsedTime)
	{
		return (TargetTracePhase)super.getPhaseByTime(elapsedTime);
	}
	
	public static class TargetTracePhase extends Phase
	{
		protected final float weaponOffset;
		
		public TargetTracePhase(float antic, float preDelay, float contact, float recovery, Hand hand, String indexer, Collider collider, float weaponOffset)
		{
			super(antic, preDelay, contact, recovery, hand, indexer, collider);
			this.weaponOffset = weaponOffset;
		}
	}
}