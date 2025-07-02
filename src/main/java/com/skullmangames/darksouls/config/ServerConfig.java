package com.skullmangames.darksouls.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ServerConfig
{
	public final ConfigValue<Double> iFramesPercentage;
	public final ConfigValue<Boolean> gradualPoiseRegen;
	
	public ServerConfig(ForgeConfigSpec.Builder config)
	{
		this.iFramesPercentage = config.defineInRange("server_config.iframes_percentage", 0.8D, 0.0D, 1.0F);
		this.gradualPoiseRegen = config.define("server_config.gradual_poise_regen", false);
	}
}
