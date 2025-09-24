package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.projectile.ProjectileCapability;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class ProjectileConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public ProjectileConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (ProjectileCapability.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save projectile config {}", path1, ioexception);
			}
		}
	}
	
	private static List<ProjectileCapability.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
				ProjectileCapability.builder(ModEntities.LIGHTNING_SPEAR.get(), 2F, 1F, StunType.HEAVY, true)
				.putDamage(CoreDamageType.PHYSICAL, 80F)
				.putDamage(CoreDamageType.LIGHTNING, 100F),
				ProjectileCapability.builder(ModEntities.GREAT_LIGHTNING_SPEAR.get(), 4F, 1F, StunType.HEAVY, true)
				.putDamage(CoreDamageType.PHYSICAL, 80F)
				.putDamage(CoreDamageType.LIGHTNING, 150F),
				
				ProjectileCapability.builder(ModEntities.FIREBOMB.get(), 2F, 1F, StunType.HEAVY, false)
				.putDamage(CoreDamageType.PHYSICAL, 80F)
				.putDamage(CoreDamageType.FIRE, 40F),
				ProjectileCapability.builder(ModEntities.BLACK_FIREBOMB.get(), 2F, 1F, StunType.HEAVY, false)
				.putDamage(CoreDamageType.PHYSICAL, 90F)
				.putDamage(CoreDamageType.FIRE, 70F)
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve(DSDefaultPackResources.ROOT_DIR_NAME+"/" + location.getNamespace() + "/projectiles/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "ProjectileConfig";
	}
}
