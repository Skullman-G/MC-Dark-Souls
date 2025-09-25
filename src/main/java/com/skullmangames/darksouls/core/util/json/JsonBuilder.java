package com.skullmangames.darksouls.core.util.json;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public interface JsonBuilder<T>
{
	public ResourceLocation getId();
	
	public JsonObject toJson();
	
	public T build();
}
