package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class SpellConfigs extends AbstractDSDataConfig
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public SpellConfigs()
	{
		super("spell_configs");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
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
