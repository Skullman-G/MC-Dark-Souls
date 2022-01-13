package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SoulsGroup extends CreativeModeTab
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