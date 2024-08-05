package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.RangedWeaponCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class RangedWeaponConfigs extends AbstractDSDataConfig
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public RangedWeaponConfigs()
	{
		super("weapon_configs/ranged");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		Collection<RangedWeaponCap.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				RangedWeaponCap.Builder builder = RangedWeaponCap.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading ranged weapon config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" ranged weapon configs");
		
		
		for (RangedWeaponCap.Builder builder : configs)
		{
			RangedWeaponCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
}
