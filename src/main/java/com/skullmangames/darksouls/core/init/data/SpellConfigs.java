package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public class SpellConfigs extends SimpleJsonResourceReloadListener
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	
	public SpellConfigs()
	{
		super(GSON, "spell_configs");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profiler)
	{
		Collection<SpellCap.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				SpellCap.Builder builder = SpellCap.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading spell config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" spell configs");
		
		
		for (SpellCap.Builder builder : configs)
		{
			SpellCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
}
