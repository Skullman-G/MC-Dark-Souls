package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

public class TargetTraceJumpAnimation extends AttackAnimation
{
	public TargetTraceJumpAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, float weaponOffset, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, antic, preDelay, contact, recovery, InteractionHand.MAIN_HAND, collider, index, weaponOffset, path, model);
	}
	
	public TargetTraceJumpAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, String index, float weaponOffset, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, path, model, new TargetTracePhase(antic, preDelay, contact, recovery, hand, index, collider, weaponOffset));
	}
	
	public TargetTraceJumpAnimation(float convertTime, String path, Function<Models<?>, Model> model, TargetTracePhase... phases)
	{
		super(convertTime, path, model, phases);
	}
	
	@Override
	protected Vector3f getCoordVector(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		float elapsedTime = entityCap.getAnimator().getPlayerFor(animation).getElapsedTime();
		EntityState state = this.getState(elapsedTime);
		Vector3f vec3 = super.getCoordVector(entityCap, animation);
		if(state.getContactLevel() < 3)
		{
			LivingEntity orgEntity = entityCap.getOriginalEntity();
			LivingEntity target = entityCap.getTarget();
			float multiplier = (orgEntity instanceof Player) ? 2.0F : 1.0F;
			
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
		
		public TargetTracePhase(float antic, float preDelay, float contact, float recovery, InteractionHand hand, String indexer, Collider collider, float weaponOffset)
		{
			super(antic, preDelay, contact, recovery, hand, indexer, collider);
			this.weaponOffset = weaponOffset;
		}
	}
}