package com.skullmangames.darksouls.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.item.IShield.ShieldType;
import com.skullmangames.darksouls.common.capability.item.ShieldCap.ShieldMat;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.Scaling;
import com.skullmangames.darksouls.common.capability.item.WeaponCap.WeaponCategory;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

public class ConfigManager
{
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec CLIENT_CONFIG;
	public static final IngameConfig INGAME_CONFIG;
	
	static
	{
		CommentedFileConfig file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(DarkSouls.CONFIG_FILE_PATH).toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		
		INGAME_CONFIG = new IngameConfig(client);
		
		String weaponKey = "weapon_config";
		if(file.valueMap().get(weaponKey) == null)
		{
			server.define(weaponKey+".sample.registry_name", "samle");
			server.defineEnum(weaponKey+".sample.category", WeaponCategory.STRAIGHT_SWORD);
			server.define(weaponKey+".sample.required_strength", 0);
			server.define(weaponKey+".sample.required_dexterity", 0);
			server.define(weaponKey+".sample.required_faith", 0);
			server.defineEnum(weaponKey+".sample.strength_scaling", Scaling.NONE);
			server.defineEnum(weaponKey+".sample.dexterity_scaling", Scaling.NONE);
			server.defineEnum(weaponKey+".sample.faith_scaling", Scaling.NONE);
		}
		
		String shieldKey = "shield_config";
		if(file.valueMap().get(shieldKey) == null)
		{
			server.define(shieldKey+".sample_shield.registry_name", "sample_shield");
			server.defineEnum(shieldKey+".sample_shield.category", WeaponCategory.SHIELD);
			server.define(shieldKey+".sample_shield.required_strength", 0);
			server.define(shieldKey+".sample_shield.required_dexterity", 0);
			server.define(shieldKey+".sample_shield.required_faith", 0);
			server.defineEnum(shieldKey+".sample_shield.strength_scaling", Scaling.NONE);
			server.defineEnum(shieldKey+".sample_shield.dexterity_scaling", Scaling.NONE);
			server.defineEnum(shieldKey+".sample_shield.faith_scaling", Scaling.NONE);
			server.defineEnum(shieldKey+".sample_shield.shield_type", ShieldType.NORMAL);
			server.defineEnum(shieldKey+".sample_shield.shield_material", ShieldMat.WOOD);
			server.defineInRange(shieldKey+".sample_shield.physical_defense", 0.0F, 0.0F, 1.0F);
		}
		
		CapabilityConfig.init(server, file.valueMap());
		
		CLIENT_CONFIG = client.build();
		COMMON_CONFIG = server.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path)
	{
		CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
		DarkSouls.LOGGER.info("Configuration File loaded");
	}
}