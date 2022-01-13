package com.skullmangames.darksouls.common.world.structures;

import com.mojang.serialization.Codec;

import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;

public abstract class ModStructure extends StructureFeature<JigsawConfiguration>
{
	public ModStructure(Codec<JigsawConfiguration> p_197168_, PieceGeneratorSupplier<JigsawConfiguration> p_197169_,
			PostPlacementProcessor p_197170_)
	{
		super(p_197168_, p_197169_, p_197170_);
	}
	
	public ConfiguredStructureFeature<JigsawConfiguration, ModStructure> configured()
	{
		return  new ConfiguredStructureFeature<JigsawConfiguration, ModStructure>(this, new JigsawConfiguration(() -> PlainVillagePools.START, 0));
	}
	
	public void setupSpawns(StructureSpawnListGatherEvent event) {}
	
	public abstract boolean canSpawnInBiome(BiomeCategory biome);
}
