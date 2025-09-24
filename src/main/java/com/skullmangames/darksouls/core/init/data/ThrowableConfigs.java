package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class ThrowableConfigs extends AbstractDSDataRegister
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ThrowableConfigs()
	{
		super("throwables");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		Collection<ThrowableCap.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				ThrowableCap.Builder builder = ThrowableCap.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading throwable config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" throwable configs");
		
		
		for (ThrowableCap.Builder builder : configs)
		{
			ThrowableCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
}
