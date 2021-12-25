package com.skullmangames.darksouls.common.world.structures;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class UndeadAsylumStructure extends Structure<NoFeatureConfig>
{
	public UndeadAsylumStructure(Codec<NoFeatureConfig> p_i231997_1_)
	{
		super(p_i231997_1_);
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory()
	{
		return UndeadAsylumStructure.Start::new;
	}

	@Override
	public Decoration step()
	{
		return Decoration.TOP_LAYER_MODIFICATION;
	}

	private static final List<MobSpawnInfo.Spawners> CREATURES = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.PIG, 1, 0, 0));
	private static final List<MobSpawnInfo.Spawners> MONSTERS = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.ZOMBIE, 1, 0, 0));

	@Override
	public List<MobSpawnInfo.Spawners> getDefaultSpawnList()
	{
		return MONSTERS;
	}

	@Override
	public List<MobSpawnInfo.Spawners> getDefaultCreatureSpawnList()
	{
		return CREATURES;
	}

	@Override
	protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX,
			int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig)
	{
		BlockPos[] positions = new BlockPos[]
		{
				new BlockPos((chunkX * 16) + 7, 0, (chunkZ * 16) + 7),
				new BlockPos((chunkX * 16) - 1, 0, (chunkZ * 16) - 1),
				new BlockPos((chunkX * 16) + 15, 0, (chunkZ * 16) + 15),
				new BlockPos((chunkX * 16) + 15, 0, (chunkZ * 16) - 1),
				new BlockPos((chunkX * 16) - 1, 0, (chunkZ * 16) + 15)
		};
		boolean flag = true;
		for (BlockPos pos : positions)
		{
			int landHeight = chunkGenerator.getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
			if (landHeight < 100)
				flag = false;
		}
		return flag;
	}

	public static class Start extends StructureStart<NoFeatureConfig>
	{
		public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn,
				long seedIn)
		{
			super(structure, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		@Override
		public void generatePieces(DynamicRegistries dynamicregistries, ChunkGenerator generator, TemplateManager templatemanager, int chunkX,
				int chunkZ, Biome biome, NoFeatureConfig config)
		{
			int x = chunkX * 16;
			int z = chunkZ * 16;

			BlockPos centerPos = new BlockPos(x, 0, z);

			ResourceLocation startpoollocation = new ResourceLocation(DarkSouls.MOD_ID, "undead_asylum/center_building");
			JigsawManager.addPieces(dynamicregistries,
					new VillageConfig(() -> dynamicregistries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(startpoollocation), 20),
					AbstractVillagePiece::new, generator, templatemanager, centerPos, this.pieces, this.random, false, true);

			this.pieces.forEach(piece -> piece.move(0, 1, 0));

			this.calculateBoundingBox();
		}
	}
}
