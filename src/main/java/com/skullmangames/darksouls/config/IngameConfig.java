package com.skullmangames.darksouls.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class IngameConfig
{
	private final ForgeConfigSpec.Builder config;
	public final List<Option<?>> OPTIONS = new ArrayList<Option<?>>();
	
	public static final float A_TICK = 0.05F;
	public static final float GENERAL_ANIMATION_CONVERT_TIME = 0.16F;
	
	public final Option<Integer> longPressCount;
	
	public final Option<Boolean> filterAnimation;
	public final Option<Boolean> showHealthIndicator;
	
	public IngameConfig(ForgeConfigSpec.Builder config)
	{
		this.config = config;
		
		this.longPressCount = this.registerInt("long_press_count", 2, 1, 10);
		
		this.showHealthIndicator = this.registerBoolean("show_health_indicator", true);
		this.filterAnimation = this.registerBoolean("filter_animation", false);
	}
	
	public void populateDefaultValues()
	{
		for (Option<?> option : OPTIONS) option.init();
	}
	
	public void resetSettings()
	{
		for (Option<?> option : OPTIONS) option.setDefaultValue();
	}
	
	public void save()
	{
		for (Option<?> option : OPTIONS) option.save();
	}
	
	private Option<Boolean> registerBoolean(String name, boolean defaultValue)
	{
		name = "ingame."+name;
		Option<Boolean> option = new Option<Boolean>(this.config.define(name, () -> defaultValue), name, defaultValue);
		OPTIONS.add(option);
		return option;
	}
	
	private Option<Integer> registerInt(String name, int defaultValue, int min, int max)
	{
		name = "ingame."+name;
		Option<Integer> option = new Option.IntegerOption(this.config.defineInRange(name, defaultValue, min, max), name, defaultValue, min, max);
		OPTIONS.add(option);
		return option;
	}
}