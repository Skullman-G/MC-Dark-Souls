package com.skullmangames.darksouls.world.structures;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.core.init.StructureInit;

import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class CheckpointPlainsPieces
{
	private static final ResourceLocation STRUCTURE_CHECKPOINT_PLAINS = new ResourceLocation(DarkSouls.MOD_ID + ":checkpoint_plains");
    private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.of(STRUCTURE_CHECKPOINT_PLAINS, new BlockPos(0, 1, 0));
    
    public static void addPieces(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<StructurePiece> pieceList, Random random)
    {
        int x = pos.getX();
        int z = pos.getZ();

        BlockPos rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
        BlockPos blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.add(new CheckpointPlainsPieces.Piece(templateManager, STRUCTURE_CHECKPOINT_PLAINS, blockpos, rotation));

        rotationOffSet = new BlockPos(-10, 0, 0).rotate(rotation);
        blockpos = rotationOffSet.offset(x, pos.getY(), z);
        pieceList.add(new CheckpointPlainsPieces.Piece(templateManager, STRUCTURE_CHECKPOINT_PLAINS, blockpos, rotation));
    }
    
    public static class Piece extends TemplateStructurePiece
    {
        private ResourceLocation resourceLocation;
        private Rotation rotation;

        public Piece(TemplateManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos, Rotation rotationIn)
        {
            super(StructureInit.CHECKPOINT_PLAINS_PIECE, 0);
            this.resourceLocation = resourceLocationIn;
            BlockPos blockpos = CheckpointPlainsPieces.OFFSET.get(resourceLocation);
            this.templatePosition = pos.offset(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.rotation = rotationIn;
            this.loadTemplate(templateManagerIn);
        }

        public Piece(TemplateManager templateManagerIn, CompoundNBT tagCompound)
        {
            super(StructureInit.CHECKPOINT_PLAINS_PIECE, tagCompound);
            this.resourceLocation = new ResourceLocation(tagCompound.getString("Template"));
            this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
            this.loadTemplate(templateManagerIn);
        }

        private void loadTemplate(TemplateManager templateManager)
        {
            Template template = templateManager.getOrCreate(this.resourceLocation);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
            this.setup(template, this.templatePosition, placementsettings);
        }

        @Override
        protected void addAdditionalSaveData(CompoundNBT tagCompound)
        {
            super.addAdditionalSaveData(tagCompound);
            tagCompound.putString("Template", this.resourceLocation.toString());
            tagCompound.putString("Rot", this.rotation.name());
        }
        
        @Override
        public boolean postProcess(ISeedReader worldIn, StructureManager structuremanager, ChunkGenerator gen, Random random, MutableBoundingBox boundingbox, ChunkPos chunkpos, BlockPos blockpos)
        {
        	PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
            BlockPos blockpos1 = CheckpointPlainsPieces.OFFSET.get(this.resourceLocation);
            this.templatePosition.offset(Template.calculateRelativePosition(placementsettings, new BlockPos(0 - blockpos.getX(), 0, 0 - blockpos.getZ())));
            return super.postProcess(worldIn, structuremanager, gen, random, boundingbox, chunkpos, blockpos);
        }


		@Override
		protected void handleDataMarker(String name, BlockPos pos, IServerWorld world, Random random, MutableBoundingBox boundingbox)
		{
			if ("chest".equals(name))
			{
	            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	            TileEntity tileentity = world.getBlockEntity(pos.below());
	            if (tileentity instanceof ChestTileEntity) {
	               ((ChestTileEntity)tileentity).setLootTable(LootTables.WOODLAND_MANSION, random.nextLong());
	            }
	        }
		}
    }
}
