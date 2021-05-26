package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkSouls.MOD_ID);
	
	//Block Items
	public static final RegistryObject<Item> TITANITE_ORE = ITEMS.register("titanite_ore", () -> new BlockItem(BlockInit.TITANITE_ORE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(BlockInit.BONFIRE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
}
