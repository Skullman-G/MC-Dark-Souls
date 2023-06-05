package com.skullmangames.darksouls.core.init;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.item.AshenEstusFlaskItem;
import com.skullmangames.darksouls.common.item.DarkSoulsUseAction;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.common.item.HumanityItem;
import com.skullmangames.darksouls.common.item.ModArmorItem;
import com.skullmangames.darksouls.common.item.SoulContainerItem;
import com.skullmangames.darksouls.common.item.SpellItem;
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
	
	//Dagger
	/*public static final RegistryObject<Item> DAGGER = ITEMS.register("dagger", () -> new SwordItem(Tiers.WOOD, 2, -2.4F, new Item.Properties()
	.tab(CreativeModeTab.TAB_COMBAT)));*/
	
	
	//Straight Swords
	public static final RegistryObject<Item> BROKEN_STRAIGHT_SWORD = ITEMS.register("broken_straight_sword", () -> new SwordItem(Tiers.WOOD, 40, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> STRAIGHT_SWORD_HILT = ITEMS.register("straight_sword_hilt", () -> new SwordItem(Tiers.WOOD, 20, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LONGSWORD = ITEMS.register("longsword", () -> new SwordItem(Tiers.IRON, 80, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Greatswords
	public static final RegistryObject<Item> BLACK_KNIGHT_SWORD = ITEMS.register("black_knight_sword", () -> new SwordItem(Tiers.IRON, 220, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> CLAYMORE = ITEMS.register("claymore", () -> new SwordItem(Tiers.IRON, 103, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Axes
	public static final RegistryObject<Item> BATTLE_AXE = ITEMS.register("battle_axe", () -> new AxeItem(Tiers.IRON, 95F, -3.1F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Hammers
	public static final RegistryObject<Item> MACE = ITEMS.register("mace", () -> new SwordItem(Tiers.IRON, 91, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Spears
	public static final RegistryObject<Item> WINGED_SPEAR = ITEMS.register("winged_spear", () -> new SwordItem(Tiers.IRON, 86, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> SPEAR = ITEMS.register("spear", () -> new SwordItem(Tiers.IRON, 80, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Ultra Greatsword
	public static final RegistryObject<Item> ZWEIHANDER = ITEMS.register("zweihander", () -> new SwordItem(Tiers.IRON, 130, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Great Hammer
	public static final RegistryObject<Item> DEMON_GREAT_HAMMER = ITEMS.register("demon_great_hammer", () -> new SwordItem(Tiers.WOOD, 138, -2.4F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Shields
	public static final RegistryObject<Item> HEATER_SHIELD = ITEMS.register("heater_shield", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> CRACKED_ROUND_SHIELD = ITEMS.register("cracked_round_shield", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LORDRAN_SOLDIER_SHIELD = ITEMS.register("lordran_soldier_shield", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> KNIGHT_SHIELD = ITEMS.register("knight_shield", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> GOLDEN_FALCON_SHIELD = ITEMS.register("golden_falcon_shield", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	
	//Talismans
	public static final RegistryObject<Item> TALISMAN = ITEMS.register("talisman", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> THOROLUND_TALISMAN = ITEMS.register("thorolund_talisman", () -> new Item(new Item.Properties()
			.stacksTo(1)
			.tab(DarkSouls.TAB)));
	
	
	//Miracles
	public static final RegistryObject<Item> MIRACLE_HEAL = ITEMS.register("miracle_heal", () -> new SpellItem(() -> Animations.BIPED_CAST_MIRACLE_HEAL, 12, 45F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> MIRACLE_HEAL_AID = ITEMS.register("miracle_heal_aid", () -> new SpellItem(() -> Animations.BIPED_CAST_MIRACLE_HEAL_AID, 8, 27F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> MIRACLE_HOMEWARD = ITEMS.register("miracle_homeward", () -> new SpellItem(() -> Animations.BIPED_CAST_MIRACLE_HOMEWARD, 18, 30F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> MIRACLE_FORCE = ITEMS.register("miracle_force", () -> new SpellItem(() -> Animations.BIPED_CAST_MIRACLE_FORCE, 12, 26F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> MIRACLE_LIGHTNING_SPEAR = ITEMS.register("miracle_lightning_spear", () -> new SpellItem(() -> Animations.BIPED_CAST_MIRACLE_LIGHTNING_SPEAR, () -> Animations.HORSEBACK_CAST_MIRACLE_LIGHTNING_SPEAR, 20, 23F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> MIRACLE_GREAT_LIGHTNING_SPEAR = ITEMS.register("miracle_great_lightning_spear", () -> new SpellItem(() -> Animations.BIPED_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR, () -> Animations.HORSEBACK_CAST_MIRACLE_GREAT_LIGHTNING_SPEAR, 30, 32F, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Armor
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
	
	public static final RegistryObject<Item> ELITE_CLERIC_HELM = ITEMS.register("elite_cleric_helm", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, "elite_cleric_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> ELITE_CLERIC_ARMOR = ITEMS.register("elite_cleric_armor", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.CHEST, "elite_cleric_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> ELITE_CLERIC_LEGGINGS = ITEMS.register("elite_cleric_leggings", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.LEGS, "elite_cleric_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> FALCONER_HELM = ITEMS.register("falconer_helm", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, "falconer_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> FALCONER_ARMOR = ITEMS.register("falconer_armor", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.CHEST, "falconer_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> FALCONER_LEGGINGS = ITEMS.register("falconer_leggings", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.LEGS, "falconer_set_layer_1", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> FALCONER_BOOTS = ITEMS.register("falconer_boots", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.FEET, "falconer_set_layer_2", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BLACK_KNIGHT_HELM = ITEMS.register("black_knight_helm", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, "black_knight_set", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> BLACK_KNIGHT_ARMOR = ITEMS.register("black_knight_armor", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.CHEST, "black_knight_set", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> BLACK_KNIGHT_LEGGINGS = ITEMS.register("black_knight_leggings", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.LEGS, "black_knight_set", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> BALDER_HELM = ITEMS.register("balder_helm", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.HEAD, "balder_helm", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> BALDER_ARMOR = ITEMS.register("balder_armor", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.CHEST, "balder_set", new Item.Properties()
			.tab(DarkSouls.TAB)));
	public static final RegistryObject<Item> BALDER_BOOTS = ITEMS.register("balder_boots", () -> new ModArmorItem(ArmorMaterials.IRON, EquipmentSlot.FEET, "balder_boots", new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Other
	public static final RegistryObject<Item> ESTUS_FLASK = ITEMS.register("estus_flask", () -> new EstusFlaskItem(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> ASHEN_ESTUS_FLASK = ITEMS.register("ashen_estus_flask", () -> new AshenEstusFlaskItem(new Item.Properties()
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
	
	public static final RegistryObject<Item> SUNLIGHT_MEDAL = ITEMS.register("sunlight_medal", () -> new Item(new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Souls
	public static final RegistryObject<Item> SOUL_OF_A_LOST_UNDEAD = ITEMS.register("soul_of_a_lost_undead", () -> new SoulContainerItem(200, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LARGE_SOUL_OF_A_LOST_UNDEAD = ITEMS.register("large_soul_of_a_lost_undead", () -> new SoulContainerItem(400, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> SOUL_OF_A_NAMELESS_SOLDIER = ITEMS.register("soul_of_a_nameless_soldier", () -> new SoulContainerItem(800, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> LARGE_SOUL_OF_A_NAMELESS_SOLDIER = ITEMS.register("large_soul_of_a_nameless_soldier", () -> new SoulContainerItem(1000, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> HUMANITY = ITEMS.register("humanity", () -> new HumanityItem(1, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> FIRE_KEEPER_SOUL = ITEMS.register("fire_keeper_soul", () -> new HumanityItem(5, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	
	//Block Items
	public static final RegistryObject<Item> BONFIRE = ITEMS.register("bonfire", () -> new BlockItem(ModBlocks.BONFIRE.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> SUNLIGHT_ALTAR = ITEMS.register("sunlight_altar", () -> new BlockItem(ModBlocks.SUNLIGHT_ALTAR.get(),
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
	
	public static final RegistryObject<Item> OAK_PLATFORM = ITEMS.register("oak_platform", () -> new BlockItem(ModBlocks.OAK_PLATFORM.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> STONE_BRICK_WINDOW = ITEMS.register("stone_brick_window", () -> new BlockItem(ModBlocks.STONE_BRICK_WINDOW.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	public static final RegistryObject<Item> TERRACOTTA_POT = ITEMS.register("terracotta_pot", () -> new BlockItem(ModBlocks.TERRACOTTA_POT.get(),
			new Item.Properties().tab(DarkSouls.TAB)));
	
	
	//Spawn Eggs
	public static final RegistryObject<SpawnEggItem> HOLLOW_SPAWN_EGG = ITEMS.register("hollow_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HOLLOW, 0xAA2A00, 0xB05139, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<SpawnEggItem> HOLLOW_LORDRAN_WARRIOR_SPAWN_EGG = ITEMS.register("hollow_lordran_warrior_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HOLLOW_LORDRAN_WARRIOR, 0x2e2103, 0xbd6d1c, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<SpawnEggItem> HOLLOW_LORDRAN_SOLDIER_SPAWN_EGG = ITEMS.register("hollow_lordran_soldier_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.HOLLOW_LORDRAN_SOLDIER, 0x7d7d7d, 0xbd6d1c, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<SpawnEggItem> FALCONER_SPAWN_EGG = ITEMS.register("falconer_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.FALCONER, 0x590d07, 0x573f1e, new Item.Properties()
			.tab(DarkSouls.TAB)));
	
	public static final RegistryObject<SpawnEggItem> BLACK_KNIGHT_SPAWN_EGG = ITEMS.register("black_knight_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.BLACK_KNIGHT, 0x333333, 0x171717, new Item.Properties()
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
		DESCRIPTION_ITEMS.add(ModItems.TALISMAN.get());
		DESCRIPTION_ITEMS.add(ModItems.THOROLUND_TALISMAN.get());
		DESCRIPTION_ITEMS.add(ModItems.ASHEN_ESTUS_FLASK.get());
		DESCRIPTION_ITEMS.add(ModItems.MIRACLE_HEAL_AID.get());
		DESCRIPTION_ITEMS.add(ModItems.MIRACLE_HEAL.get());
		DESCRIPTION_ITEMS.add(ModItems.MIRACLE_FORCE.get());
		DESCRIPTION_ITEMS.add(ModItems.MIRACLE_HOMEWARD.get());
		DESCRIPTION_ITEMS.add(ModItems.MIRACLE_LIGHTNING_SPEAR.get());
		DESCRIPTION_ITEMS.add(ModItems.MIRACLE_GREAT_LIGHTNING_SPEAR.get());
		DESCRIPTION_ITEMS.add(ModItems.GOLDEN_FALCON_SHIELD.get());
		
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
		
		DESCRIPTION_ITEMS.add(ModItems.ELITE_CLERIC_HELM.get());
		DESCRIPTION_ITEMS.add(ModItems.ELITE_CLERIC_ARMOR.get());
		DESCRIPTION_ITEMS.add(ModItems.ELITE_CLERIC_LEGGINGS.get());
		
		DESCRIPTION_ITEMS.add(ModItems.FALCONER_HELM.get());
		DESCRIPTION_ITEMS.add(ModItems.FALCONER_ARMOR.get());
		DESCRIPTION_ITEMS.add(ModItems.FALCONER_LEGGINGS.get());
		DESCRIPTION_ITEMS.add(ModItems.FALCONER_BOOTS.get());
	}
}
