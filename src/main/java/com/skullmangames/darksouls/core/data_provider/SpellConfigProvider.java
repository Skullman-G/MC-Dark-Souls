package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellCap;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.SpellType;
import com.skullmangames.darksouls.core.util.data.pack_resources.DSDefaultPackResources;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class SpellConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public SpellConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (SpellCap.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save spell config {}", path1, ioexception);
			}
		}
	}
	
	private static List<SpellCap.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
				//Miracles
				SpellCap.builder(ModItems.MIRACLE_HEAL.get(), SpellType.MIRACLE, 45F, Animations.BIPED_CAST_MIRACLE_HEAL.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 12),
				
				SpellCap.builder(ModItems.MIRACLE_HEAL_AID.get(), SpellType.MIRACLE, 27F, Animations.BIPED_CAST_MIRACLE_HEAL_AID.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 8),
				
				SpellCap.builder(ModItems.MIRACLE_HOMEWARD.get(), SpellType.MIRACLE, 30F, Animations.BIPED_CAST_MIRACLE_HOMEWARD.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 18),
				
				SpellCap.builder(ModItems.MIRACLE_FORCE.get(), SpellType.MIRACLE, 26F, Animations.BIPED_CAST_MIRACLE_FORCE.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 12),
				
				SpellCap.builder(ModItems.MIRACLE_LIGHTNING_SPEAR.get(), SpellType.MIRACLE, 23F, Animations.BIPED_CAST_MIRACLE_LIGHTNING_SPEAR.getId(),
						Animations.HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 20),
				
				SpellCap.builder(ModItems.MIRACLE_GREAT_LIGHTNING_SPEAR.get(), SpellType.MIRACLE, 32F, Animations.BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR.getId(),
						Animations.HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR.getId())
				.putStatReq(Stats.INTELLIGENCE, 0)
				.putStatReq(Stats.FAITH, 30)
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve(DSDefaultPackResources.ROOT_DIR_NAME+"/" + location.getNamespace() + "/spell_configs/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "SpellConfigs";
	}
}
