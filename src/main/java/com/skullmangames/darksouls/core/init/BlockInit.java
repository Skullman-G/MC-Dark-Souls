package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.blocks.BonfireBlock;
import com.skullmangames.darksouls.common.blocks.SmithingTableBlockOverride;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
	
	
	// Vanilla Overrides
	public static final DeferredRegister<Block> VANILLA_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
	
	public static final RegistryObject<Block> SMITHING_TABLE = VANILLA_BLOCKS.register("smithing_table", () -> new SmithingTableBlockOverride(AbstractBlock.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
}