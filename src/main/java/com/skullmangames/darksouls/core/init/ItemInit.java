package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.item.DarkSoulsSpawnEggItem;
import com.skullmangames.darksouls.common.item.DarkSoulsUseAction;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.common.item.FireKeeperSoulItem;
import com.skullmangames.darksouls.common.item.GreatHammerItem;
import com.skullmangames.darksouls.common.item.HumanityItem;
import com.skullmangames.darksouls.common.item.KeyItem;
import com.skullmangames.darksouls.common.item.Teleport2BonfireItem;
import com.skullmangames.darksouls.common.item.UndeadBoneShardItem;
import com.skullmangames.darksouls.common.item.WeaponItem;

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
	
	public static final RegistryObject<Item> ESTUS_SHARD = ITEMS.register("estus_shard", () -> new Item(new Item.Properties()
			.tab(ItemGroup.TAB_MATERIALS)));
	
	public static final RegistryObject<Item> FIRE_KEEPER_SOUL = ITEMS.register("fire_keeper_soul", () -> new FireKeeperSoulItem(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	public static final RegistryObject<Item> EMERALD_FLASK = ITEMS.register("emerald_flask", () -> new Item(new Item.Properties()
			.tab(ItemGroup.TAB_BREWING)));
	
	public static final RegistryObject<Item> DARKSIGN = ITEMS.register("darksign", () -> new Teleport2BonfireItem(DarkSoulsUseAction.DARKSIGN, true, false, true, new Item.Properties()
			.stacksTo(1)));
	
	public static final RegistryObject<Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new Teleport2BonfireItem(DarkSoulsUseAction.MIRACLE, false, true, false, new Item.Properties()
			.tab(ItemGroup.TAB_TOOLS)));
	
	public static final RegistryObject<Item> HUMANITY = ITEMS.register("humanity", () -> new HumanityItem(new Item.Properties()
			.tab(DarkSouls.TAB_SOULS)));
	
	public static final RegistryObject<Item> BROKEN_STRAIGHT_SWORD = ITEMS.register("broken_straight_sword", () -> new WeaponItem(ItemTier.WOOD, 1, -2.4F, new Item.Properties()
			.tab(ItemGroup.TAB_COMBAT)));
	
	public static final RegistryObject<Item> STRAIGHT_SWORD_HILT = ITEMS.register("straight_sword_hilt", () -> new WeaponItem(ItemTier.WOOD, 0, -2.4F, new Item.Properties()
			.tab(ItemGroup.TAB_COMBAT)));
	
	public static final RegistryObject<Item> UNDEAD_BONE_SHARD = ITEMS.register("undead_bone_shard", () -> new UndeadBoneShardItem(new Item.Properties()
			.tab(ItemGroup.TAB_MATERIALS)));
	
	public static final RegistryObject<Item> DEMON_GREAT_HAMMER = ITEMS.register("demon_great_hammer", () -> new GreatHammerItem(ItemTier.WOOD, 7, -2.4F, new Item.Properties()
			.tab(ItemGroup.TAB_COMBAT))
			.addStat(Stats.STRENGTH, 46));
	
	/*public static final RegistryObject<Item> KEY = ITEMS.register("key", () -> new KeyItem(new Item.Properties()
			.tab(ItemGroup.TAB_REDSTONE)));*/
	
	//Block Items
	public static final RegistryObject<Item> TITANITE_ORE = ITEMS.register("titanite_ore", () -> new BlockItem(BlockInit.TITANITE_ORE.get(),
			new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
	
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(BlockInit.BONFIRE.get(),
			new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
	
	public static final RegistryObject<Item> BIG_ACACIA_DOOR = ITEMS.register("big_acacia_door", () -> new BlockItem(BlockInit.BIG_ACACIA_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_BIRCH_DOOR = ITEMS.register("big_birch_door", () -> new BlockItem(BlockInit.BIG_BIRCH_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_OAK_DOOR = ITEMS.register("big_oak_door", () -> new BlockItem(BlockInit.BIG_OAK_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_SPRUCE_DOOR = ITEMS.register("big_spruce_door", () -> new BlockItem(BlockInit.BIG_SPRUCE_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_JUNGLE_DOOR = ITEMS.register("big_jungle_door", () -> new BlockItem(BlockInit.BIG_JUNGLE_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_DARK_OAK_DOOR = ITEMS.register("big_dark_oak_door", () -> new BlockItem(BlockInit.BIG_DARK_OAK_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_CRIMSON_DOOR = ITEMS.register("big_crimson_door", () -> new BlockItem(BlockInit.BIG_CRIMSON_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIG_WARPED_DOOR = ITEMS.register("big_warped_door", () -> new BlockItem(BlockInit.BIG_WARPED_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> IRON_BAR_DOOR = ITEMS.register("iron_bar_door", () -> new BlockItem(BlockInit.IRON_BAR_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	
	//Spawn Eggs
	public static final RegistryObject<SpawnEggItem> HOLLOW_SPAWN_EGG = ITEMS.register("hollow_spawn_egg", () -> new DarkSoulsSpawnEggItem(EntityTypeInit.HOLLOW, 0xAA2A00, 0xB05139, new Item.Properties()
			.tab(ItemGroup.TAB_MISC)));
	
	
	//Vanilla Overrides
	public static final DeferredRegister<Item> VANILLA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");
	
	
	public static final RegistryObject<Item> SMITHING_TABLE = VANILLA_ITEMS.register("smithing_table", () -> new BlockItem(BlockInit.SMITHING_TABLE.get(),
			new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
	
	public static final RegistryObject<Item> WOODEN_SWORD = VANILLA_ITEMS.register("wooden_sword", () -> new WeaponItem(ItemTier.WOOD, 3, -2.4F, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
	public static final RegistryObject<Item> DIAMOND_SWORD = VANILLA_ITEMS.register("diamond_sword", () -> new WeaponItem(ItemTier.DIAMOND, 3, -2.4F, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
	public static final RegistryObject<Item> GOLDEN_SWORD = VANILLA_ITEMS.register("golden_sword", () -> new WeaponItem(ItemTier.GOLD, 3, -2.4F, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
	public static final RegistryObject<Item> IRON_SWORD = VANILLA_ITEMS.register("iron_sword", () -> new WeaponItem(ItemTier.IRON, 3, -2.4F, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
	public static final RegistryObject<Item> NETHERITE_SWORD = VANILLA_ITEMS.register("netherite_sword", () -> new WeaponItem(ItemTier.NETHERITE, 3, -2.4F, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
	public static final RegistryObject<Item> STONE_SWORD = VANILLA_ITEMS.register("stone_sword", () -> new WeaponItem(ItemTier.STONE, 3, -2.4F, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

	public static final RegistryObject<Item> ACACIA_DOOR = VANILLA_ITEMS.register("acacia_door", () -> new BlockItem(BlockInit.ACACIA_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> BIRCH_DOOR = VANILLA_ITEMS.register("birch_door", () -> new BlockItem(BlockInit.BIRCH_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> CRIMSON_DOOR = VANILLA_ITEMS.register("crimson_door", () -> new BlockItem(BlockInit.CRIMSON_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> DARK_OAK_DOOR = VANILLA_ITEMS.register("dark_oak_door", () -> new BlockItem(BlockInit.DARK_OAK_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> JUNGLE_DOOR = VANILLA_ITEMS.register("jungle_door", () -> new BlockItem(BlockInit.JUNGLE_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> OAK_DOOR = VANILLA_ITEMS.register("oak_door", () -> new BlockItem(BlockInit.OAK_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> SPRUCE_DOOR = VANILLA_ITEMS.register("spruce_door", () -> new BlockItem(BlockInit.SPRUCE_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
	
	public static final RegistryObject<Item> WARPED_DOOR = VANILLA_ITEMS.register("warped_door", () -> new BlockItem(BlockInit.WARPED_DOOR.get(),
			new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
}
