package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;

public class ThrowableConfigs extends DSJsonDataRegister<ThrowableCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	@Override
	public String getDirectory()
	{
		return "throwables";
	}
	
	@Override
	protected void finish(Set<ThrowableCap.Builder> builders)
	{
		builders.forEach(builder ->
		{
			ThrowableCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		});
	}
	
	@Override
	protected ThrowableCap.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return ThrowableCap.Builder.fromJson(location, json);
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
}
