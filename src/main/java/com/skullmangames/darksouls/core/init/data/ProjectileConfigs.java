package com.skullmangames.darksouls.core.init.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;
import com.skullmangames.darksouls.core.init.ProviderProjectile;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class ProjectileConfigs extends AbstractDSDataRegister
{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public ProjectileConfigs()
	{
		super("projectiles");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager)
	{
		Collection<ProjectileCapability.Builder> configs = new HashSet<>();
		objects.forEach((location, json) ->
		{
			try
			{
				ProjectileCapability.Builder builder = ProjectileCapability.Builder.fromJson(location, json.getAsJsonObject());
				configs.add(builder);
			}
			catch (IllegalArgumentException | JsonParseException jsonparseexception)
			{
				LOGGER.error("Parsing error loading projectile config {}", location, jsonparseexception);
			}
		});
		LOGGER.info("Loaded "+configs.size()+" projectile configs");
		
		
		for (ProjectileCapability.Builder builder : configs)
		{
			ProviderProjectile.CAPABILITIES.put(builder.getEntityType(), builder::build);
		}
	}
}
