package com.skullmangames.darksouls.common.entity;

import net.minecraft.network.chat.TranslatableComponent;

public enum Covenant
{
	NONE("none"), WARRIORS_OF_SUNLIGHT("warriors_of_sunlight");
	
	final String name;
	
	Covenant(String name)
	{
		this.name = "covenant.darksouls."+name;
	}
	
	public String getRegistryName()
	{
		return new TranslatableComponent(this.name).getString();
	}
	
	public String getDescription()
	{
		return new TranslatableComponent(this.name+".description").getString();
	}
}
