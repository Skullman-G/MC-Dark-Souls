package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;

public class BlockedAnimation extends ActionAnimation
{
	public BlockedAnimation(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
	{
		super(id, convertTime, path, model);
	}
	
	@Override
	public EntityState getState(float time)
	{
		return EntityState.BLOCK;
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
			return AnimationType.BLOCKED;
		}
		
		@Override
		public BlockedAnimation build()
		{
			return new BlockedAnimation(this.id, this.convertTime, this.location, this.model);
		}
	}
}
