package com.skullmangames.darksouls.common.structures;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FirelinkShrine extends AbstractSurfaceStructure
{
	public FirelinkShrine()
	{
		super(NoFeatureConfig.CODEC, "firelink_shrine/church");
	}

	@Override
	public Decoration step()
	{
		return Decoration.SURFACE_STRUCTURES;
	}
	
	@Override
	public boolean canGenerateHere(DynamicRegistries dynamicregistries, ChunkGenerator chunkGenerator, TemplateManager templatemanager, int chunkX,
			int chunkZ, Biome biome, NoFeatureConfig config)
	{
		BlockPos centerOfChunk = new BlockPos((chunkX << 4) + 7, 0, (chunkZ << 4) + 7);
        int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        return landHeight >= chunkGenerator.getSeaLevel();
	}
}
