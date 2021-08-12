package com.skullmangames.darksouls.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.skullmangames.darksouls.DarkSouls;

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
		
		//CapabilityConfig.init(server, file.valueMap());
		CLIENT_CONFIG = client.build();
		COMMON_CONFIG = server.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path) {
		CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
		DarkSouls.LOGGER.info("Load Configuration File");
	}
}