package com.skullmangames.darksouls.common.animation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.core.util.JsonBuilder;

import net.minecraft.resources.ResourceLocation;

public abstract class AnimBuilder implements JsonBuilder<StaticAnimation>
{
	public AnimBuilder() {}
	
	public AnimBuilder(ResourceLocation id, JsonObject json) {}
	
	public abstract ResourceLocation getId();
	public abstract JsonObject toJson();
	
	public abstract AnimationType getAnimType();
	
	public abstract void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register);
	
	/**
	 * returns null
	 */
	@Override
	public StaticAnimation build()
	{
		return null;
	}
}
