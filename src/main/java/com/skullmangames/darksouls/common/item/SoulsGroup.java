package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class SoulsGroup extends ItemGroup
{

	public SoulsGroup(String label)
	{
		super(label);
	}

	@Override
	public ItemStack makeIcon()
	{
		return ModItems.FIRE_KEEPER_SOUL.get().getDefaultInstance();
	}
	
}