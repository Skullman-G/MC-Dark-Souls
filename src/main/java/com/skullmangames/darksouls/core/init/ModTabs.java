package com.skullmangames.darksouls.core.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModTabs
{
	public static final CreativeModeTab TAB_SOULS = new CreativeModeTab("soulstab")
		{
			@Override
			public ItemStack makeIcon()
			{
				return ModItems.FIRE_KEEPER_SOUL.get().getDefaultInstance();
			}
		};
}
