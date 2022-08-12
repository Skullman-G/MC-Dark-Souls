package com.skullmangames.darksouls.common.structures;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
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

public class FirelinkShrine extends Structure<NoFeatureConfig>
{
	public FirelinkShrine()
	{
		super(NoFeatureConfig.CODEC);
	}

	@Override
	public Decoration step()
	{
		return Decoration.SURFACE_STRUCTURES;
	}
	
	@Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig)
	{
        BlockPos centerOfChunk = new BlockPos((chunkX << 4) + 7, 0, (chunkZ << 4) + 7);
        int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        return landHeight >= chunkGenerator.getSeaLevel();
    }

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory()
	{
		return FirelinkShrine.Start::new;
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
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;
			BlockPos blockpos = new BlockPos(x, 0, z);

			ResourceLocation resourcelocation = new ResourceLocation(DarkSouls.MOD_ID, "firelink_shrine/bonfire");
			JigsawManager.addPieces(dynamicregistries,
					new VillageConfig(() -> dynamicregistries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(resourcelocation), 10),
					AbstractVillagePiece::new, generator, templatemanager, blockpos, this.pieces, this.random, false, true);
			this.calculateBoundingBox();
		}
	}
}
