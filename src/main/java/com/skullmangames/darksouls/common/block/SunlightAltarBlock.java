package com.skullmangames.darksouls.common.block;

import java.util.Optional;
import java.util.stream.Stream;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.Covenant.Reward;
import com.skullmangames.darksouls.common.entity.Covenants;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.gui.STCOpenCovenantScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenJoinCovenantScreen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SunlightAltarBlock extends HorizontalDirectionalBlock
{
	private static final Optional<VoxelShape> SHAPE_NORTH = Stream.of(
			Block.box(0, 0, 2, 16, 4, 14),
			Block.box(3, 4, 4, 7, 8, 8),
			Block.box(9, 4, 4, 13, 10, 8),
			Block.box(9, 4, 8, 13, 6, 11),
			Block.box(3, 4, 8, 7, 6, 11)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	
	private static final Optional<VoxelShape> SHAPE_SOUTH = Stream.of(
			Block.box(0, 0, 2, 16, 4, 14),
			Block.box(9, 4, 8, 13, 8, 12),
			Block.box(3, 4, 8, 7, 10, 12),
			Block.box(3, 4, 5, 7, 6, 8),
			Block.box(9, 4, 5, 13, 6, 8)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	
	private static final Optional<VoxelShape> SHAPE_WEST = Stream.of(
			Block.box(2, 0, 0, 14, 4, 16),
			Block.box(4, 4, 9, 8, 8, 13),
			Block.box(4, 4, 3, 8, 10, 7),
			Block.box(8, 4, 3, 11, 6, 7),
			Block.box(8, 4, 9, 11, 6, 13)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	
	private static final Optional<VoxelShape> SHAPE_EAST = Stream.of(
			Block.box(2, 0, 0, 14, 4, 16),
			Block.box(8, 4, 3, 12, 8, 7),
			Block.box(8, 4, 9, 12, 10, 13),
			Block.box(5, 4, 9, 8, 6, 13),
			Block.box(5, 4, 3, 8, 6, 7)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	
	public SunlightAltarBlock()
	{
		super(Properties.of(Material.STONE)
				.strength(2.0F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos vertex, CollisionContext context)
	{
		switch(state.getValue(FACING))
		{
		default:
		case NORTH: return SHAPE_NORTH.orElse(Shapes.block());
		case SOUTH: return SHAPE_SOUTH.orElse(Shapes.block());
		case WEST: return SHAPE_WEST.orElse(Shapes.block());
		case EAST: return SHAPE_EAST.orElse(Shapes.block());
		}
	}
	
	@Override
	public InteractionResult use(BlockState blockstate, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hitresult)
	{
		if (player instanceof ServerPlayer)
		{
			PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				if (!playerCap.getCovenant().is(Covenants.WARRIORS_OF_SUNLIGHT)) ModNetworkManager.sendToPlayer(new STCOpenJoinCovenantScreen(Covenants.WARRIORS_OF_SUNLIGHT), (ServerPlayer)player);
				else if (player.getItemInHand(hand).is(ModItems.SUNLIGHT_MEDAL.get()))
				{
					int reqCost = playerCap.getCovenant().getProgressTillNextReward(playerCap);
					if (reqCost > 0)
					{
						ItemStack medals = player.getItemInHand(hand);
						if (medals.getCount() < reqCost)
						{
							playerCap.raiseCovenantProgress(medals.getCount());
							medals.shrink(medals.getCount());
						}
						else
						{
							Reward reward = playerCap.getCovenant().getNextReward(playerCap);
							playerCap.raiseCovenantProgress(reqCost);
							medals.shrink(reqCost);
							
							ItemEntity itementity = new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), reward.getRewardItem());
							itementity.setDefaultPickUpDelay();
							level.addFreshEntity(itementity);
						}
					}
				}
				else ModNetworkManager.sendToPlayer(new STCOpenCovenantScreen(Covenants.WARRIORS_OF_SUNLIGHT), (ServerPlayer)player);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
