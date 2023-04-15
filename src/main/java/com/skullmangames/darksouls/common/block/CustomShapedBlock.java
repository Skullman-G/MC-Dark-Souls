package com.skullmangames.darksouls.common.block;

import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CustomShapedBlock extends Block
{
	private final Optional<VoxelShape> shape;
	
	public CustomShapedBlock(BlockBehaviour.Properties properties, VoxelShape... boxes)
	{
		super(properties);
		this.shape = Stream.of(boxes).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	}
	
	@Override
	public VoxelShape getShape(BlockState blockstate, BlockGetter level, BlockPos pos, CollisionContext cc)
	{
		return this.shape.orElse(Shapes.block());
	}
}
