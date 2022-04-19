package com.skullmangames.darksouls.common.structures;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

public class FirelinkShrine extends StructureFeature<JigsawConfiguration>
{
	public FirelinkShrine()
	{
		super(JigsawConfiguration.CODEC, FirelinkShrine::createPiecesGenerator);
	}

	@Override
	public GenerationStep.Decoration step()
	{
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}

	private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
		return context.getLowestY(12, 15) >= context.chunkGenerator().getSeaLevel();
	}

	private static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
		if (!FirelinkShrine.checkLocation(context)) return Optional.empty();
		
		BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);
		int topLandY = context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(),
				Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
		blockpos = blockpos.above(topLandY);

		Optional<PieceGenerator<JigsawConfiguration>> generator = JigsawPlacement.addPieces(context,
				PoolElementStructurePiece::new,
				blockpos,
				false,
				false
		);
		
		return generator;
	}
}
