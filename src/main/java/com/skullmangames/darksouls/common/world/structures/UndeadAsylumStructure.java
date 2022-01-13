package com.skullmangames.darksouls.common.world.structures;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class UndeadAsylumStructure extends ModStructure
{
	public UndeadAsylumStructure(Codec<JigsawConfiguration> codec)
	{
		super(codec, UndeadAsylumStructure::createPiecesGenerator, PostPlacementProcessor.NONE);
	}

	public static final List<MobSpawnSettings.SpawnerData> MONSTERS = ImmutableList
			.of(new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 1, 0, 0));
	public static final List<MobSpawnSettings.SpawnerData> CREATURES = ImmutableList
			.of(new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 0, 0));
	
	@Override
	public void setupSpawns(StructureSpawnListGatherEvent event)
	{
		event.addEntitySpawns(MobCategory.MONSTER, MONSTERS);
		event.addEntitySpawns(MobCategory.CREATURE, CREATURES);
	}
	
	@Override
	public Decoration step()
	{
		return Decoration.SURFACE_STRUCTURES;
	}

	private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context)
	{
		int chunkX = context.chunkPos().x;
		int chunkZ = context.chunkPos().z;
		BlockPos[] positions = new BlockPos[]
		{ new BlockPos((chunkX * 16) + 7, 0, (chunkZ * 16) + 7), new BlockPos((chunkX * 16) - 1, 0, (chunkZ * 16) - 1),
				new BlockPos((chunkX * 16) + 15, 0, (chunkZ * 16) + 15),
				new BlockPos((chunkX * 16) + 15, 0, (chunkZ * 16) - 1),
				new BlockPos((chunkX * 16) - 1, 0, (chunkZ * 16) + 15) };
		boolean flag = true;
		for (BlockPos pos : positions)
		{
			int landHeight = context.getLowestY(pos.getX(), pos.getZ());
			if (landHeight < 100)
				flag = false;
		}
		return flag;
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
						.get(new ResourceLocation(DarkSouls.MOD_ID, "undead_asylum/center_building")),
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
		return biome == BiomeCategory.EXTREME_HILLS;
	}
}
