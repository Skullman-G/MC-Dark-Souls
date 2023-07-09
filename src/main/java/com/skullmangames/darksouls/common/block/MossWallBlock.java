package com.skullmangames.darksouls.common.block;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MossWallBlock extends Block implements net.minecraftforge.common.IForgeShearable
{
	public static final BooleanProperty UP = PipeBlock.UP;
	public static final BooleanProperty NORTH = PipeBlock.NORTH;
	public static final BooleanProperty EAST = PipeBlock.EAST;
	public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
	public static final BooleanProperty WEST = PipeBlock.WEST;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION
			.entrySet().stream().filter((p_57886_) ->
			{
				return p_57886_.getKey() != Direction.DOWN;
			}).collect(Util.toMap());
	protected static final float AABB_OFFSET = 1.0F;
	private final Map<BlockState, VoxelShape> shapesCache;

	public MossWallBlock(BlockBehaviour.Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.valueOf(false))
				.setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false))
				.setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)));
		this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream()
				.collect(Collectors.toMap(Function.identity(), MossWallBlock::calculateShape)));
	}

	private static VoxelShape calculateShape(BlockState blockState)
	{
		VoxelShape north = Block.box(0, 0, 0, 16, 16, 2);
		VoxelShape voxelShape = Shapes.empty();
		
		if (blockState.getValue(UP))
		{
			voxelShape = Block.box(0, 14, 0, 16, 16, 16);
		}
		
		if (blockState.getValue(NORTH))
		{
			voxelShape = Shapes.or(voxelShape, north);
		}

		if (blockState.getValue(SOUTH))
		{
			voxelShape = Shapes.or(voxelShape, rotateShapeHorizontal(Direction.SOUTH, north));
		}

		if (blockState.getValue(EAST))
		{
			voxelShape = Shapes.or(voxelShape, rotateShapeHorizontal(Direction.EAST, north));
		}

		if (blockState.getValue(WEST))
		{
			voxelShape = Shapes.or(voxelShape, rotateShapeHorizontal(Direction.WEST, north));
		}

		return voxelShape.isEmpty() ? Shapes.block() : voxelShape;
	}
	
	public static VoxelShape rotateShapeHorizontal(Direction to, VoxelShape shape)
	{
		VoxelShape[] buffer = new VoxelShape[]
		{ shape, Shapes.empty() };

		int times = (to.get2DDataValue() - 2 + 4) % 4;
		for (int i = 0; i < times; i++)
		{
			buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
					Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
			buffer[0] = buffer[1];
			buffer[1] = Shapes.empty();
		}

		return buffer[0];
	}

	public VoxelShape getShape(BlockState blockstate, BlockGetter level, BlockPos pos, CollisionContext collision)
	{
		return this.shapesCache.get(blockstate);
	}

	public boolean propagatesSkylightDown(BlockState blockstate, BlockGetter level, BlockPos pos)
	{
		return true;
	}

	public boolean canSurvive(BlockState blockstate, LevelReader level, BlockPos pos)
	{
		return this.hasFaces(this.getUpdatedState(blockstate, level, pos));
	}

	private boolean hasFaces(BlockState blockstate)
	{
		return this.countFaces(blockstate) > 0;
	}

	private int countFaces(BlockState blockstate)
	{
		int i = 0;

		for (BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values())
		{
			if (blockstate.getValue(booleanproperty)) ++i;
		}

		return i;
	}

	private boolean canSupportAtFace(BlockGetter level, BlockPos pos, Direction direction)
	{
		if (direction == Direction.DOWN)
		{
			return false;
		}
		else
		{
			BlockPos blockpos = pos.relative(direction);
			if (isAcceptableNeighbour(level, blockpos, direction))
			{
				return true;
			}
			else if (direction.getAxis() == Direction.Axis.Y)
			{
				return false;
			}
			else
			{
				BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(direction);
				BlockState blockstate = level.getBlockState(pos.above());
				return blockstate.is(this) && blockstate.getValue(booleanproperty);
			}
		}
	}

	public static boolean isAcceptableNeighbour(BlockGetter level, BlockPos pos, Direction direction)
	{
		BlockState blockstate = level.getBlockState(pos);
		return Block.isFaceFull(blockstate.getCollisionShape(level, pos), direction.getOpposite());
	}

	private BlockState getUpdatedState(BlockState blockstate, BlockGetter level, BlockPos pos)
	{
		BlockPos blockpos = pos.above();
		if (blockstate.getValue(UP))
		{
			blockstate = blockstate.setValue(UP,
					Boolean.valueOf(isAcceptableNeighbour(level, blockpos, Direction.DOWN)));
		}

		BlockState otherBlockState = null;

		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			BooleanProperty booleanproperty = getPropertyForFace(direction);
			if (blockstate.getValue(booleanproperty))
			{
				boolean flag = this.canSupportAtFace(level, pos, direction);
				if (!flag)
				{
					if (otherBlockState == null)
					{
						otherBlockState = level.getBlockState(blockpos);
					}

					flag = otherBlockState.is(this) && otherBlockState.getValue(booleanproperty);
				}

				blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(flag));
			}
		}

		return blockstate;
	}

	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState blockstate, Direction direction, BlockState newBlockState, LevelAccessor level,
			BlockPos p_57879_, BlockPos p_57880_)
	{
		if (direction == Direction.DOWN)
		{
			return super.updateShape(blockstate, direction, newBlockState, level, p_57879_, p_57880_);
		}
		else
		{
			BlockState updatedState = this.getUpdatedState(blockstate, level, p_57879_);
			return !this.hasFaces(updatedState) ? Blocks.AIR.defaultBlockState() : updatedState;
		}
	}

	@SuppressWarnings("deprecation")
	public void randomTick(BlockState blockstateIn, ServerLevel level, BlockPos pos, Random random)
	{
		if (level.random.nextInt(4) == 0 && level.isAreaLoaded(pos, 4))
		{ // Forge: check area to prevent loading unloaded chunks
			Direction direction = Direction.getRandom(random);
			BlockPos blockpos = pos.above();
			if (direction.getAxis().isHorizontal() && !blockstateIn.getValue(getPropertyForFace(direction)))
			{
				if (this.canSpread(level, pos))
				{
					BlockPos blockpos4 = pos.relative(direction);
					BlockState blockstate4 = level.getBlockState(blockpos4);
					if (blockstate4.isAir())
					{
						Direction direction3 = direction.getClockWise();
						Direction direction4 = direction.getCounterClockWise();
						boolean flag = blockstateIn.getValue(getPropertyForFace(direction3));
						boolean flag1 = blockstateIn.getValue(getPropertyForFace(direction4));
						BlockPos blockpos2 = blockpos4.relative(direction3);
						BlockPos blockpos3 = blockpos4.relative(direction4);
						if (flag && isAcceptableNeighbour(level, blockpos2, direction3))
						{
							level.setBlock(blockpos4, this.defaultBlockState()
									.setValue(getPropertyForFace(direction3), Boolean.valueOf(true)), 2);
						} else if (flag1 && isAcceptableNeighbour(level, blockpos3, direction4))
						{
							level.setBlock(blockpos4, this.defaultBlockState()
									.setValue(getPropertyForFace(direction4), Boolean.valueOf(true)), 2);
						} else
						{
							Direction direction1 = direction.getOpposite();
							if (flag && level.isEmptyBlock(blockpos2)
									&& isAcceptableNeighbour(level, pos.relative(direction3), direction1))
							{
								level.setBlock(blockpos2, this.defaultBlockState()
										.setValue(getPropertyForFace(direction1), Boolean.valueOf(true)), 2);
							} else if (flag1 && level.isEmptyBlock(blockpos3)
									&& isAcceptableNeighbour(level, pos.relative(direction4), direction1))
							{
								level.setBlock(blockpos3, this.defaultBlockState()
										.setValue(getPropertyForFace(direction1), Boolean.valueOf(true)), 2);
							} else if ((double) random.nextFloat() < 0.05D
									&& isAcceptableNeighbour(level, blockpos4.above(), Direction.UP))
							{
								level.setBlock(blockpos4,
										this.defaultBlockState().setValue(UP, Boolean.valueOf(true)), 2);
							}
						}
					} else if (isAcceptableNeighbour(level, blockpos4, direction))
					{
						level.setBlock(pos,
								blockstateIn.setValue(getPropertyForFace(direction), Boolean.valueOf(true)), 2);
					}

				}
			}
			else
			{
				if (direction == Direction.UP && pos.getY() < level.getMaxBuildHeight() - 1)
				{
					if (this.canSupportAtFace(level, pos, direction))
					{
						level.setBlock(pos, blockstateIn.setValue(UP, Boolean.valueOf(true)), 2);
						return;
					}

					if (level.isEmptyBlock(blockpos))
					{
						if (!this.canSpread(level, pos))
						{
							return;
						}

						BlockState blockstate3 = blockstateIn;

						for (Direction direction2 : Direction.Plane.HORIZONTAL)
						{
							if (random.nextBoolean()
									|| !isAcceptableNeighbour(level, blockpos.relative(direction2), direction2))
							{
								blockstate3 = blockstate3.setValue(getPropertyForFace(direction2),
										Boolean.valueOf(false));
							}
						}

						if (this.hasHorizontalConnection(blockstate3))
						{
							level.setBlock(blockpos, blockstate3, 2);
						}

						return;
					}
				}

				if (pos.getY() > level.getMinBuildHeight())
				{
					BlockPos blockpos1 = pos.below();
					BlockState blockstate = level.getBlockState(blockpos1);
					if (blockstate.isAir() || blockstate.is(this))
					{
						BlockState blockstate1 = blockstate.isAir() ? this.defaultBlockState() : blockstate;
						BlockState blockstate2 = this.copyRandomFaces(blockstateIn, blockstate1, random);
						if (blockstate1 != blockstate2 && this.hasHorizontalConnection(blockstate2))
						{
							level.setBlock(blockpos1, blockstate2, 2);
						}
					}
				}

			}
		}
	}

	private BlockState copyRandomFaces(BlockState blockstate, BlockState copy, Random random)
	{
		for (Direction direction : Direction.Plane.HORIZONTAL)
		{
			if (random.nextBoolean())
			{
				BooleanProperty booleanproperty = getPropertyForFace(direction);
				if (blockstate.getValue(booleanproperty))
				{
					copy = copy.setValue(booleanproperty, Boolean.valueOf(true));
				}
			}
		}

		return copy;
	}

	private boolean hasHorizontalConnection(BlockState blockstate)
	{
		return blockstate.getValue(NORTH) || blockstate.getValue(EAST) || blockstate.getValue(SOUTH)
				|| blockstate.getValue(WEST);
	}

	private boolean canSpread(BlockGetter level, BlockPos pos)
	{
		int i = 4;
		Iterable<BlockPos> iterable = BlockPos.betweenClosed(pos.getX() - i, pos.getY() - 1,
				pos.getZ() - i, pos.getX() + i, pos.getY() + 1, pos.getZ() + i);
		int j = 5;

		for (BlockPos blockpos : iterable)
		{
			if (level.getBlockState(blockpos).is(this))
			{
				--j;
				if (j <= 0)
				{
					return false;
				}
			}
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean canBeReplaced(BlockState thisAsState, BlockPlaceContext ctx)
	{
		BlockState blockstate = ctx.getLevel().getBlockState(ctx.getClickedPos());
		if (blockstate.is(this))
		{
			return this.countFaces(blockstate) < PROPERTY_BY_DIRECTION.size();
		}
		else return super.canBeReplaced(thisAsState, ctx);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		BlockState blockstate = ctx.getLevel().getBlockState(ctx.getClickedPos());
		boolean flag = blockstate.is(this);
		BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

		for (Direction direction : ctx.getNearestLookingDirections())
		{
			if (direction != Direction.DOWN)
			{
				BooleanProperty booleanproperty = getPropertyForFace(direction);
				boolean flag1 = flag && blockstate.getValue(booleanproperty);
				if (!flag1 && this.canSupportAtFace(ctx.getLevel(), ctx.getClickedPos(), direction))
				{
					return blockstate1.setValue(booleanproperty, Boolean.valueOf(true));
				}
			}
		}

		return flag ? blockstate1 : null;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(UP, NORTH, EAST, SOUTH, WEST);
	}

	public BlockState rotate(BlockState blockstate, Rotation rotation)
	{
		switch (rotation)
		{
		case CLOCKWISE_180:
			return blockstate.setValue(NORTH, blockstate.getValue(SOUTH)).setValue(EAST, blockstate.getValue(WEST))
					.setValue(SOUTH, blockstate.getValue(NORTH)).setValue(WEST, blockstate.getValue(EAST));
		case COUNTERCLOCKWISE_90:
			return blockstate.setValue(NORTH, blockstate.getValue(EAST)).setValue(EAST, blockstate.getValue(SOUTH))
					.setValue(SOUTH, blockstate.getValue(WEST)).setValue(WEST, blockstate.getValue(NORTH));
		case CLOCKWISE_90:
			return blockstate.setValue(NORTH, blockstate.getValue(WEST)).setValue(EAST, blockstate.getValue(NORTH))
					.setValue(SOUTH, blockstate.getValue(EAST)).setValue(WEST, blockstate.getValue(SOUTH));
		default:
			return blockstate;
		}
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState blockstate, Mirror mirror)
	{
		switch (mirror)
		{
		case LEFT_RIGHT:
			return blockstate.setValue(NORTH, blockstate.getValue(SOUTH)).setValue(SOUTH, blockstate.getValue(NORTH));
		case FRONT_BACK:
			return blockstate.setValue(EAST, blockstate.getValue(WEST)).setValue(WEST, blockstate.getValue(EAST));
		default:
			return super.mirror(blockstate, mirror);
		}
	}

	public static BooleanProperty getPropertyForFace(Direction direction)
	{
		return PROPERTY_BY_DIRECTION.get(direction);
	}
}
