package com.skullmangames.darksouls.common.structures;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class AbstractSurfaceStructure extends Structure<NoFeatureConfig>
{
	private final String startPool;
	
	public AbstractSurfaceStructure(Codec<NoFeatureConfig> codec, String startPool)
	{
		super(codec);
		this.startPool = startPool;
	}

	@Override
	public Decoration step()
	{
		return Decoration.SURFACE_STRUCTURES;
	}
	
	public boolean canGenerateHere(DynamicRegistries dynamicregistries, ChunkGenerator generator, TemplateManager templatemanager, int chunkX,
				int chunkZ, Biome biome, NoFeatureConfig config)
	{
		return true;
	}
	
	public BlockPos placeAt(DynamicRegistries dynamicregistries, ChunkGenerator generator, TemplateManager templatemanager, int chunkX,
				int chunkZ, Biome biome, NoFeatureConfig config)
	{
		int x = (chunkX << 4) + 7;
		int z = (chunkZ << 4) + 7;
		return new BlockPos(x, 0, z);
	}
	
	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory()
	{
		return AbstractSurfaceStructure.Start::new;
	}
	
	private static class Start extends StructureStart<NoFeatureConfig>
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
			AbstractSurfaceStructure structure = ((AbstractSurfaceStructure)this.getFeature());
			if (!structure.canGenerateHere(dynamicregistries, generator, templatemanager, chunkX, chunkZ, biome, config)) return;
			BlockPos blockpos = structure.placeAt(dynamicregistries, generator, templatemanager, chunkX, chunkZ, biome, config);
			
			ResourceLocation resourcelocation = new ResourceLocation(DarkSouls.MOD_ID, structure.startPool);
			JigsawManager.addPieces(dynamicregistries,
					new VillageConfig(() -> dynamicregistries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(resourcelocation), 10),
					AbstractVillagePiece::new, generator, templatemanager, blockpos, this.pieces, this.random, false, true);
			this.calculateBoundingBox();
		}
	}
}
