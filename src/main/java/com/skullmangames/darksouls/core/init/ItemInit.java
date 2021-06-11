package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.items.Darksign;
import com.skullmangames.darksouls.common.items.EmeraldFlask;
import com.skullmangames.darksouls.common.items.EstusFlask;
import com.skullmangames.darksouls.common.items.EstusShard;
import com.skullmangames.darksouls.common.items.FireKeeperSoul;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<Item> ESTUS_FLASK = ITEMS.register("estus_flask", () -> new EstusFlask(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING), 5));
	
	public static final RegistryObject<Item> ESTUS_FLASK_PLUS_ONE = ITEMS.register("estus_flask_plus_one", () -> new EstusFlask(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING), 6));
	
	public static final RegistryObject<Item> ESTUS_SHARD = ITEMS.register("estus_shard", () -> new EstusShard(new Item.Properties()
			.tab(ItemGroup.TAB_MATERIALS)));
	
	public static final RegistryObject<Item> FIRE_KEEPER_SOUL = ITEMS.register("fire_keeper_soul", () -> new FireKeeperSoul(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	public static final RegistryObject<Item> EMERALD_FLASK = ITEMS.register("emerald_flask", () -> new EmeraldFlask(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING)));
	
	public static final RegistryObject<Item> DARKSIGN = ITEMS.register("darksign", () -> new Darksign(new Item.Properties()
			.tab(ItemGroup.TAB_TOOLS)));
	
	//Block Items
	public static final RegistryObject<Item> TITANITE_ORE = ITEMS.register("titanite_ore", () -> new BlockItem(BlockInit.TITANITE_ORE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(BlockInit.BONFIRE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
}
