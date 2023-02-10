package com.skullmangames.darksouls.common.block;

import com.skullmangames.darksouls.common.blockentity.LightSourceBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModBlocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;

public class LightSource extends Block
{
	public static final IntegerProperty LIGHT_EMISSION = IntegerProperty.create("light_emission", 0, 100);
	
	public LightSource()
	{
		super(Properties.of(Material.AIR));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_EMISSION, 1));
	}
	
	@SuppressWarnings("deprecation")
	public static void setLightSource(World level, BlockPos pos, int lightEmission, float time)
	{
		BlockState state = ModBlocks.LIGHT_SOURCE.get().defaultBlockState().setValue(LIGHT_EMISSION, lightEmission);
		if (level.getBlockState(pos).isAir(level, pos))
		{
			level.setBlock(pos, state, 3);
			TileEntity te = level.getBlockEntity(pos);
			LightSourceBlockEntity blockEntity = te instanceof LightSourceBlockEntity ? (LightSourceBlockEntity)te : null;
			if (blockEntity != null)
			{
				blockEntity.setTimer(time);
			}
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(LIGHT_EMISSION);
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader level, BlockPos pos)
	{
		return state.getValue(LIGHT_EMISSION);
	}
	
	public BlockRenderType getRenderShape(BlockState p_48758_)
	{
		return BlockRenderType.INVISIBLE;
	}

	public VoxelShape getShape(BlockState p_48760_, IBlockReader p_48761_, BlockPos p_48762_, ISelectionContext p_48763_)
	{
		return VoxelShapes.empty();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return ModBlockEntities.LIGHT_SOURCE.get().create();
	}
}
