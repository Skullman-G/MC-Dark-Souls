package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.item.Item;

public class ModTags
{
	public static class Items
	{
		public static final INamedTag<Item> TALISMANS = tag("talismans");
		public static final INamedTag<Item> MIRACLES = tag("miracles");
		
		private static INamedTag<Item> tag(String name)
		{
			return ItemTags.createOptional(new ResourceLocation(DarkSouls.MOD_ID, name));
		}
	}
}
