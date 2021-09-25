package com.skullmangames.darksouls.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LockableDoorBlock extends LockableBlock
{
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

   public LockableDoorBlock(AbstractBlock.Properties p_i48413_1_)
   {
      super(p_i48413_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, Boolean.valueOf(false)).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_)
   {
      Direction direction = p_220053_1_.getValue(FACING);
      boolean flag = !p_220053_1_.getValue(OPEN);
      boolean flag1 = p_220053_1_.getValue(HINGE) == DoorHingeSide.RIGHT;
      switch(direction) {
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
public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_)
   {
      DoubleBlockHalf doubleblockhalf = p_196271_1_.getValue(HALF);
      if (p_196271_2_.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (p_196271_2_ == Direction.UP))
      {
         return p_196271_3_.is(this) && p_196271_3_.getValue(HALF) != doubleblockhalf ? p_196271_1_.setValue(FACING, p_196271_3_.getValue(FACING)).setValue(OPEN, p_196271_3_.getValue(OPEN)).setValue(HINGE, p_196271_3_.getValue(HINGE)).setValue(POWERED, p_196271_3_.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
      }
      else
      {
         return doubleblockhalf == DoubleBlockHalf.LOWER && p_196271_2_ == Direction.DOWN && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public void playerWillDestroy(World level, BlockPos blockpos, BlockState blockstate, PlayerEntity player)
   {
      if (!level.isClientSide && player.isCreative())
      {
    	  DoubleBlockHalf doubleblockhalf = blockstate.getValue(HALF);
          if (doubleblockhalf == DoubleBlockHalf.UPPER)
          {
             BlockPos blockpos2 = blockpos.below();
             BlockState blockstate2 = level.getBlockState(blockpos2);
             if (blockstate2.getBlock() == blockstate.getBlock() && blockstate2.getValue(HALF) == DoubleBlockHalf.LOWER)
             {
            	 level.setBlock(blockpos2, Blocks.AIR.defaultBlockState(), 35);
            	 level.levelEvent(player, 2001, blockpos2, Block.getId(blockstate2));
             }
          }
      }

      super.playerWillDestroy(level, blockpos, blockstate, player);
   }

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

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_)
   {
      BlockPos blockpos = p_196258_1_.getClickedPos();
      if (blockpos.getY() < 255 && p_196258_1_.getLevel().getBlockState(blockpos.above()).canBeReplaced(p_196258_1_))
      {
         World world = p_196258_1_.getLevel();
         boolean flag = world.hasNeighborSignal(blockpos) || world.hasNeighborSignal(blockpos.above());
         return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection()).setValue(HINGE, this.getHinge(p_196258_1_)).setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)).setValue(HALF, DoubleBlockHalf.LOWER);
      }
      else return null;
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_)
   {
      p_180633_1_.setBlock(p_180633_2_.above(), p_180633_3_.setValue(HALF, DoubleBlockHalf.UPPER), 3);
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
      boolean flag = blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
      boolean flag1 = blockstate2.is(this) && blockstate2.getValue(HALF) == DoubleBlockHalf.LOWER;
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
         else return DoorHingeSide.LEFT;
      }
      else return DoorHingeSide.RIGHT;
   }
   
   @Override
	public BlockPos[] getPartPositions(World world, BlockPos blockpos)
   {
	   BlockPos[] positions = new BlockPos[2];
	   positions[0] = blockpos;
		switch (world.getBlockState(blockpos).getValue(HALF))
		{
			case LOWER:
				positions[1] = blockpos.above();
				break;
				
			case UPPER:
				positions[1] = blockpos.below();
				break;
				
			default:
				throw new IndexOutOfBoundsException("Invalid door half.");
		}
		
		return positions;
	}

   public ActionResultType use(BlockState blockstate, World level, BlockPos blockpos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_)
   {
	   if (this.isLocked(level, blockpos))
	   {
		   if (player != null && player instanceof ServerPlayerEntity) ((ServerPlayerEntity)player).connection.send(new STitlePacket(STitlePacket.Type.ACTIONBAR, new StringTextComponent("Door locked by "+this.getKeyName(level, blockpos))));
		   return ActionResultType.SUCCESS;
	   }
	   else
	   {
		   blockstate = blockstate.cycle(OPEN);
		   level.setBlock(blockpos, blockstate, 10);
		   level.levelEvent(player, blockstate.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), blockpos, 0);
		   return ActionResultType.sidedSuccess(level.isClientSide);
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

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_)
   {
      boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_) || p_220069_2_.hasNeighborSignal(p_220069_3_.relative(p_220069_1_.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (p_220069_4_ != this && flag != p_220069_1_.getValue(POWERED))
      {
         if (flag != p_220069_1_.getValue(OPEN)) this.playSound(p_220069_2_, p_220069_3_, flag);
         p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)), 2);
      }

   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_)
   {
      BlockPos blockpos = p_196260_3_.below();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      return p_196260_1_.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(p_196260_2_, blockpos, Direction.UP) : blockstate.is(this);
   }

   private void playSound(World p_196426_1_, BlockPos p_196426_2_, boolean p_196426_3_)
   {
      p_196426_1_.levelEvent((PlayerEntity)null, p_196426_3_ ? this.getOpenSound() : this.getCloseSound(), p_196426_2_, 0);
   }

   public PushReaction getPistonPushReaction(BlockState p_149656_1_)
   {
      return PushReaction.DESTROY;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_)
   {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   @SuppressWarnings("deprecation")
public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_)
   {
      return p_185471_2_ == Mirror.NONE ? p_185471_1_ : p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING))).cycle(HINGE);
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed(BlockState p_209900_1_, BlockPos p_209900_2_)
   {
      return MathHelper.getSeed(p_209900_2_.getX(), p_209900_2_.below(p_209900_1_.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_209900_2_.getZ());
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_)
   {
      p_206840_1_.add(HALF, FACING, OPEN, HINGE, POWERED);
   }

   public static boolean isWoodenDoor(World p_235491_0_, BlockPos p_235491_1_)
   {
      return isWoodenDoor(p_235491_0_.getBlockState(p_235491_1_));
   }

   public static boolean isWoodenDoor(BlockState p_235492_0_)
   {
      return p_235492_0_.getBlock() instanceof LockableDoorBlock && (p_235492_0_.getMaterial() == Material.WOOD || p_235492_0_.getMaterial() == Material.NETHER_WOOD);
   }
}