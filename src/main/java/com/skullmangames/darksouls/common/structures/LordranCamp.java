package com.skullmangames.darksouls.common.structures;

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

public class LordranCamp extends Structure<NoFeatureConfig>
{
	public LordranCamp()
	{
		super(NoFeatureConfig.CODEC);
	}

	@Override
	public Decoration step()
	{
		return Decoration.SURFACE_STRUCTURES;
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory()
	{
		return LordranCamp.Start::new;
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

			ResourceLocation resourcelocation = new ResourceLocation(DarkSouls.MOD_ID, "lordran_camp/start_pool");
			JigsawManager.addPieces(dynamicregistries,
					new VillageConfig(() -> dynamicregistries.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(resourcelocation), 10),
					AbstractVillagePiece::new, generator, templatemanager, blockpos, this.pieces, this.random, false, true);
			this.calculateBoundingBox();
		}
	}
}
