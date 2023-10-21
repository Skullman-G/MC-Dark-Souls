package com.skullmangames.darksouls.common.block;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.state.properties.TrippleBlockPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BigDoorBlock extends HorizontalDirectionalBlock
{
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<TrippleBlockPart> PART = EnumProperty.create("part", TrippleBlockPart.class);
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
	
	public BigDoorBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, Boolean.valueOf(false)).setValue(PART, TrippleBlockPart.LOWER));
	}
	
	@Override
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_)
	{
		Direction direction = p_220053_1_.getValue(FACING);
	    boolean flag = !p_220053_1_.getValue(OPEN);
	    boolean flag1 = p_220053_1_.getValue(HINGE) == DoorHingeSide.RIGHT;
	    switch(direction)
	    {
		    case EAST:
		    default:
		       return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
		    case SOUTH:
		       return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
		    case WEST:
		       return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
		    case NORTH:
		       return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
	    }
	}
	
	public BlockPos[] getPartPositions(Level world, BlockPos blockpos)
	{
		BlockPos[] positions = new BlockPos[3];
		positions[0] = blockpos;
		switch (world.getBlockState(blockpos).getValue(PART))
		{
			case LOWER:
				positions[1] = blockpos.above();
				positions[2] = blockpos.above(2);
				break;
				
			case UPPER:
				positions[1] = blockpos.below();
				positions[2] = blockpos.below(2);
				break;
			
			case MIDDLE:
				positions[1] = blockpos.below();
				positions[2] = blockpos.above();
				break;
				
			default:
				throw new IndexOutOfBoundsException("Invalid big door part.");
		}
		
		return positions;
	}
	
	@Override
	public BlockState updateShape(BlockState blockstate, Direction direction, BlockState blockstate2, LevelAccessor world, BlockPos p_196271_5_, BlockPos p_196271_6_)
	{
		TrippleBlockPart trippleblockpart = blockstate.getValue(PART);
	    if (direction.getAxis() == Direction.Axis.Y && (trippleblockpart == TrippleBlockPart.LOWER || trippleblockpart == TrippleBlockPart.MIDDLE || trippleblockpart == TrippleBlockPart.UPPER == (direction == Direction.DOWN)))
	    {
	    	return blockstate2.is(this) && blockstate2.getValue(PART) != trippleblockpart ? blockstate.setValue(FACING, blockstate2.getValue(FACING)).setValue(OPEN, blockstate2.getValue(OPEN)).setValue(HINGE, blockstate2.getValue(HINGE)).setValue(POWERED, blockstate2.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
	    }
	    else
	    {
	    	return trippleblockpart == TrippleBlockPart.LOWER && direction == Direction.DOWN && !blockstate.canSurvive(world, p_196271_5_) ? Blocks.AIR.defaultBlockState() : blockstate;
	    }
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos blockpos, BlockState blockstate, Player player)
	{
		if (!level.isClientSide && player.isCreative())
		{
			switch (blockstate.getValue(PART))
			{
				case UPPER:
					BlockPos middleblockpos = blockpos.below();
				    BlockState middleblockstate = level.getBlockState(middleblockpos);
				    
				    if (middleblockstate.getBlock() == blockstate.getBlock() && middleblockstate.getValue(PART) == TrippleBlockPart.MIDDLE)
				    {
				       level.setBlock(middleblockpos, Blocks.AIR.defaultBlockState(), 35);
				       level.levelEvent(player, 2001, middleblockpos, Block.getId(middleblockstate));
				    }
				    
				    BlockPos lowerblockpos = blockpos.below(2);
				    BlockState lowerblockstate = level.getBlockState(lowerblockpos);
				    
				    if (lowerblockstate.getBlock() == blockstate.getBlock() && lowerblockstate.getValue(PART) == TrippleBlockPart.MIDDLE)
				    {
				       level.setBlock(lowerblockpos, Blocks.AIR.defaultBlockState(), 35);
				       level.levelEvent(player, 2001, lowerblockpos, Block.getId(lowerblockstate));
				    }
					break;
					
				case MIDDLE:
				    BlockPos lowerblockpos2 = blockpos.below();
				    BlockState lowerblockstate2 = level.getBlockState(lowerblockpos2);
				    
				    if (lowerblockstate2.getBlock() == blockstate.getBlock() && lowerblockstate2.getValue(PART) == TrippleBlockPart.MIDDLE)
				    {
				       level.setBlock(lowerblockpos2, Blocks.AIR.defaultBlockState(), 35);
				       level.levelEvent(player, 2001, lowerblockpos2, Block.getId(lowerblockstate2));
				    }
					break;
					
				default:
					break;
			}
	    }

	    super.playerWillDestroy(level, blockpos, blockstate, player);
	}
	
	@Override
	public boolean isPathfindable(BlockState p_196266_1_, BlockGetter p_196266_2_, BlockPos p_196266_3_, PathComputationType p_196266_4_)
	{
		switch(p_196266_4_)
	    {
	      case LAND:
	         return p_196266_1_.getValue(OPEN);
	         
	      case WATER:
	         return false;
	         
	      case AIR:
	         return p_196266_1_.getValue(OPEN);
	         
	      default:
	         return false;
	    }
	 }
	
	private int getCloseSound()
	{
	   return this.material == Material.METAL ? 1011 : 1012;
	}

	private int getOpenSound()
	{
	   return this.material == Material.METAL ? 1005 : 1006;
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		BlockPos blockpos = ctx.getClickedPos();
	    if (blockpos.getY() < 255 && ctx.getLevel().getBlockState(blockpos.above()).canBeReplaced(ctx))
	    {
	       Level level = ctx.getLevel();
	       boolean flag = level.hasNeighborSignal(blockpos) || level.hasNeighborSignal(blockpos.above());
	       return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(HINGE, this.getHinge(ctx)).setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)).setValue(PART, TrippleBlockPart.LOWER);
	    }
	    else return null;
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos blockpos, BlockState blockstate, LivingEntity entity, ItemStack itemstack)
	{
		level.setBlock(blockpos.above(), blockstate.setValue(PART, TrippleBlockPart.MIDDLE), 3);
		level.setBlock(blockpos.above(2), blockstate.setValue(PART, TrippleBlockPart.UPPER), 3);
	}
	
	private DoorHingeSide getHinge(BlockPlaceContext ctx)
	{
		BlockGetter iblockreader = ctx.getLevel();
	    BlockPos blockpos = ctx.getClickedPos();
	    Direction direction = ctx.getHorizontalDirection();
	    BlockPos blockpos1 = blockpos.above();
	    Direction direction1 = direction.getCounterClockWise();
	    BlockPos blockpos2 = blockpos.relative(direction1);
	    BlockState blockstate = iblockreader.getBlockState(blockpos2);
	    BlockPos blockpos3 = blockpos1.relative(direction1);
	    BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
	    Direction direction2 = direction.getClockWise();
	    BlockPos blockpos4 = blockpos.relative(direction2);
	    BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
	    BlockPos blockpos5 = blockpos1.relative(direction2);
	    BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
	    int i = (blockstate.isCollisionShapeFullBlock(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(iblockreader, blockpos5) ? 1 : 0);
	    boolean flag = blockstate.is(this) && blockstate.getValue(PART) == TrippleBlockPart.LOWER;
	    boolean flag1 = blockstate2.is(this) && blockstate2.getValue(PART) == TrippleBlockPart.LOWER;
	    if ((!flag || flag1) && i <= 0)
	    {
	       if ((!flag1 || flag) && i >= 0)
	       {
	          int j = direction.getStepX();
	          int k = direction.getStepZ();
	          Vec3 vector3d = ctx.getClickLocation();
	          double d0 = vector3d.x - (double)blockpos.getX();
	          double d1 = vector3d.z - (double)blockpos.getZ();
	          return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
	       }
	       else
	       {
	          return DoorHingeSide.LEFT;
	       }
	    }
	    else
	    {
	       return DoorHingeSide.RIGHT;
	    }
	}
	
	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos blockpos, Player player, InteractionHand hand, BlockHitResult raytraceresult)
	{
		if (this.material == Material.METAL) return InteractionResult.PASS;
	    else
	    {
	       blockstate = blockstate.cycle(OPEN);
	       world.setBlock(blockpos, blockstate, 10);
	       world.levelEvent(player, blockstate.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), blockpos, 0);
	       return InteractionResult.sidedSuccess(world.isClientSide);
	    }
	}
	
	public boolean isOpen(BlockState blockstate)
	{
	    return blockstate.getValue(OPEN);
	}
	
	public void setOpen(Level world, BlockState blockstate, BlockPos blockpos, boolean value)
	{
	    if (blockstate.is(this) && blockstate.getValue(OPEN) != value)
	    {
	       world.setBlock(blockpos, blockstate.setValue(OPEN, Boolean.valueOf(value)), 10);
	       this.playSound(world, blockpos, value);
	    }
	}
	
	@Override
	public void neighborChanged(BlockState blockstate, Level level, BlockPos blockpos, Block neighborblock, BlockPos otherpos, boolean p_220069_6_)
	{
		boolean flag = level.hasNeighborSignal(blockpos) || level.hasNeighborSignal(blockpos.relative(Direction.UP)) || level.hasNeighborSignal(blockpos.relative(Direction.DOWN));
	    if (neighborblock != this && flag != blockstate.getValue(POWERED))
	    {
	       if (flag != blockstate.getValue(OPEN)) this.playSound(level, blockpos, flag);
	       level.setBlock(blockpos, blockstate.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)), 2);
	    }
	}
	
	@Override
	public boolean canSurvive(BlockState blockstate, LevelReader level, BlockPos pos)
	{
		BlockPos posBelow = pos.below();
	    BlockState stateBelow = level.getBlockState(posBelow);
	    return blockstate.getValue(PART) == TrippleBlockPart.LOWER ? stateBelow.isFaceSturdy(level, posBelow, Direction.UP) : stateBelow.is(this);
	}
	
	private void playSound(Level level, BlockPos pos, boolean open)
	{
	    level.levelEvent((Player)null, open ? this.getOpenSound() : this.getCloseSound(), pos, 0);
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState blockstate)
	{
		return PushReaction.DESTROY;
	}
	
	@Override
	public BlockState rotate(BlockState blockstate, Rotation rot)
	{
	    return blockstate.setValue(FACING, rot.rotate(blockstate.getValue(FACING)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState blockstate, Mirror mirror)
	{
		return mirror == Mirror.NONE ? blockstate : blockstate.rotate(mirror.getRotation(blockstate.getValue(FACING))).cycle(HINGE);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public long getSeed(BlockState blockstate, BlockPos pos)
	{
	    return Mth.getSeed(pos.getX(), pos.below(blockstate.getValue(PART) == TrippleBlockPart.LOWER ? 0 : 1).getY(), pos.getZ());
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(PART, FACING, OPEN, HINGE, POWERED);
	}
	
	public static boolean isWoodenDoor(Level level, BlockPos pos)
	{
	    return isWoodenDoor(level.getBlockState(pos));
	}
	
	public static boolean isWoodenDoor(BlockState blockstate)
	{
	    return blockstate.getBlock() instanceof DoorBlock && (blockstate.getMaterial() == Material.WOOD || blockstate.getMaterial() == Material.NETHER_WOOD);
	}
}
