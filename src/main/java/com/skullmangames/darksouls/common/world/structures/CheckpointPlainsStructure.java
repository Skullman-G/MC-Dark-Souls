package com.skullmangames.darksouls.common.world.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class CheckpointPlainsStructure extends ModStructure
{
	public CheckpointPlainsStructure(Codec<JigsawConfiguration> codec)
    {
		super(codec, UndeadAsylumStructure::createPiecesGenerator, PostPlacementProcessor.NONE);
	}
	
    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
    	ChunkPos chunk = context.chunkPos();
		BlockPos centerOfChunk = new BlockPos((chunk.x << 4) + 7, 0, (chunk.z << 4) + 7);
        int landHeight = context.getLowestY(centerOfChunk.getX(), centerOfChunk.getZ());
        NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ(), context.heightAccessor());
        BlockState topBlock = columnOfBlocks.getBlock(landHeight);
        return topBlock.getFluidState().isEmpty();
    }
    
    @Override
    public Decoration step()
    {
    	return Decoration.SURFACE_STRUCTURES;
    }
	
	public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(
			PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
		if (!isFeatureChunk(context))
		{
			return Optional.empty();
		}

		JigsawConfiguration newConfig = new JigsawConfiguration(
				() -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
						.get(new ResourceLocation(DarkSouls.MOD_ID, "checkpoint_plains/checkpoint_plains")),
				10);

		PieceGeneratorSupplier.Context<JigsawConfiguration> newContext = new PieceGeneratorSupplier.Context<>(
				context.chunkGenerator(), context.biomeSource(), context.seed(), context.chunkPos(), newConfig,
				context.heightAccessor(), context.validBiome(), context.structureManager(), context.registryAccess());

		BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);

		Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator = JigsawPlacement.addPieces(newContext,
				PoolElementStructurePiece::new, blockpos, false, true);

		return structurePiecesGenerator;
	}

	@Override
	public boolean canSpawnInBiome(BiomeCategory biome)
	{
		return biome != BiomeCategory.NETHER && biome != BiomeCategory.NONE && biome != BiomeCategory.THEEND && biome != BiomeCategory.OCEAN;
	}
}
