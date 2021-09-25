package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.block.BigDoorBlock;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.block.LockableDoorBlock;
import com.skullmangames.darksouls.common.block.SmithingTableBlockOverride;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit 
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<Block> TITANITE_ORE = BLOCKS.register("titanite_ore", () -> new Block(AbstractBlock.Properties
			.of(Material.STONE)
			.strength(15, 30)
			.harvestTool(ToolType.PICKAXE)
			.harvestLevel(1)
			.sound(SoundType.STONE)));
	
	public static final RegistryObject<Block> BONFIRE = BLOCKS.register("bonfire", () -> new BonfireBlock());
	
	public static final RegistryObject<Block> BIG_ACACIA_DOOR = BLOCKS.register("big_acacia_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_BIRCH_DOOR = BLOCKS.register("big_birch_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_OAK_DOOR = BLOCKS.register("big_oak_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_SPRUCE_DOOR = BLOCKS.register("big_spruce_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_JUNGLE_DOOR = BLOCKS.register("big_jungle_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.JUNGLE_DOOR.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_DARK_OAK_DOOR = BLOCKS.register("big_dark_oak_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.DARK_OAK_DOOR.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_CRIMSON_DOOR = BLOCKS.register("big_crimson_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_WARPED_DOOR = BLOCKS.register("big_warped_door", () -> new BigDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> IRON_BAR_DOOR = BLOCKS.register("iron_bar_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.METAL, MaterialColor.METAL)
			.requiresCorrectToolForDrops()
			.strength(5.0F)
			.sound(SoundType.METAL)
			.noOcclusion()));
	
	
	// Vanilla Overrides
	public static final DeferredRegister<Block> VANILLA_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
	
	
	public static final RegistryObject<Block> SMITHING_TABLE = VANILLA_BLOCKS.register("smithing_table", () -> new SmithingTableBlockOverride(AbstractBlock.Properties
			.of(Material.WOOD)
			.strength(2.5F)
			.sound(SoundType.WOOD)));
	
	public static final RegistryObject<Block> ACACIA_DOOR = VANILLA_BLOCKS.register("acacia_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> BIRCH_DOOR = VANILLA_BLOCKS.register("birch_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> OAK_DOOR = VANILLA_BLOCKS.register("oak_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> SPRUCE_DOOR = VANILLA_BLOCKS.register("spruce_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> JUNGLE_DOOR = VANILLA_BLOCKS.register("jungle_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.JUNGLE_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> DARK_OAK_DOOR = VANILLA_BLOCKS.register("dark_oak_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> CRIMSON_DOOR = VANILLA_BLOCKS.register("crimson_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
	
	public static final RegistryObject<Block> WARPED_DOOR = VANILLA_BLOCKS.register("warped_door", () -> new LockableDoorBlock(AbstractBlock.Properties
			.of(Material.WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noCollission()));
}