package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellcastingWeaponCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class SpellcastingWeaponConfigs extends AbstractDSDataRegister
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public SpellcastingWeaponConfigs()
	{
		super("weapon_configs/spellcasting");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		Collection<SpellcastingWeaponCap.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				SpellcastingWeaponCap.Builder builder = SpellcastingWeaponCap.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading spellcasting weapon config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" spellcasting weapon configs");
		
		
		for (SpellcastingWeaponCap.Builder builder : configs)
		{
			SpellcastingWeaponCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
}
