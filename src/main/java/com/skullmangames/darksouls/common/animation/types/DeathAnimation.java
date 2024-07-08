package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.DeathProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class DeathAnimation extends InvincibleAnimation
{
	public DeathAnimation(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
	{
		super(id, convertTime, path, model);
	}
	
	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		super.onStart(entityCap);
		entityCap.getOriginalEntity().deathTime = 0;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		entityCap.getOriginalEntity().deathTime = 0;
		
		if (entityCap.isClientSide())
		{
			float elapsedTime = entityCap.getAnimator().getPlayerFor(this).getElapsedTime();
			float disappearAt = this.getProperty(DeathProperty.DISAPPEAR_AT).orElse(this.getTotalTime());
			if (elapsedTime >= disappearAt)
			{
				float per = (elapsedTime - disappearAt);
				entityCap.setAlpha(Math.max(0, Mth.lerp(per, 1F, 0F)));
				
				LivingEntity entity = entityCap.getOriginalEntity();
				double d0 = entity.getRandom().nextGaussian() * 0.02D;
				double d1 = entity.getRandom().nextGaussian() * 0.02D;
				double d2 = entity.getRandom().nextGaussian() * 0.02D;
				entity.level.addParticle(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(),
						entity.getRandomZ(1.0D), d0, d1, d2);
			}
		}
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		entityCap.onDeath();
		entityCap.getOriginalEntity().deathTime = 19;
	}
	
	@Override
	public <V> DeathAnimation addProperty(Property<V> propertyType, V value)
	{
		return (DeathAnimation)super.addProperty(propertyType, value);
	}
	
	public static class Builder extends ActionAnimation.Builder
	{
		public Builder(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
		{
			super(id, convertTime, path, model);
		}
		
		public Builder(ResourceLocation id, JsonObject json)
		{
			super(id, json);
		}

		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.DEATH;
		}
		
		@Override
		public DeathAnimation build()
		{
			return new DeathAnimation(this.id, this.convertTime, this.location, this.model);
		}
	}
}
