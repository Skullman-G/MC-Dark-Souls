package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class MeleeWeaponConfigs extends AbstractDSDataConfig
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public MeleeWeaponConfigs()
	{
		super("weapon_configs/melee");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		Collection<MeleeWeaponCap.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				MeleeWeaponCap.Builder builder = MeleeWeaponCap.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading melee weapon config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" melee weapon configs");
		
		
		for (MeleeWeaponCap.Builder builder : configs)
		{
			MeleeWeaponCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
}
