package com.skullmangames.darksouls.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig
{
	public final double iFramesPercentage;
	
	public ServerConfig(ForgeConfigSpec.Builder config)
	{
		this.iFramesPercentage = config.defineInRange("server_config.iframes_percentage", 0.8D, 0.0D, 1.0F).get();
	}
}
