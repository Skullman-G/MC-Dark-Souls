package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;

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
}
