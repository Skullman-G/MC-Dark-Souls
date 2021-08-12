package com.skullmangames.darksouls.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IngameConfig
{
	public final IntValue longPressCountConfig;
	public final BooleanValue showHealthIndicator;
	public final BooleanValue showTargetIndicator;
	public final BooleanValue filterAnimation;
	
	public IngameConfig(ForgeConfigSpec.Builder config)
	{
		this.longPressCountConfig = config.defineInRange("ingame.long_press_count", 2, 1, 10);
		this.showHealthIndicator = config.define("ingame.show_health_indicator", () -> true);
		this.showTargetIndicator = config.define("ingame.show_target_indicator", () -> true);
		this.filterAnimation = config.define("ingame.filter_animation", () -> false);
	}
}