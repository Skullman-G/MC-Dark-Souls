package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.items.DarksignItem;
import com.skullmangames.darksouls.common.items.EmeraldFlaskItem;
import com.skullmangames.darksouls.common.items.EstusFlaskItem;
import com.skullmangames.darksouls.common.items.EstusShardItem;
import com.skullmangames.darksouls.common.items.FireKeeperSoulItem;
import com.skullmangames.darksouls.common.items.HumanityItem;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<Item> ESTUS_FLASK = ITEMS.register("estus_flask", () -> new EstusFlaskItem(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING)));
	
	public static final RegistryObject<Item> ESTUS_SHARD = ITEMS.register("estus_shard", () -> new EstusShardItem(new Item.Properties()
			.tab(ItemGroup.TAB_MATERIALS)));
	
	public static final RegistryObject<Item> FIRE_KEEPER_SOUL = ITEMS.register("fire_keeper_soul", () -> new FireKeeperSoulItem(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	public static final RegistryObject<Item> EMERALD_FLASK = ITEMS.register("emerald_flask", () -> new EmeraldFlaskItem(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING)));
	
	public static final RegistryObject<Item> DARKSIGN = ITEMS.register("darksign", () -> new DarksignItem(new Item.Properties()
			.tab(ItemGroup.TAB_TOOLS)));
	
	public static final RegistryObject<Item> HUMANITY = ITEMS.register("humanity", () -> new HumanityItem(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	//Block Items
	public static final RegistryObject<Item> TITANITE_ORE = ITEMS.register("titanite_ore", () -> new BlockItem(BlockInit.TITANITE_ORE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(BlockInit.BONFIRE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	//Vanilla Overrides
	public static final DeferredRegister<Item> VANILLA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");
	
	public static final RegistryObject<Item> SMITHING_TABLE = VANILLA_ITEMS.register("smithing_table", () -> new BlockItem(BlockInit.SMITHING_TABLE.get(),
			new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
}
