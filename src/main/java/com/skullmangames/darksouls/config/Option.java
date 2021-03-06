package com.skullmangames.darksouls.config;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Option<T>
{
	protected T defaultOption;
	protected T option;
	protected final String name;
	protected ConfigValue<T> configValue;
	
	public Option(ConfigValue<T> configValue, String name, T defaultValue)
	{
		this.name = name;
		this.defaultOption = defaultValue;
		this.configValue = configValue;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void init()
	{
		this.defaultOption = this.configValue.get();
		this.option = this.configValue.get();
	}
	
	public void save()
	{
		this.configValue.set(this.getValue());
	}
	
	public T getValue()
	{
		return this.option;
	}
	
	public void setValue(T option)
	{
		this.option = option;
	}
	
	public void setDefaultValue()
	{
		this.option = this.defaultOption;
	}
	
	public static class BooleanOption extends Option<Boolean>
	{
		public BooleanOption(ConfigValue<Boolean> configValue, String name, boolean defaultValue)
		{
			super(configValue, name, defaultValue);
		}
	}
	
	public static class IntegerOption extends Option<Integer>
	{
		private final int minValue;
		private final int maxValue;
		
		public IntegerOption(ConfigValue<Integer> configValue, String name, int defaultValue, int minValue, int maxValue)
		{
			super(configValue, name, defaultValue);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public void setValue(Integer option)
		{
			if (option > this.maxValue)
			{
				this.option = this.minValue;
			}
			else if (option < this.minValue)
			{
				this.option = this.maxValue;
			}
			else
			{
				this.option = option;
			}
		}
	}
}