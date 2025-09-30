package com.skullmangames.darksouls.core.init.data;

import java.util.Set;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;
import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability.Builder;
import com.skullmangames.darksouls.core.init.ProviderProjectile;
import net.minecraft.resources.ResourceLocation;

public class ProjectileConfigs extends DSJsonDataRegister<ProjectileCapability.Builder>
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	@Override
	public String getDirectory()
	{
		return "projectile";
	}
	
	@Override
	protected Builder builderFromJson(ResourceLocation location, JsonObject json)
	{
		return ProjectileCapability.Builder.fromJson(location, json);
	}
	
	@Override
	protected void finish(Set<ProjectileCapability.Builder> builders)
	{
		builders.forEach(builder ->
		{
			ProviderProjectile.CAPABILITIES.put(builder.getEntityType(), builder::build);
		});
	}
	
	@Override
	public Logger getLogger()
	{
		return LOGGER;
	}
}
