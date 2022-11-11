package com.skullmangames.darksouls.common.structures;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

public class SunlightAltar extends StructureFeature<JigsawConfiguration>
{
	public SunlightAltar()
	{
		super(JigsawConfiguration.CODEC, SunlightAltar::createPiecesGenerator, PostPlacementProcessor.NONE);
	}

	@Override
	public GenerationStep.Decoration step()
	{
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
		return true;
	}

	private static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
		if (!SunlightAltar.isFeatureChunk(context)) return Optional.empty();
		
		BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);
		int topLandY = context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(),
				Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
		blockpos = blockpos.above(topLandY);

		Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator = JigsawPlacement.addPieces(context,
				PoolElementStructurePiece::new,
				blockpos,
				false,
				false
		);
		return structurePiecesGenerator;
	}
}
