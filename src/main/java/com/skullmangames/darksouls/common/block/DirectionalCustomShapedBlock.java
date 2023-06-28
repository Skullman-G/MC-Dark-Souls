package com.skullmangames.darksouls.common.block;

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
	private final VoxelShape north;
	private final VoxelShape south;
	private final VoxelShape west;
	private final VoxelShape east;
	
	public DirectionalCustomShapedBlock(Properties properties, VoxelShape[] north)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
		this.north = Stream.of(north).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.block());
		this.south = rotateShape(Direction.NORTH, Direction.SOUTH, this.north);
		this.west = rotateShape(Direction.NORTH, Direction.WEST, this.north);
		this.east = rotateShape(Direction.NORTH, Direction.EAST, this.north);
	}
	
	public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
	{
		VoxelShape[] buffer = new VoxelShape[]
		{ shape, Shapes.empty() };

		int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
		for (int i = 0; i < times; i++)
		{
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
					Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
		}

		return buffer[0];
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
			case NORTH: return this.north;
			case SOUTH: return this.south;
			case WEST: return this.west;
			case EAST: return this.east;
		}
	}
}
