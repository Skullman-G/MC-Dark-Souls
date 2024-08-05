package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.ArmorCap;
import com.skullmangames.darksouls.common.capability.item.ArmorCap.ArmorDefenseType;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class ArmorConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public ArmorConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (ArmorCap.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getLocation());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save armor config {}", path1, ioexception);
			}
		}
	}
	
	private static List<ArmorCap.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
				ArmorCap.builder(ModItems.DINGY_HOOD.get(), 0.8F, 0F)
				.putDefense(ArmorDefenseType.REGULAR, 6F)
				.putDefense(ArmorDefenseType.STRIKE, 7.5F)
				.putDefense(ArmorDefenseType.SLASH, 6F)
				.putDefense(ArmorDefenseType.THRUST, 6F)
				.putDefense(ArmorDefenseType.MAGIC, 8F)
				.putDefense(ArmorDefenseType.FIRE, 5F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 8F)
				.putDefense(ArmorDefenseType.DARK, 8F),
				ArmorCap.builder(ModItems.DINGY_ROBE.get(), 3F, 0F)
				.putDefense(ArmorDefenseType.REGULAR, 23F)
				.putDefense(ArmorDefenseType.STRIKE, 28.7F)
				.putDefense(ArmorDefenseType.SLASH, 23F)
				.putDefense(ArmorDefenseType.THRUST, 23F)
				.putDefense(ArmorDefenseType.MAGIC, 33F)
				.putDefense(ArmorDefenseType.FIRE, 19F)
				.putDefense(ArmorDefenseType.LIGHTNING, 25F)
				.putDefense(ArmorDefenseType.HOLY, 33F)
				.putDefense(ArmorDefenseType.DARK, 33F),
				ArmorCap.builder(ModItems.BLOOD_STAINED_SKIRT.get(), 1.4F, 0F)
				.putDefense(ArmorDefenseType.REGULAR, 13F)
				.putDefense(ArmorDefenseType.STRIKE, 16.3F)
				.putDefense(ArmorDefenseType.SLASH, 13F)
				.putDefense(ArmorDefenseType.THRUST, 13F)
				.putDefense(ArmorDefenseType.MAGIC, 27F)
				.putDefense(ArmorDefenseType.FIRE, 16F)
				.putDefense(ArmorDefenseType.LIGHTNING, 21F)
				.putDefense(ArmorDefenseType.HOLY, 27F)
				.putDefense(ArmorDefenseType.DARK, 27F),
				ArmorCap.builder(ModItems.LORDRAN_SOLDIER_HELM.get(), 3F, 5F)
				.putDefense(ArmorDefenseType.REGULAR, 10F)
				.putDefense(ArmorDefenseType.STRIKE, 10F)
				.putDefense(ArmorDefenseType.SLASH, 11.1F)
				.putDefense(ArmorDefenseType.THRUST, 8.8F)
				.putDefense(ArmorDefenseType.MAGIC, 6F)
				.putDefense(ArmorDefenseType.FIRE, 6F)
				.putDefense(ArmorDefenseType.LIGHTNING, 4F)
				.putDefense(ArmorDefenseType.HOLY, 6F)
				.putDefense(ArmorDefenseType.DARK, 6F),
				ArmorCap.builder(ModItems.LORDRAN_SOLDIER_ARMOR.get(), 7.8F, 12F)
				.putDefense(ArmorDefenseType.REGULAR, 26F)
				.putDefense(ArmorDefenseType.STRIKE, 26F)
				.putDefense(ArmorDefenseType.SLASH, 28.9F)
				.putDefense(ArmorDefenseType.THRUST, 22.9F)
				.putDefense(ArmorDefenseType.MAGIC, 16F)
				.putDefense(ArmorDefenseType.FIRE, 17F)
				.putDefense(ArmorDefenseType.LIGHTNING, 10F)
				.putDefense(ArmorDefenseType.HOLY, 16F)
				.putDefense(ArmorDefenseType.DARK, 16F),
				ArmorCap.builder(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get(), 1.5F, 7F)
				.putDefense(ArmorDefenseType.REGULAR, 13F)
				.putDefense(ArmorDefenseType.STRIKE, 13F)
				.putDefense(ArmorDefenseType.SLASH, 14.4F)
				.putDefense(ArmorDefenseType.THRUST, 11.4F)
				.putDefense(ArmorDefenseType.MAGIC, 8F)
				.putDefense(ArmorDefenseType.FIRE, 8F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 8F)
				.putDefense(ArmorDefenseType.DARK, 8F),
				ArmorCap.builder(ModItems.LORDRAN_SOLDIER_BOOTS.get(), 1F, 2F)
				.putDefense(ArmorDefenseType.REGULAR, 13F)
				.putDefense(ArmorDefenseType.STRIKE, 13F)
				.putDefense(ArmorDefenseType.SLASH, 14.4F)
				.putDefense(ArmorDefenseType.THRUST, 11.4F)
				.putDefense(ArmorDefenseType.MAGIC, 8F)
				.putDefense(ArmorDefenseType.FIRE, 8F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 8F)
				.putDefense(ArmorDefenseType.DARK, 8F),
				ArmorCap.builder(ModItems.LORDRAN_WARRIOR_HELM.get(), 2.6F, 3F)
				.putDefense(ArmorDefenseType.REGULAR, 10F)
				.putDefense(ArmorDefenseType.STRIKE, 9.7F)
				.putDefense(ArmorDefenseType.SLASH, 10.3F)
				.putDefense(ArmorDefenseType.THRUST, 9.5F)
				.putDefense(ArmorDefenseType.MAGIC, 6F)
				.putDefense(ArmorDefenseType.FIRE, 5F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 6F)
				.putDefense(ArmorDefenseType.DARK, 6F),
				ArmorCap.builder(ModItems.LORDRAN_WARRIOR_ARMOR.get(), 6.6F, 8F)
				.putDefense(ArmorDefenseType.REGULAR, 24F)
				.putDefense(ArmorDefenseType.STRIKE, 23.3F)
				.putDefense(ArmorDefenseType.SLASH, 24.7F)
				.putDefense(ArmorDefenseType.THRUST, 22.8F)
				.putDefense(ArmorDefenseType.MAGIC, 16F)
				.putDefense(ArmorDefenseType.FIRE, 12F)
				.putDefense(ArmorDefenseType.LIGHTNING, 17F)
				.putDefense(ArmorDefenseType.HOLY, 16F)
				.putDefense(ArmorDefenseType.DARK, 16F),
				ArmorCap.builder(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get(), 1.4F, 5F)
				.putDefense(ArmorDefenseType.REGULAR, 13F)
				.putDefense(ArmorDefenseType.STRIKE, 12.5F)
				.putDefense(ArmorDefenseType.SLASH, 13.4F)
				.putDefense(ArmorDefenseType.THRUST, 12.3F)
				.putDefense(ArmorDefenseType.MAGIC, 8F)
				.putDefense(ArmorDefenseType.FIRE, 6F)
				.putDefense(ArmorDefenseType.LIGHTNING, 8F)
				.putDefense(ArmorDefenseType.HOLY, 8F)
				.putDefense(ArmorDefenseType.DARK, 8F),
				ArmorCap.builder(ModItems.LORDRAN_WARRIOR_BOOTS.get(), 1F, 0F)
				.putDefense(ArmorDefenseType.REGULAR, 13F)
				.putDefense(ArmorDefenseType.STRIKE, 13.8F)
				.putDefense(ArmorDefenseType.SLASH, 13F)
				.putDefense(ArmorDefenseType.THRUST, 13F)
				.putDefense(ArmorDefenseType.MAGIC, 12F)
				.putDefense(ArmorDefenseType.FIRE, 7F)
				.putDefense(ArmorDefenseType.LIGHTNING, 13F)
				.putDefense(ArmorDefenseType.HOLY, 12F)
				.putDefense(ArmorDefenseType.DARK, 12F),
				ArmorCap.builder(ModItems.ELITE_CLERIC_HELM.get(), 4.8F, 8F)
				.putDefense(ArmorDefenseType.REGULAR, 15F)
				.putDefense(ArmorDefenseType.STRIKE, 15F)
				.putDefense(ArmorDefenseType.SLASH, 17.2F)
				.putDefense(ArmorDefenseType.THRUST, 15F)
				.putDefense(ArmorDefenseType.MAGIC, 9F)
				.putDefense(ArmorDefenseType.FIRE, 8F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 9F)
				.putDefense(ArmorDefenseType.DARK, 9F),
				ArmorCap.builder(ModItems.ELITE_CLERIC_ARMOR.get(), 12.5F, 20F)
				.putDefense(ArmorDefenseType.REGULAR, 40F)
				.putDefense(ArmorDefenseType.STRIKE, 40F)
				.putDefense(ArmorDefenseType.SLASH, 46F)
				.putDefense(ArmorDefenseType.THRUST, 40F)
				.putDefense(ArmorDefenseType.MAGIC, 23F)
				.putDefense(ArmorDefenseType.FIRE, 21F)
				.putDefense(ArmorDefenseType.LIGHTNING, 15F)
				.putDefense(ArmorDefenseType.HOLY, 23F)
				.putDefense(ArmorDefenseType.DARK, 23F),
				ArmorCap.builder(ModItems.ELITE_CLERIC_LEGGINGS.get(), 7.4F, 12F)
				.putDefense(ArmorDefenseType.REGULAR, 24F)
				.putDefense(ArmorDefenseType.STRIKE, 24F)
				.putDefense(ArmorDefenseType.SLASH, 27.6F)
				.putDefense(ArmorDefenseType.THRUST, 24F)
				.putDefense(ArmorDefenseType.MAGIC, 13F)
				.putDefense(ArmorDefenseType.FIRE, 12F)
				.putDefense(ArmorDefenseType.LIGHTNING, 9F)
				.putDefense(ArmorDefenseType.HOLY, 13F)
				.putDefense(ArmorDefenseType.DARK, 13F),
				ArmorCap.builder(ModItems.FALCONER_HELM.get(), 4.5F, 6F)
				.putDefense(ArmorDefenseType.REGULAR, 43F)
				.putDefense(ArmorDefenseType.STRIKE, 38F)
				.putDefense(ArmorDefenseType.SLASH, 47F)
				.putDefense(ArmorDefenseType.THRUST, 44F)
				.putDefense(ArmorDefenseType.MAGIC, 13F)
				.putDefense(ArmorDefenseType.FIRE, 19F)
				.putDefense(ArmorDefenseType.LIGHTNING, 7F)
				.putDefense(ArmorDefenseType.HOLY, 13F)
				.putDefense(ArmorDefenseType.DARK, 13F),
				ArmorCap.builder(ModItems.FALCONER_ARMOR.get(), 7.7F, 12F)
				.putDefense(ArmorDefenseType.REGULAR, 74F)
				.putDefense(ArmorDefenseType.STRIKE, 78F)
				.putDefense(ArmorDefenseType.SLASH, 71F)
				.putDefense(ArmorDefenseType.THRUST, 71F)
				.putDefense(ArmorDefenseType.MAGIC, 22F)
				.putDefense(ArmorDefenseType.FIRE, 19F)
				.putDefense(ArmorDefenseType.LIGHTNING, 25F)
				.putDefense(ArmorDefenseType.HOLY, 22F)
				.putDefense(ArmorDefenseType.DARK, 22F),
				ArmorCap.builder(ModItems.FALCONER_LEGGINGS.get(), 5.8F, 8F)
				.putDefense(ArmorDefenseType.REGULAR, 55F)
				.putDefense(ArmorDefenseType.STRIKE, 59F)
				.putDefense(ArmorDefenseType.SLASH, 54F)
				.putDefense(ArmorDefenseType.THRUST, 54F)
				.putDefense(ArmorDefenseType.MAGIC, 17F)
				.putDefense(ArmorDefenseType.FIRE, 14F)
				.putDefense(ArmorDefenseType.LIGHTNING, 19F)
				.putDefense(ArmorDefenseType.HOLY, 17F)
				.putDefense(ArmorDefenseType.DARK, 17F),
				ArmorCap.builder(ModItems.FALCONER_BOOTS.get(), 3.2F, 4F)
				.putDefense(ArmorDefenseType.REGULAR, 35F)
				.putDefense(ArmorDefenseType.STRIKE, 39F)
				.putDefense(ArmorDefenseType.SLASH, 34F)
				.putDefense(ArmorDefenseType.THRUST, 34F)
				.putDefense(ArmorDefenseType.MAGIC, 7F)
				.putDefense(ArmorDefenseType.FIRE, 4F)
				.putDefense(ArmorDefenseType.LIGHTNING, 9F)
				.putDefense(ArmorDefenseType.HOLY, 7F)
				.putDefense(ArmorDefenseType.DARK, 7F),
				ArmorCap.builder(ModItems.BLACK_KNIGHT_HELM.get(), 5F, 8F)
				.putDefense(ArmorDefenseType.REGULAR, 21F)
				.putDefense(ArmorDefenseType.STRIKE, 19.9F)
				.putDefense(ArmorDefenseType.SLASH, 23.7F)
				.putDefense(ArmorDefenseType.THRUST, 20.6F)
				.putDefense(ArmorDefenseType.MAGIC, 11F)
				.putDefense(ArmorDefenseType.FIRE, 18F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 11F)
				.putDefense(ArmorDefenseType.DARK, 11F),
				ArmorCap.builder(ModItems.BLACK_KNIGHT_ARMOR.get(), 13F, 21F)
				.putDefense(ArmorDefenseType.REGULAR, 57F)
				.putDefense(ArmorDefenseType.STRIKE, 54.2F)
				.putDefense(ArmorDefenseType.SLASH, 64.4F)
				.putDefense(ArmorDefenseType.THRUST, 55.9F)
				.putDefense(ArmorDefenseType.MAGIC, 29F)
				.putDefense(ArmorDefenseType.FIRE, 48F)
				.putDefense(ArmorDefenseType.LIGHTNING, 17F)
				.putDefense(ArmorDefenseType.HOLY, 29F)
				.putDefense(ArmorDefenseType.DARK, 29F),
				ArmorCap.builder(ModItems.BLACK_KNIGHT_LEGGINGS.get(), 7F, 17F)
				.putDefense(ArmorDefenseType.REGULAR, 30F)
				.putDefense(ArmorDefenseType.STRIKE, 28.5F)
				.putDefense(ArmorDefenseType.SLASH, 33.9F)
				.putDefense(ArmorDefenseType.THRUST, 29.4F)
				.putDefense(ArmorDefenseType.MAGIC, 15F)
				.putDefense(ArmorDefenseType.FIRE, 26F)
				.putDefense(ArmorDefenseType.LIGHTNING, 9F)
				.putDefense(ArmorDefenseType.HOLY, 15F)
				.putDefense(ArmorDefenseType.DARK, 15F),
				ArmorCap.builder(ModItems.BALDER_HELM.get(), 4.2F, 6F)
				.putDefense(ArmorDefenseType.REGULAR, 14F)
				.putDefense(ArmorDefenseType.STRIKE, 13.7F)
				.putDefense(ArmorDefenseType.SLASH, 17.1F)
				.putDefense(ArmorDefenseType.THRUST, 13.4F)
				.putDefense(ArmorDefenseType.MAGIC, 6F)
				.putDefense(ArmorDefenseType.FIRE, 8F)
				.putDefense(ArmorDefenseType.LIGHTNING, 6F)
				.putDefense(ArmorDefenseType.HOLY, 6F)
				.putDefense(ArmorDefenseType.DARK, 6F),
				ArmorCap.builder(ModItems.BALDER_ARMOR.get(), 10.9F, 16F)
				.putDefense(ArmorDefenseType.REGULAR, 37F)
				.putDefense(ArmorDefenseType.STRIKE, 36.3F)
				.putDefense(ArmorDefenseType.SLASH, 45.1F)
				.putDefense(ArmorDefenseType.THRUST, 35.5F)
				.putDefense(ArmorDefenseType.MAGIC, 15F)
				.putDefense(ArmorDefenseType.FIRE, 18F)
				.putDefense(ArmorDefenseType.LIGHTNING, 15F)
				.putDefense(ArmorDefenseType.HOLY, 15F)
				.putDefense(ArmorDefenseType.DARK, 15F),
				ArmorCap.builder(ModItems.BALDER_LEGGINGS.get(), 6.4F, 9F)
				.putDefense(ArmorDefenseType.REGULAR, 22F)
				.putDefense(ArmorDefenseType.STRIKE, 21.6F)
				.putDefense(ArmorDefenseType.SLASH, 26.8F)
				.putDefense(ArmorDefenseType.THRUST, 21.1F)
				.putDefense(ArmorDefenseType.MAGIC, 12F)
				.putDefense(ArmorDefenseType.FIRE, 14F)
				.putDefense(ArmorDefenseType.LIGHTNING, 9F)
				.putDefense(ArmorDefenseType.HOLY, 12F)
				.putDefense(ArmorDefenseType.DARK, 12F),
				ArmorCap.builder(ModItems.BALDER_BOOTS.get(), 3.5F, 5F)
				.putDefense(ArmorDefenseType.REGULAR, 12F)
				.putDefense(ArmorDefenseType.STRIKE, 11.6F)
				.putDefense(ArmorDefenseType.SLASH, 16.8F)
				.putDefense(ArmorDefenseType.THRUST, 11.1F)
				.putDefense(ArmorDefenseType.MAGIC, 7F)
				.putDefense(ArmorDefenseType.FIRE, 12F)
				.putDefense(ArmorDefenseType.LIGHTNING, 4F)
				.putDefense(ArmorDefenseType.HOLY, 7F)
				.putDefense(ArmorDefenseType.DARK, 7F),
				ArmorCap.builder(ModItems.BURNT_SHIRT.get(), 0.8F, 0F)
				.putDefense(ArmorDefenseType.REGULAR, 12F)
				.putDefense(ArmorDefenseType.STRIKE, 11.6F)
				.putDefense(ArmorDefenseType.SLASH, 16.8F)
				.putDefense(ArmorDefenseType.THRUST, 11.1F)
				.putDefense(ArmorDefenseType.MAGIC, 7F)
				.putDefense(ArmorDefenseType.FIRE, 12F)
				.putDefense(ArmorDefenseType.LIGHTNING, 4F)
				.putDefense(ArmorDefenseType.HOLY, 7F)
				.putDefense(ArmorDefenseType.DARK, 7F),
				ArmorCap.builder(ModItems.BURNT_TROUSERS.get(), 0.8F, 0F)
				.putDefense(ArmorDefenseType.REGULAR, 12F)
				.putDefense(ArmorDefenseType.STRIKE, 11.6F)
				.putDefense(ArmorDefenseType.SLASH, 16.8F)
				.putDefense(ArmorDefenseType.THRUST, 11.1F)
				.putDefense(ArmorDefenseType.MAGIC, 7F)
				.putDefense(ArmorDefenseType.FIRE, 12F)
				.putDefense(ArmorDefenseType.LIGHTNING, 4F)
				.putDefense(ArmorDefenseType.HOLY, 7F)
				.putDefense(ArmorDefenseType.DARK, 7F),
				ArmorCap.builder(ModItems.FANG_BOAR_HELM.get(), 8F, 12F)
				.putDefense(ArmorDefenseType.REGULAR, 26F)
				.putDefense(ArmorDefenseType.STRIKE, 23.4F)
				.putDefense(ArmorDefenseType.SLASH, 31.2F)
				.putDefense(ArmorDefenseType.THRUST, 26F)
				.putDefense(ArmorDefenseType.MAGIC, 5F)
				.putDefense(ArmorDefenseType.FIRE, 4F)
				.putDefense(ArmorDefenseType.LIGHTNING, 4F)
				.putDefense(ArmorDefenseType.HOLY, 5F)
				.putDefense(ArmorDefenseType.DARK, 5F),
				ArmorCap.builder(ModItems.BERENIKE_HELM.get(), 5.4F, 9F)
				.putDefense(ArmorDefenseType.REGULAR, 16F)
				.putDefense(ArmorDefenseType.STRIKE, 15F)
				.putDefense(ArmorDefenseType.SLASH, 18.4F)
				.putDefense(ArmorDefenseType.THRUST, 18.4F)
				.putDefense(ArmorDefenseType.MAGIC, 8F)
				.putDefense(ArmorDefenseType.FIRE, 8F)
				.putDefense(ArmorDefenseType.LIGHTNING, 5F)
				.putDefense(ArmorDefenseType.HOLY, 8F)
				.putDefense(ArmorDefenseType.DARK, 8F),
				ArmorCap.builder(ModItems.BERENIKE_ARMOR.get(), 14F, 23F)
				.putDefense(ArmorDefenseType.REGULAR, 42F)
				.putDefense(ArmorDefenseType.STRIKE, 39.5F)
				.putDefense(ArmorDefenseType.SLASH, 48.3F)
				.putDefense(ArmorDefenseType.THRUST, 42F)
				.putDefense(ArmorDefenseType.MAGIC, 22F)
				.putDefense(ArmorDefenseType.FIRE, 21F)
				.putDefense(ArmorDefenseType.LIGHTNING, 13F)
				.putDefense(ArmorDefenseType.HOLY, 22F)
				.putDefense(ArmorDefenseType.DARK, 22F),
				ArmorCap.builder(ModItems.BERENIKE_LEGGINGS.get(), 8.3F, 14F)
				.putDefense(ArmorDefenseType.REGULAR, 25F)
				.putDefense(ArmorDefenseType.STRIKE, 23.5F)
				.putDefense(ArmorDefenseType.SLASH, 28.8F)
				.putDefense(ArmorDefenseType.THRUST, 25F)
				.putDefense(ArmorDefenseType.MAGIC, 13F)
				.putDefense(ArmorDefenseType.FIRE, 12F)
				.putDefense(ArmorDefenseType.LIGHTNING, 8F)
				.putDefense(ArmorDefenseType.HOLY, 13F)
				.putDefense(ArmorDefenseType.DARK, 13F)
		);
	}
	
	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve(DSDefaultPackResources.ROOT_DIR_NAME+"/" + location.getNamespace() + "/armor_configs/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "ArmorConfigs";
	}
}
