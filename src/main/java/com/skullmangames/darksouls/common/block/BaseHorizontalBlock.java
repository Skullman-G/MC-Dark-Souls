package com.skullmangames.darksouls.common.block;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;

public class BaseHorizontalBlock extends Block
{
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	protected static final Map<Direction, VoxelShape> SHAPES = new HashMap<Direction, VoxelShape>();
	
	public BaseHorizontalBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_)
	{
		return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState state, IWorld level, BlockPos vertex, Rotation direction)
	{
		return state.setValue(HORIZONTAL_FACING, direction.rotate(state.getValue(HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}
	
	protected static void calculateShapes(Direction to, VoxelShape shape)
	{
		VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };

		int times;
		
		switch (to)
		{
			default:
			case NORTH:
				times = 0;
				break;
				
			case EAST:
				times = 1;
				break;
				
			case SOUTH:
				times = 2;
				break;
				
			case WEST:
				times = 3;
				break;
		}
		
		for (int i = 0; i < times; i++)
		{
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(buffer[1],
					VoxelShapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = VoxelShapes.empty();
		}

		SHAPES.put(to, buffer[0]);
	}

	protected void runCalculation(VoxelShape shape)
	{
		for (Direction direction : Direction.values())
		{
			if (direction != Direction.UP && direction != Direction.DOWN) calculateShapes(direction, shape);
		}
	}
}
