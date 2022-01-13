package com.skullmangames.darksouls.common.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseHorizontalBlock extends Block
{
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	protected static final Map<Direction, VoxelShape> SHAPES = new HashMap<Direction, VoxelShape>();
	
	public BaseHorizontalBlock(Properties properties)
	{
		super(properties);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_)
	{
		return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos vertex, Rotation direction)
	{
		return state.setValue(HORIZONTAL_FACING, direction.rotate(state.getValue(HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}
	
	protected void runCalculation(VoxelShape shape)
	{
		for (Direction direction : Direction.values())
		{
			if (direction != Direction.UP && direction != Direction.DOWN) SHAPES.put(direction, shape.getFaceShape(direction));
		}
	}
}
