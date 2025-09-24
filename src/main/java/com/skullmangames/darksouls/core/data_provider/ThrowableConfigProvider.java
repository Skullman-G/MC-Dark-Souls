package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class ThrowableConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public ThrowableConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (ThrowableCap.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save throwable config {}", path1, ioexception);
			}
		}
	}
	
	private static List<ThrowableCap.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
				ThrowableCap.builder(Items.SNOWBALL, EntityType.SNOWBALL, () -> SoundEvents.SNOWBALL_THROW),
				ThrowableCap.builder(Items.EGG, EntityType.EGG, () -> SoundEvents.EGG_THROW),
				
				ThrowableCap.builder(ModItems.FIREBOMB.get(), ModEntities.FIREBOMB.get(), () -> SoundEvents.SNOWBALL_THROW),
				ThrowableCap.builder(ModItems.BLACK_FIREBOMB.get(), ModEntities.BLACK_FIREBOMB.get(), () -> SoundEvents.SNOWBALL_THROW)
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve(DSDefaultPackResources.ROOT_DIR_NAME+"/" + location.getNamespace() + "/throwables/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "ThrowableConfig";
	}
}
