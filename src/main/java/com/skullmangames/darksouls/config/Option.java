package com.skullmangames.darksouls.config;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Option<T>
{
	protected T defaultOption;
	protected T option;
	protected ConfigValue<T> configValue;
	
	public Option(ConfigValue<T> configValue)
	{
		this.configValue = configValue;
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
	
	public static class IntegerOption extends Option<Integer>
	{
		private final int minValue;
		private final int maxValue;
		
		public IntegerOption(ConfigValue<Integer> configValue, int minValue, int maxValue)
		{
			super(configValue);
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