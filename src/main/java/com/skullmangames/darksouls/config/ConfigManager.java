package com.skullmangames.darksouls.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.skullmangames.darksouls.DarkSouls;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

public class ConfigManager
{
	public static final ForgeConfigSpec SERVER_CONFIG_BUILDER;
	public static final ForgeConfigSpec CLIENT_CONFIG_BUILDER;
	public static final ClientConfig CLIENT_CONFIG;
	public static final ServerConfig SERVER_CONFIG;
	
	static
	{
		CommentedFileConfig file = CommentedFileConfig.builder(new File(FMLPaths.CONFIGDIR.get().resolve(DarkSouls.CONFIG_FILE_PATH).toString())).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();
		
		CLIENT_CONFIG = new ClientConfig(client);
		SERVER_CONFIG = new ServerConfig(server, file.valueMap());
		
		CLIENT_CONFIG_BUILDER = client.build();
		SERVER_CONFIG_BUILDER = server.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path)
	{
		CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
		DarkSouls.LOGGER.info("Configuration File loaded");
	}
}