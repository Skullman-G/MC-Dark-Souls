package com.skullmangames.darksouls.common.world.structures;

import java.util.Random;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
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
	
	@Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig)
	{
        BlockPos centerOfChunk = new BlockPos((chunkX << 4) + 7, 0, (chunkZ << 4) + 7);
        int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        return landHeight > 100;
    }
	
	public static class Start extends StructureStart<NoFeatureConfig>
	{
		public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn)
	    {
			super(structure, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
	    }

		@Override
		public void generatePieces(DynamicRegistries dynamicregistries, ChunkGenerator generator, TemplateManager templatemanager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config)
		{
			int x = chunkX * 16;
            int z = chunkZ * 16;
            
            BlockPos centerPos = new BlockPos(x, 0, z);
            
            ResourceLocation startpoollocation = new ResourceLocation(DarkSouls.MOD_ID, "undead_asylum/center_building");
            JigsawManager.addPieces(dynamicregistries, new VillageConfig(() -> dynamicregistries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(startpoollocation), 20), AbstractVillagePiece::new, generator, templatemanager, centerPos, this.pieces, this.random, false, true);
            
            this.pieces.forEach(piece -> piece.move(0, 1, 0));
            
            this.calculateBoundingBox();
		}
		
		@Override
		public void placeInChunk(ISeedReader seedReader, StructureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, MutableBoundingBox bb, ChunkPos p_230366_6_)
		{
			super.placeInChunk(seedReader, p_230366_2_, p_230366_3_, p_230366_4_, bb, p_230366_6_);
			
			int y = this.boundingBox.y0;

			for(int x = bb.x0; x <= bb.x1; ++x)
			{
	            for(int z = bb.z0; z <= bb.z1; ++z)
	            {
	            	BlockPos blockpos = new BlockPos(x, y, z);
	                if (seedReader.isEmptyBlock(blockpos) || !this.boundingBox.isInside(blockpos)) continue;
	                
	                boolean flag = false;

	                for(StructurePiece structurepiece : this.pieces)
	                {
	                   if (structurepiece.getBoundingBox().isInside(blockpos))
	                   {
	                	   flag = true;
	                       break;
	                   }
	                }

	                if (flag)
	                {
	                	for(int l = y - 1; l > 1; --l)
	                    {
	                       BlockPos blockpos1 = new BlockPos(x, l, z);
	                       if (!seedReader.isEmptyBlock(blockpos1) && !seedReader.getBlockState(blockpos1).getMaterial().isLiquid()) break;

	                       seedReader.setBlock(blockpos1, Blocks.STONE.defaultBlockState(), 2);
	                    }
	                }
	            }
			}
		}
	}
}
