package com.skullmangames.darksouls.common.blocks;

import java.util.stream.Stream;

import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class Bonfire extends BaseHorizontalBlock
{
	public Bonfire() 
	{
		super(AbstractBlock.Properties.of(Material.DIRT)
				.strength(15f)
				.sound(SoundType.GRAVEL));
		runCalculation(Stream.of(
				Block.box(2, 0.75, 2, 14, 1.75, 14),
				Block.box(1, 0, 1, 15, 1, 15),
				Block.box(4, 2, 4, 12, 3, 12),
				Block.box(6, 3, 6, 10, 4, 10),
				Block.box(7, 4, 8, 8, 20, 9),
				Block.box(6, 15, 8, 7, 16, 9),
				Block.box(8, 15, 8, 9, 16, 9)
				).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
		return SHAPES.get(state.getValue(HORIZONTAL_FACING));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) 
	{
		return super.hasTileEntity(state);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) 
	{
		return TileEntityTypeInit.BONFIRE_TILE_ENTITY.get().create();
	}
}
