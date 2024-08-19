package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class ArmorConfigs extends AbstractDSDataRegister
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ArmorConfigs()
	{
		super("armor_configs");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		Collection<ArmorCap.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				ArmorCap.Builder builder = ArmorCap.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading armor config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" armor configs");
		
		
		for (ArmorCap.Builder builder : configs)
		{
			ArmorCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
}
