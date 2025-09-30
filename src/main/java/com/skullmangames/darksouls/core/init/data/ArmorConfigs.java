package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.core.init.ProviderItem;
import net.minecraft.resources.ResourceLocation;

public class ArmorConfigs extends DSJsonDataRegister<ArmorCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	@Override
	public String getDirectory()
	{
		return "armor_configs";
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
	
	@Override
	protected void finish(Set<ArmorCap.Builder> builders)
	{
		builders.forEach(builder ->
		{
			ArmorCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		});
	}
	
	@Override
	protected ArmorCap.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return ArmorCap.Builder.fromJson(location, json);
	}
}
