package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap.Builder;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ParryAnimation extends ActionAnimation
{
	private final float start;
	private final float end;
	private final String jointName;
	
	public ParryAnimation(ResourceLocation id, float convertTime, float start, float end, String jointName, ResourceLocation path,
			Function<Models<?>, Model> model)
	{
		super(id, convertTime, path, model);
		this.start = start;
		this.end = end;
		this.jointName = jointName;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		
		if (!entityCap.isClientSide())
		{
			LivingEntity orgEntity = entityCap.getOriginalEntity();
			List<Entity> entities = entityCap.getLevel().getEntities(orgEntity, orgEntity.getBoundingBox().inflate(2.0D));
			
			for (Entity entity : entities)
			{
				if (entity instanceof LivingEntity livingEntity)
				{
					LivingCap<?> cap = (LivingCap<?>) livingEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					/*if (cap != null && cap.weaponCollider.)
					{
						
					}*/
				}
			}
			
			Collider collider = entityCap.getColliderMatching(InteractionHand.OFF_HAND);
			collider.updateAndFilterCollideEntity(entityCap, this.jointName, 1.0F);
		}
	}
	
	@Override
	public EntityState getState(float time)
	{
		if (time < this.start)
		{
			return EntityState.PRE_CONTACT;
		}
		else if (time < this.end)
		{
			return EntityState.CONTACT;
		}
		else return EntityState.POST_CONTACT;
	}
	
	@Override
	public ParryAnimation register(Builder<ResourceLocation, StaticAnimation> builder)
	{
		super.register(builder);
		return this;
	}
}
