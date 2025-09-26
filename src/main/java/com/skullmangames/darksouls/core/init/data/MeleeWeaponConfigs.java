package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.ProviderItem;
import net.minecraft.resources.ResourceLocation;

public class MeleeWeaponConfigs extends AbstractDSDataRegister<MeleeWeaponCap.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public MeleeWeaponConfigs()
	{
		super("weapon_configs/melee");
	}
	
	@Override
	protected void finish(Set<MeleeWeaponCap.Builder> builders)
	{
		builders.forEach(builder ->
		{
			MeleeWeaponCap cap = builder.build();
			ProviderItem.CAPABILITIES.put(cap.getOriginalItem(), cap);
		});
	}
	
	@Override
	protected MeleeWeaponCap.Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return MeleeWeaponCap.Builder.fromJson(location, json);
	}
	
	@Override
	protected Logger getLogger()
	{
		return LOGGER;
	}
}
