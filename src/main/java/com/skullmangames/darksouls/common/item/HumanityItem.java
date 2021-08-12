package com.skullmangames.darksouls.common.item;

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
