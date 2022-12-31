package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
	public class Items
	{
		public static final TagKey<Item> TALISMANS = tag("talismans");
		public static final TagKey<Item> MIRACLES = tag("miracles");
		
		private static TagKey<Item> tag(String name)
		{
			return ItemTags.create(new ResourceLocation(DarkSouls.MOD_ID, name));
		}
	}
}
