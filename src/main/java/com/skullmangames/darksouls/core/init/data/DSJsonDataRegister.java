package com.skullmangames.darksouls.core.init.data;

import java.io.BufferedReader;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public abstract class DSJsonDataRegister<B extends JsonBuilder<?>> extends DSDataRegister<B>
{
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	
	@Override
	public String getPathSuffix()
	{
		return ".json";
	}
	
	protected abstract void finish(Set<B> builders);
	
	protected abstract B builderFromJson(ResourceLocation location, JsonObject json);
	
	@Override
	protected B parseFromFile(ResourceLocation fileID, BufferedReader reader) throws Exception
	{
		JsonObject jsonObject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
		return this.builderFromJson(fileID, jsonObject);
	}
}
