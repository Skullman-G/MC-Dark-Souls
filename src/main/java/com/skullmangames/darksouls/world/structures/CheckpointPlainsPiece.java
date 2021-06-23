package com.skullmangames.darksouls.world.structures;

import java.util.Random;

import com.skullmangames.darksouls.core.init.EntityTypeInit;
import com.skullmangames.darksouls.core.init.StructureInit;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class CheckpointPlainsPiece extends TemplateStructurePiece
{
	private final ResourceLocation templateLocation;
	private final Rotation rotation;
	
	public CheckpointPlainsPiece(TemplateManager templatemanager, BlockPos templatepos, ResourceLocation templatelocation, Rotation rotation)
	{
		super(StructureInit.CHECKPOINT_PLAINS_PIECE, 0);
		this.templateLocation = templatelocation;
		this.rotation = rotation;
		this.templatePosition = templatepos;
		this.loadTemplate(templatemanager);
	}
	
	public CheckpointPlainsPiece(TemplateManager templatemanager, CompoundNBT nbt)
	{
		super(StructureInit.CHECKPOINT_PLAINS_PIECE, nbt);
		this.templateLocation = new ResourceLocation(nbt.getString("Template"));
		this.rotation = Rotation.valueOf(nbt.getString("Rotation"));
		this.loadTemplate(templatemanager);
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putString("Template", this.templateLocation.toString());
	    nbt.putString("Rotation", this.rotation.name());
	}
	
	private void loadTemplate(TemplateManager templatemanager)
	{
		Template template = templatemanager.getOrCreate(this.templateLocation);
		BlockPos pivotpoint = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(pivotpoint).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
        this.setup(template, this.templatePosition, placementsettings);
	}

	@Override
	protected void handleDataMarker(String function, BlockPos p_186175_2_, IServerWorld world, Random p_186175_4_, MutableBoundingBox p_186175_5_)
	{
		if (function == "firekeeper")
		{
			world.addFreshEntity(EntityTypeInit.FIRE_KEEPER.get().create(world.getLevel()));
		}
	}
}
