package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.items.DarkSoulsSpawnEggItem;
import com.skullmangames.darksouls.common.items.DarkSoulsUseAction;
import com.skullmangames.darksouls.common.items.DescriptionItem;
import com.skullmangames.darksouls.common.items.EstusFlaskItem;
import com.skullmangames.darksouls.common.items.FireKeeperSoulItem;
import com.skullmangames.darksouls.common.items.HumanityItem;
import com.skullmangames.darksouls.common.items.SwordDescriptionItem;
import com.skullmangames.darksouls.common.items.Teleport2BonfireItem;
import com.skullmangames.darksouls.common.items.UndeadBoneShardItem;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<Item> ESTUS_FLASK = ITEMS.register("estus_flask", () -> new EstusFlaskItem(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING)));
	
	public static final RegistryObject<Item> ESTUS_SHARD = ITEMS.register("estus_shard", () -> new DescriptionItem(new Item.Properties()
			.tab(ItemGroup.TAB_MATERIALS)));
	
	public static final RegistryObject<Item> FIRE_KEEPER_SOUL = ITEMS.register("fire_keeper_soul", () -> new FireKeeperSoulItem(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	public static final RegistryObject<Item> EMERALD_FLASK = ITEMS.register("emerald_flask", () -> new DescriptionItem(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING)));
	
	public static final RegistryObject<Item> DARKSIGN = ITEMS.register("darksign", () -> new Teleport2BonfireItem(DarkSoulsUseAction.DARKSIGN, true, false, new Item.Properties()
			.stacksTo(1)));
	
	public static final RegistryObject<Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new Teleport2BonfireItem(DarkSoulsUseAction.MIRACLE, false, true, new Item.Properties()
			.tab(ItemGroup.TAB_TOOLS)));
	
	public static final RegistryObject<Item> HUMANITY = ITEMS.register("humanity", () -> new HumanityItem(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	public static final RegistryObject<Item> BROKEN_STRAIGHT_SWORD = ITEMS.register("broken_straight_sword", () -> new SwordDescriptionItem(ItemTier.WOOD, 1, -2.4F, new Item.Properties()
			.tab(ItemGroup.TAB_COMBAT)));
	
	public static final RegistryObject<Item> STRAIGHT_SWORD_HILT = ITEMS.register("straight_sword_hilt", () -> new SwordDescriptionItem(ItemTier.WOOD, 1, -2.4F, new Item.Properties()
			.tab(ItemGroup.TAB_COMBAT)));
	
	public static final RegistryObject<Item> UNDEAD_BONE_SHARD = ITEMS.register("undead_bone_shard", () -> new UndeadBoneShardItem(new Item.Properties()
			.tab(ItemGroup.TAB_MATERIALS)));
	
	//Block Items
	public static final RegistryObject<Item> TITANITE_ORE = ITEMS.register("titanite_ore", () -> new BlockItem(BlockInit.TITANITE_ORE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(BlockInit.BONFIRE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	//Spawn Eggs
	public static final RegistryObject<SpawnEggItem> HOLLOW_SPAWN_EGG = ITEMS.register("hollow_spawn_egg", () -> new DarkSoulsSpawnEggItem(EntityTypeInit.HOLLOW, 0xAA2A00, 0xB05139, new Item.Properties()
			.tab(ItemGroup.TAB_MISC)));
	
	//Vanilla Overrides
	public static final DeferredRegister<Item> VANILLA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");
	
	public static final RegistryObject<Item> SMITHING_TABLE = VANILLA_ITEMS.register("smithing_table", () -> new BlockItem(BlockInit.SMITHING_TABLE.get(),
			new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
}
