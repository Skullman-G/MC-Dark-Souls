package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.item.DarkSoulsUseAction;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.common.item.HumanityItem;
import com.skullmangames.darksouls.common.item.ModArmorItem;
import com.skullmangames.darksouls.common.item.SoulContainerItem;
import com.skullmangames.darksouls.common.item.Teleport2BonfireItem;
import com.skullmangames.darksouls.common.item.UndeadBoneShardItem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
	public static final List<Item> DESCRIPTION_ITEMS = new ArrayList<>();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DarkSouls.MOD_ID);
	
	// WEAPONS
	// Dagger
	/*public static final RegistryObject<Item> DAGGER = ITEMS.register("dagger", () -> new SwordItem(Tiers.WOOD, 2, -2.4F, new Item.Properties()
	.tab(CreativeModeTab.TAB_COMBAT)));*/
	
	
	// Straight Swords
	public static final RegistryObject<Item> BROKEN_STRAIGHT_SWORD = ITEMS.register("broken_straight_sword", () -> new SwordItem(Tiers.WOOD, 1, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> STRAIGHT_SWORD_HILT = ITEMS.register("straight_sword_hilt", () -> new SwordItem(Tiers.WOOD, 0, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LONGSWORD = ITEMS.register("longsword", () -> new SwordItem(Tiers.IRON, 3, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Axes
	public static final RegistryObject<Item> BATTLE_AXE = ITEMS.register("battle_axe", () -> new AxeItem(Tiers.IRON, 6.5F, -3.1F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Spears
	public static final RegistryObject<Item> WINGED_SPEAR = ITEMS.register("winged_spear", () -> new SwordItem(Tiers.IRON, 2, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> SPEAR = ITEMS.register("spear", () -> new SwordItem(Tiers.IRON, 3, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Ultra Greatsword
	public static final RegistryObject<Item> ZWEIHANDER = ITEMS.register("zweihander", () -> new SwordItem(Tiers.IRON, 10, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Great Hammer
	public static final RegistryObject<Item> DEMON_GREAT_HAMMER = ITEMS.register("demon_great_hammer", () -> new SwordItem(Tiers.WOOD, 11, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Shields
	public static final RegistryObject<Item> HEATER_SHIELD = ITEMS.register("heater_shield", () -> new Item(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> CRACKED_ROUND_SHIELD = ITEMS.register("cracked_round_shield", () -> new Item(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LORDRAN_SOLDIER_SHIELD = ITEMS.register("lordran_soldier_shield", () -> new Item(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Armor
	public static final RegistryObject<Item> DINGY_HOOD = ITEMS.register("dingy_hood", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, "dingy_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> DINGY_ROBE = ITEMS.register("dingy_robe", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.CHEST, "dingy_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> BLOOD_STAINED_SKIRT = ITEMS.register("blood_stained_skirt", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.LEGS, "dingy_set_layer_2", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LORDRAN_WARRIOR_HELM = ITEMS.register("lordran_warrior_helm", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, "lordran_warrior_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> LORDRAN_WARRIOR_ARMOR = ITEMS.register("lordran_warrior_armor", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.CHEST, "lordran_warrior_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> LORDRAN_WARRIOR_WAISTCLOTH = ITEMS.register("lordran_warrior_waistcloth", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.LEGS, "lordran_warrior_set_layer_2", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> LORDRAN_WARRIOR_BOOTS = ITEMS.register("lordran_warrior_boots", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.FEET, "lordran_warrior_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LORDRAN_SOLDIER_HELM = ITEMS.register("lordran_soldier_helm", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, "lordran_soldier_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> LORDRAN_SOLDIER_ARMOR = ITEMS.register("lordran_soldier_armor", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.CHEST, "lordran_soldier_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> LORDRAN_SOLDIER_WAISTCLOTH = ITEMS.register("lordran_soldier_waistcloth", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.LEGS, "lordran_soldier_set_layer_2", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> LORDRAN_SOLDIER_BOOTS = ITEMS.register("lordran_soldier_boots", () -> new ModArmorItem(ArmorMaterials.LEATHER, EquipmentSlot.FEET, "lordran_soldier_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Other
	public static final RegistryObject<Item> ESTUS_FLASK = ITEMS.register("estus_flask", () -> new EstusFlaskItem(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> ESTUS_SHARD = ITEMS.register("estus_shard", () -> new Item(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> EMERALD_FLASK = ITEMS.register("emerald_flask", () -> new Item(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> DARKSIGN = ITEMS.register("darksign", () -> new Teleport2BonfireItem(DarkSoulsUseAction.DARKSIGN, true, false, true, new Item.Properties()
			.stacksTo(1)));
	
	public static final RegistryObject<Item> HOMEWARD_BONE = ITEMS.register("homeward_bone", () -> new Teleport2BonfireItem(DarkSoulsUseAction.MIRACLE, false, true, false, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> UNDEAD_BONE_SHARD = ITEMS.register("undead_bone_shard", () -> new UndeadBoneShardItem(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	// Souls
	public static final RegistryObject<Item> SOUL_OF_A_LOST_UNDEAD = ITEMS.register("soul_of_a_lost_undead", () -> new SoulContainerItem(200, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LARGE_SOUL_OF_A_LOST_UNDEAD = ITEMS.register("large_soul_of_a_lost_undead", () -> new SoulContainerItem(400, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> HUMANITY = ITEMS.register("humanity", () -> new HumanityItem(1, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> FIRE_KEEPER_SOUL = ITEMS.register("fire_keeper_soul", () -> new HumanityItem(5, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Block Items
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(ModBlocks.BONFIRE.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_ACACIA_DOOR = ITEMS.register("big_acacia_door", () -> new BlockItem(ModBlocks.BIG_ACACIA_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_BIRCH_DOOR = ITEMS.register("big_birch_door", () -> new BlockItem(ModBlocks.BIG_BIRCH_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_OAK_DOOR = ITEMS.register("big_oak_door", () -> new BlockItem(ModBlocks.BIG_OAK_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_SPRUCE_DOOR = ITEMS.register("big_spruce_door", () -> new BlockItem(ModBlocks.BIG_SPRUCE_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_JUNGLE_DOOR = ITEMS.register("big_jungle_door", () -> new BlockItem(ModBlocks.BIG_JUNGLE_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_DARK_OAK_DOOR = ITEMS.register("big_dark_oak_door", () -> new BlockItem(ModBlocks.BIG_DARK_OAK_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_CRIMSON_DOOR = ITEMS.register("big_crimson_door", () -> new BlockItem(ModBlocks.BIG_CRIMSON_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BIG_WARPED_DOOR = ITEMS.register("big_warped_door", () -> new BlockItem(ModBlocks.BIG_WARPED_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> IRON_BAR_DOOR = ITEMS.register("iron_bar_door", () -> new BlockItem(ModBlocks.IRON_BAR_DOOR.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	
	//Spawn Eggs
	public static final RegistryObject<SpawnEggItem> HOLLOW_SPAWN_EGG = ITEMS.register("hollow_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HOLLOW, 0xAA2A00, 0xB05139, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<SpawnEggItem> HOLLOW_LORDRAN_WARRIOR_SPAWN_EGG = ITEMS.register("hollow_lordran_warrior_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HOLLOW_LORDRAN_WARRIOR, 0x2e2103, 0xbd6d1c, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<SpawnEggItem> HOLLOW_LORDRAN_SOLDIER_SPAWN_EGG = ITEMS.register("hollow_lordran_soldier_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HOLLOW_LORDRAN_SOLDIER, 0x7d7d7d, 0xbd6d1c, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	public static void registerDescriptionItems()
	{
		DESCRIPTION_ITEMS.add(ModItems.ESTUS_FLASK.get());
		DESCRIPTION_ITEMS.add(ModItems.ESTUS_SHARD.get());
		DESCRIPTION_ITEMS.add(ModItems.FIRE_KEEPER_SOUL.get());
		DESCRIPTION_ITEMS.add(ModItems.EMERALD_FLASK.get());
		DESCRIPTION_ITEMS.add(ModItems.DARKSIGN.get());
		DESCRIPTION_ITEMS.add(ModItems.HOMEWARD_BONE.get());
		DESCRIPTION_ITEMS.add(ModItems.HUMANITY.get());
		DESCRIPTION_ITEMS.add(ModItems.BROKEN_STRAIGHT_SWORD.get());
		DESCRIPTION_ITEMS.add(ModItems.STRAIGHT_SWORD_HILT.get());
		DESCRIPTION_ITEMS.add(ModItems.UNDEAD_BONE_SHARD.get());
		DESCRIPTION_ITEMS.add(ModItems.DEMON_GREAT_HAMMER.get());
		DESCRIPTION_ITEMS.add(ModItems.HEATER_SHIELD.get());
		DESCRIPTION_ITEMS.add(ModItems.WINGED_SPEAR.get());
		DESCRIPTION_ITEMS.add(ModItems.ZWEIHANDER.get());
		DESCRIPTION_ITEMS.add(ModItems.LONGSWORD.get());
		DESCRIPTION_ITEMS.add(ModItems.BATTLE_AXE.get());
		DESCRIPTION_ITEMS.add(ModItems.SPEAR.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_SOLDIER_SHIELD.get());
		
		DESCRIPTION_ITEMS.add(Items.CHAINMAIL_HELMET);
		DESCRIPTION_ITEMS.add(Items.CHAINMAIL_CHESTPLATE);
		DESCRIPTION_ITEMS.add(Items.CHAINMAIL_LEGGINGS);
		DESCRIPTION_ITEMS.add(Items.CHAINMAIL_BOOTS);
		
		DESCRIPTION_ITEMS.add(ModItems.DINGY_HOOD.get());
		DESCRIPTION_ITEMS.add(ModItems.DINGY_ROBE.get());
		DESCRIPTION_ITEMS.add(ModItems.BLOOD_STAINED_SKIRT.get());
		
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_WARRIOR_HELM.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_WARRIOR_ARMOR.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_WARRIOR_BOOTS.get());
		
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_SOLDIER_HELM.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_SOLDIER_ARMOR.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_SOLDIER_WAISTCLOTH.get());
		DESCRIPTION_ITEMS.add(ModItems.LORDRAN_SOLDIER_BOOTS.get());
	}
}
