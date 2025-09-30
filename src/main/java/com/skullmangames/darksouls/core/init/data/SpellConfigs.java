package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;

public class SpellConfigs extends DSJsonDataRegister<SpellCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	@Override
	public String getDirectory()
	{
		return "spell_configs";
	}
	
	@Override
	protected void finish(Set<SpellCap.Builder> builders)
	{
		builders.forEach(builder ->
		{
			SpellCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		});
	}
	
	@Override
	protected SpellCap.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return SpellCap.Builder.fromJson(location, json);
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
}
