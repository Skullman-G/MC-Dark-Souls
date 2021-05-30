package com.skullmangames.darksouls.common.items;

import com.skullmangames.darksouls.core.init.ItemInit;

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
		return ItemInit.FIRE_KEEPER_SOUL.get().getDefaultInstance();
	}
	
}