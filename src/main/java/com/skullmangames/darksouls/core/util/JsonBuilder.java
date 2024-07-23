package com.skullmangames.darksouls.core.util;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public interface JsonBuilder<T>
{
	public ResourceLocation getId();
	
	public void initFromJson(ResourceLocation location, JsonObject json);
	
	public JsonObject toJson();
	
	public T build();
}
