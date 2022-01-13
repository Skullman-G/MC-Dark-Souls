package com.skullmangames.darksouls.common.state.properties;

import net.minecraft.util.StringRepresentable;

public enum TrippleBlockPart implements StringRepresentable
{
	UPPER,
	MIDDLE,
	LOWER;
	
	@Override
	public String toString()
	{
	      return this.getSerializedName();
	}
	
	@Override
	public String getSerializedName()
	{
		switch (this)
		{
			case UPPER:
				return "upper";
				
			case MIDDLE:
				return "middle";
				
			case LOWER:
			default:
				return "lower";
		}
	}

}
