package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.RangedWeaponCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;

public class RangedWeaponConfigs extends AbstractDSDataRegister<RangedWeaponCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public RangedWeaponConfigs()
	{
		super("weapon_configs/ranged");
	}
	
	@Override
	protected void finish(Set<RangedWeaponCap.Builder> builders)
	{
		builders.forEach(builder ->
		{
			RangedWeaponCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		});
	}
	
	@Override
	protected RangedWeaponCap.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return RangedWeaponCap.Builder.fromJson(location, json);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
}
