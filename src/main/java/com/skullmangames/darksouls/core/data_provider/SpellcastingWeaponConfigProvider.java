package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.common.capability.item.SpellcastingWeaponCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class SpellcastingWeaponConfigProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public SpellcastingWeaponConfigProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (SpellcastingWeaponCap.Builder builder : defaultConfigs())
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save spellcasting weapon config {}", path1, ioexception);
			}
		}
	}
	
	private static List<SpellcastingWeaponCap.Builder> defaultConfigs()
	{
		return ImmutableList.of
		(
				//Talismans
				SpellcastingWeaponCap.builder(ModItems.TALISMAN.get(), WeaponCategory.TALISMAN, 1.00F, 1.00F, 0.5F)
						.putDamage(CoreDamageType.PHYSICAL, 52)
						.putStatInfo(Stats.STRENGTH, 4, Scaling.E)
						.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 10, Scaling.B),
				SpellcastingWeaponCap.builder(ModItems.THOROLUND_TALISMAN.get(), WeaponCategory.TALISMAN, 1.00F, 1.15F, 0.3F)
						.putDamage(CoreDamageType.PHYSICAL, 52)
						.putStatInfo(Stats.STRENGTH, 4, Scaling.E)
						.putStatInfo(Stats.DEXTERITY, 0, Scaling.NONE)
						.putStatInfo(Stats.INTELLIGENCE, 0, Scaling.NONE)
						.putStatInfo(Stats.FAITH, 10, Scaling.B)
		);
	}

	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve("data/" + location.getNamespace() + "/weapon_configs/spellcasting/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "SpellcastingWeaponConfigs";
	}
}
