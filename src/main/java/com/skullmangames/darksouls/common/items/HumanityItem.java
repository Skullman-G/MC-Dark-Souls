package com.skullmangames.darksouls.common.items;

public class HumanityItem extends SoulContainerItem
{
	public HumanityItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public int getHumanity()
	{
		return 1;
	}
}
