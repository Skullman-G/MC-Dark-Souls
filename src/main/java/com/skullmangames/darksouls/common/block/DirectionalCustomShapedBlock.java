package com.skullmangames.darksouls.common.block;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirectionalCustomShapedBlock extends HorizontalDirectionalBlock
{
	private final Optional<VoxelShape> north;
	private final Optional<VoxelShape> south;
	private final Optional<VoxelShape> east;
	private final Optional<VoxelShape> west;
	
	public DirectionalCustomShapedBlock(Properties properties, VoxelShape[] north, VoxelShape[] south, VoxelShape[] east, VoxelShape[] west)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
		this.north = Stream.of(north).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
		this.south = Stream.of(south).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
		this.east = Stream.of(east).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
		this.west = Stream.of(west).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		if (context.getLevel().isEmptyBlock(context.getClickedPos().below())) return null;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> container) 
	{
	    super.createBlockStateDefinition(container);
		container.add(FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext cc)
	{
		switch(state.getValue(FACING))
		{
			default:
			case NORTH: return this.north.orElse(Shapes.block());
			case SOUTH: return this.south.orElse(Shapes.block());
			case WEST: return this.west.orElse(Shapes.block());
			case EAST: return this.east.orElse(Shapes.block());
		}
	}
}
