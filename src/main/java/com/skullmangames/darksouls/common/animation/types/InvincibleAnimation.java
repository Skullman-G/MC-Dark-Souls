package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;

public class InvincibleAnimation extends ActionAnimation
{
	public InvincibleAnimation(ResourceLocation id, float convertTime, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, path, model, properties);
	}
	
	@Override
	public EntityState getState(float time)
	{
		return EntityState.INVINCIBLE;
	}
	
	public static class Builder extends ActionAnimation.Builder
	{
		public Builder(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
		{
			super(id, convertTime, path, model);
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
		}

		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.INVINCIBLE;
		}
		
		@Override
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			register.put(this.getId(), new InvincibleAnimation(this.id, this.convertTime, this.location, this.model, this.properties.build()));
		}
	}
}
