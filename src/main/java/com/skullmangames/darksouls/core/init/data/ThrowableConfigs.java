package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.core.init.ProviderItem;

import net.minecraft.resources.ResourceLocation;

public class ThrowableConfigs extends AbstractDSDataRegister<ThrowableCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ThrowableConfigs()
	{
		super("throwables");
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
	protected Logger getLogger()
	{
		return LOGGER;
	}
}
