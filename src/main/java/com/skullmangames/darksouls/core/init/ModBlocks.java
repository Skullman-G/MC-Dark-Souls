package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.block.BigDoorBlock;
import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.common.block.CustomShapedBlock;
import com.skullmangames.darksouls.common.block.DirectionalCustomShapedBlock;
import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.common.block.SunlightAltarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks 
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DarkSouls.MOD_ID);
	
	public static final RegistryObject<Block> BONFIRE = BLOCKS.register("bonfire", () -> new BonfireBlock());
	
	public static final RegistryObject<Block> SUNLIGHT_ALTAR = BLOCKS.register("sunlight_altar", () -> new SunlightAltarBlock());
	
	public static final RegistryObject<Block> LIGHT_SOURCE = BLOCKS.register("light_source", () -> new LightSource());
	
	public static final RegistryObject<Block> BIG_ACACIA_DOOR = BLOCKS.register("big_acacia_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.ACACIA_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_BIRCH_DOOR = BLOCKS.register("big_birch_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.BIRCH_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_OAK_DOOR = BLOCKS.register("big_oak_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_SPRUCE_DOOR = BLOCKS.register("big_spruce_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.SPRUCE_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_JUNGLE_DOOR = BLOCKS.register("big_jungle_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.JUNGLE_DOOR.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_DARK_OAK_DOOR = BLOCKS.register("big_dark_oak_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.DARK_OAK_DOOR.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_CRIMSON_DOOR = BLOCKS.register("big_crimson_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.CRIMSON_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> BIG_WARPED_DOOR = BLOCKS.register("big_warped_door", () -> new BigDoorBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.WARPED_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion()));
	
	public static final RegistryObject<Block> IRON_BAR_DOOR = BLOCKS.register("iron_bar_door", () -> new DoorBlock(BlockBehaviour.Properties
			.of(Material.METAL, Blocks.IRON_BARS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.METAL)
			.noOcclusion()));
	
	public static final RegistryObject<Block> OAK_PLATFORM = BLOCKS.register("oak_platform", () -> new CustomShapedBlock(BlockBehaviour.Properties
			.of(Material.WOOD, Blocks.OAK_PLANKS.defaultMaterialColor())
			.strength(3.0F)
			.sound(SoundType.WOOD)
			.noOcclusion(),
			Block.box(0, 0, 0, 16, 4, 16)));
	
	public static final RegistryObject<Block> STONE_BRICK_WINDOW = BLOCKS.register("stone_brick_window", () -> new DirectionalCustomShapedBlock(BlockBehaviour.Properties
			.of(Material.STONE)
			.strength(3.0F)
			.sound(SoundType.STONE)
			.noOcclusion(),
			new VoxelShape[]
			{
				Block.box(14, 0, 0, 16, 16, 16),
				Block.box(2, 14, 0, 14, 16, 16),
				Block.box(2, 0, 0, 14, 2, 16),
				Block.box(2, 13, 1, 14, 14, 15),
				Block.box(2, 2, 1, 14, 3, 15),
				Block.box(2, 3, 1, 3, 13, 15),
				Block.box(13, 3, 1, 14, 13, 15),
				Block.box(7, 3, 1, 9, 13, 15),
				Block.box(0, 0, 0, 2, 16, 16)
			},
			new VoxelShape[]
			{
				Block.box(14, 0, 0, 16, 16, 16),
				Block.box(2, 14, 0, 14, 16, 16),
				Block.box(2, 0, 0, 14, 2, 16),
				Block.box(2, 13, 1, 14, 14, 15),
				Block.box(2, 2, 1, 14, 3, 15),
				Block.box(2, 3, 1, 3, 13, 15),
				Block.box(13, 3, 1, 14, 13, 15),
				Block.box(7, 3, 1, 9, 13, 15),
				Block.box(0, 0, 0, 2, 16, 16)
			},
			new VoxelShape[]
			{
				Block.box(0, 0, 0, 16, 16, 2),
				Block.box(0, 14, 2, 16, 16, 14),
				Block.box(0, 0, 2, 16, 2, 14),
				Block.box(1, 13, 2, 15, 14, 14),
				Block.box(1, 2, 2, 15, 3, 14),
				Block.box(1, 3, 13, 15, 13, 14),
				Block.box(1, 3, 2, 15, 13, 3),
				Block.box(1, 3, 7, 15, 13, 9),
				Block.box(0, 0, 14, 16, 16, 16)
			},
			new VoxelShape[]
			{
				Block.box(0, 0, 0, 16, 16, 2),
				Block.box(0, 14, 2, 16, 16, 14),
				Block.box(0, 0, 2, 16, 2, 14),
				Block.box(1, 13, 2, 15, 14, 14),
				Block.box(1, 2, 2, 15, 3, 14),
				Block.box(1, 3, 13, 15, 13, 14),
				Block.box(1, 3, 2, 15, 13, 3),
				Block.box(1, 3, 7, 15, 13, 9),
				Block.box(0, 0, 14, 16, 16, 16)
			}
	));
}