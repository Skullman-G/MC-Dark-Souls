package com.skullmangames.darksouls.common.blocks;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.state.properties.TrippleBlockPart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BigDoorBlock extends Block
{
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
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
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_)
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
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState blockstate, Direction direction, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_)
	{
		TrippleBlockPart trippleblockpart = blockstate.getValue(PART);
	    if (direction.getAxis() == Direction.Axis.Y && trippleblockpart == TrippleBlockPart.LOWER == (direction == Direction.UP))
	    {
	       return p_196271_3_.is(this) && p_196271_3_.getValue(PART) != trippleblockpart ? blockstate.setValue(FACING, p_196271_3_.getValue(FACING)).setValue(OPEN, p_196271_3_.getValue(OPEN)).setValue(HINGE, p_196271_3_.getValue(HINGE)).setValue(POWERED, p_196271_3_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
	    }
	    else
	    {
	       return trippleblockpart == TrippleBlockPart.LOWER && direction == Direction.DOWN && !blockstate.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockstate, direction, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
	    }
	}
	
	@Override
	public void playerWillDestroy(World world, BlockPos blockpos, BlockState blockstate, PlayerEntity player)
	{
		if (!world.isClientSide && player.isCreative())
		{
			switch (blockstate.getValue(PART))
			{
				case UPPER:
					BlockPos middleblockpos = blockpos.below();
				    BlockState middleblockstate = world.getBlockState(middleblockpos);
				    
				    if (middleblockstate.getBlock() == blockstate.getBlock() && middleblockstate.getValue(PART) == TrippleBlockPart.MIDDLE)
				    {
				       world.setBlock(middleblockpos, Blocks.AIR.defaultBlockState(), 35);
				       world.levelEvent(player, 2001, middleblockpos, Block.getId(middleblockstate));
				    }
				    
				    BlockPos lowerblockpos = blockpos.below(2);
				    BlockState lowerblockstate = world.getBlockState(lowerblockpos);
				    
				    if (lowerblockstate.getBlock() == blockstate.getBlock() && lowerblockstate.getValue(PART) == TrippleBlockPart.MIDDLE)
				    {
				       world.setBlock(lowerblockpos, Blocks.AIR.defaultBlockState(), 35);
				       world.levelEvent(player, 2001, lowerblockpos, Block.getId(lowerblockstate));
				    }
					break;
					
				case MIDDLE:
				    BlockPos lowerblockpos2 = blockpos.below();
				    BlockState lowerblockstate2 = world.getBlockState(lowerblockpos2);
				    
				    if (lowerblockstate2.getBlock() == blockstate.getBlock() && lowerblockstate2.getValue(PART) == TrippleBlockPart.MIDDLE)
				    {
				       world.setBlock(lowerblockpos2, Blocks.AIR.defaultBlockState(), 35);
				       world.levelEvent(player, 2001, lowerblockpos2, Block.getId(lowerblockstate2));
				    }
					break;
					
				default:
					break;
			}
	    }

	    super.playerWillDestroy(world, blockpos, blockstate, player);
	}
	
	@Override
	public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_)
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
	public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_)
	{
		BlockPos blockpos = p_196258_1_.getClickedPos();
	    if (blockpos.getY() < 255 && p_196258_1_.getLevel().getBlockState(blockpos.above()).canBeReplaced(p_196258_1_))
	    {
	       World world = p_196258_1_.getLevel();
	       boolean flag = world.hasNeighborSignal(blockpos) || world.hasNeighborSignal(blockpos.above());
	       return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection()).setValue(HINGE, this.getHinge(p_196258_1_)).setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)).setValue(PART, TrippleBlockPart.LOWER);
	    }
	    else
	    {
	       return null;
	    }
	}
	
	@Override
	public void setPlacedBy(World world, BlockPos blockpos, BlockState blockstate, LivingEntity p_180633_4_, ItemStack p_180633_5_)
	{
		world.setBlock(blockpos.above(), blockstate.setValue(PART, TrippleBlockPart.MIDDLE), 3);
		world.setBlock(blockpos.above(2), blockstate.setValue(PART, TrippleBlockPart.UPPER), 3);
	}
	
	private DoorHingeSide getHinge(BlockItemUseContext p_208073_1_)
	{
		IBlockReader iblockreader = p_208073_1_.getLevel();
	    BlockPos blockpos = p_208073_1_.getClickedPos();
	    Direction direction = p_208073_1_.getHorizontalDirection();
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
	          Vector3d vector3d = p_208073_1_.getClickLocation();
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
	public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_)
	{
	    if (this.material == Material.METAL)
	    {
	       return ActionResultType.PASS;
	    }
	    else
	    {
	       p_225533_1_ = p_225533_1_.cycle(OPEN);
	       p_225533_2_.setBlock(p_225533_3_, p_225533_1_, 10);
	       p_225533_2_.levelEvent(p_225533_4_, p_225533_1_.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), p_225533_3_, 0);
	       return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
	    }
	}
	
	public boolean isOpen(BlockState p_242664_1_)
	{
	    return p_242664_1_.getValue(OPEN);
	}
	
	public void setOpen(World p_242663_1_, BlockState p_242663_2_, BlockPos p_242663_3_, boolean p_242663_4_)
	{
	    if (p_242663_2_.is(this) && p_242663_2_.getValue(OPEN) != p_242663_4_)
	    {
	       p_242663_1_.setBlock(p_242663_3_, p_242663_2_.setValue(OPEN, Boolean.valueOf(p_242663_4_)), 10);
	       this.playSound(p_242663_1_, p_242663_3_, p_242663_4_);
	    }
	}
	
	@Override
	public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_)
	{
	    boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_) || p_220069_2_.hasNeighborSignal(p_220069_3_.relative(p_220069_1_.getValue(PART) == TrippleBlockPart.LOWER ? Direction.UP : Direction.DOWN));
	    if (p_220069_4_ != this && flag != p_220069_1_.getValue(POWERED))
	    {
	       if (flag != p_220069_1_.getValue(OPEN))
	       {
	          this.playSound(p_220069_2_, p_220069_3_, flag);
	       }

	       p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)), 2);
	    }
	}
	
	@Override
	public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_)
	{
	    BlockPos blockpos = p_196260_3_.below();
	    BlockState blockstate = p_196260_2_.getBlockState(blockpos);
	    return p_196260_1_.getValue(PART) == TrippleBlockPart.LOWER ? blockstate.isFaceSturdy(p_196260_2_, blockpos, Direction.UP) : blockstate.is(this);
	}
	
	private void playSound(World p_196426_1_, BlockPos p_196426_2_, boolean p_196426_3_)
	{
	    p_196426_1_.levelEvent((PlayerEntity)null, p_196426_3_ ? this.getOpenSound() : this.getCloseSound(), p_196426_2_, 0);
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState p_149656_1_)
	{
		return PushReaction.DESTROY;
	}
	
	@Override
	public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_)
	{
	    return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_)
	{
		return p_185471_2_ == Mirror.NONE ? p_185471_1_ : p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING))).cycle(HINGE);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public long getSeed(BlockState p_209900_1_, BlockPos p_209900_2_)
	{
	    return MathHelper.getSeed(p_209900_2_.getX(), p_209900_2_.below(p_209900_1_.getValue(PART) == TrippleBlockPart.LOWER ? 0 : 1).getY(), p_209900_2_.getZ());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> p_206840_1_)
	{
		p_206840_1_.add(PART, FACING, OPEN, HINGE, POWERED);
	}
	
	public static boolean isWoodenDoor(World p_235491_0_, BlockPos p_235491_1_)
	{
	    return isWoodenDoor(p_235491_0_.getBlockState(p_235491_1_));
	}
	
	public static boolean isWoodenDoor(BlockState p_235492_0_)
	{
	    return p_235492_0_.getBlock() instanceof DoorBlock && (p_235492_0_.getMaterial() == Material.WOOD || p_235492_0_.getMaterial() == Material.NETHER_WOOD);
	}
}
