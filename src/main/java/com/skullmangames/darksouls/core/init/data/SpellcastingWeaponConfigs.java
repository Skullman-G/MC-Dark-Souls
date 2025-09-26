package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellcastingWeaponCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;

public class SpellcastingWeaponConfigs extends AbstractDSDataRegister<SpellcastingWeaponCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public SpellcastingWeaponConfigs()
	{
		super("weapon_configs/spellcasting");
	}
	
	@Override
	protected void finish(Set<SpellcastingWeaponCap.Builder> builders)
	{
		for (SpellcastingWeaponCap.Builder builder : builders)
		{
			SpellcastingWeaponCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		}
	}
	
	@Override
	protected SpellcastingWeaponCap.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return SpellcastingWeaponCap.Builder.fromJson(location, json);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
}
