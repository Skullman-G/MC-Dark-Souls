package com.skullmangames.darksouls.core.data_provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.data.WeaponSkills;
import com.skullmangames.darksouls.core.util.WeaponSkill;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class WeaponSkillProvider implements DataProvider
{
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;
	
	public WeaponSkillProvider(DataGenerator generator)
	{
		this.generator = generator;
	}
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		Path path = this.generator.getOutputFolder();
		
		for (WeaponSkill.Builder builder : defaultSkills())
		{
			Path path1 = createPath(path, builder.getId());
			try
			{
				DataProvider.save(GSON, cache, builder.toJson(), path1);
			}
			catch (IOException ioexception)
			{
				LOGGER.error("Couldn't save weapon skill {}", path1, ioexception);
			}
		}
	}
	
	private static List<WeaponSkill.Builder> defaultSkills()
	{
		return ImmutableList.of
		(
			WeaponSkill.mirrorBuilder(WeaponSkills.PARRY, Animations.SHIELD_PARRY_LEFT.get(), Animations.SHIELD_PARRY_RIGHT.get()),
			WeaponSkill.mirrorBuilder(WeaponSkills.FAST_PARRY, Animations.BUCKLER_PARRY_LEFT.get(), Animations.BUCKLER_PARRY_RIGHT.get()),
			WeaponSkill.mirrorBuilder(WeaponSkills.GREATSHIELD_BASH, Animations.GREATSHIELD_BASH.get(), Animations.GREATSHIELD_LIGHT_ATTACK.get())
		);
	}
	
	private static Path createPath(Path path, ResourceLocation location)
	{
		return path.resolve("data/" + location.getNamespace() + "/weapon_skills/" + location.getPath() + ".json");
	}

	@Override
	public String getName()
	{
		return "WeaponSkills";
	}
}
