package com.skullmangames.darksouls.config;

public class Option<T>
{
	protected final T defaultOption;
	protected T option;
	
	public Option(T defaultOption)
	{
		this.defaultOption = defaultOption;
		this.option = defaultOption;
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
		
		public IntegerOption(Integer defaultOption, int minValue, int maxValue)
		{
			super(defaultOption);
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