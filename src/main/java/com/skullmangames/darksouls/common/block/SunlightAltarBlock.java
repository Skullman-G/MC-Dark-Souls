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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SunlightAltarBlock extends HorizontalBlock
{
	private static final Optional<VoxelShape> SHAPE_NORTH = Stream.of(
			Block.box(0, 0, 2, 16, 4, 14),
			Block.box(3, 4, 4, 7, 8, 8),
			Block.box(9, 4, 4, 13, 10, 8),
			Block.box(9, 4, 8, 13, 6, 11),
			Block.box(3, 4, 8, 7, 6, 11)
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR));
	
	private static final Optional<VoxelShape> SHAPE_SOUTH = Stream.of(
			Block.box(0, 0, 2, 16, 4, 14),
			Block.box(9, 4, 8, 13, 8, 12),
			Block.box(3, 4, 8, 7, 10, 12),
			Block.box(3, 4, 5, 7, 6, 8),
			Block.box(9, 4, 5, 13, 6, 8)
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR));
	
	private static final Optional<VoxelShape> SHAPE_WEST = Stream.of(
			Block.box(2, 0, 0, 14, 4, 16),
			Block.box(4, 4, 9, 8, 8, 13),
			Block.box(4, 4, 3, 8, 10, 7),
			Block.box(8, 4, 3, 11, 6, 7),
			Block.box(8, 4, 9, 11, 6, 13)
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR));
	
	private static final Optional<VoxelShape> SHAPE_EAST = Stream.of(
			Block.box(2, 0, 0, 14, 4, 16),
			Block.box(8, 4, 3, 12, 8, 7),
			Block.box(8, 4, 9, 12, 10, 13),
			Block.box(5, 4, 9, 8, 6, 13),
			Block.box(5, 4, 3, 8, 6, 7)
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR));
	
	public SunlightAltarBlock()
	{
		super(Properties.of(Material.STONE)
				.strength(2.0F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops());
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		if (context.getLevel().isEmptyBlock(context.getClickedPos().below())) return null;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container)
	{
	    super.createBlockStateDefinition(container);
		container.add(FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos vertex, ISelectionContext context)
	{
		switch(state.getValue(FACING))
		{
		default:
		case NORTH: return SHAPE_NORTH.orElse(VoxelShapes.block());
		case SOUTH: return SHAPE_SOUTH.orElse(VoxelShapes.block());
		case WEST: return SHAPE_WEST.orElse(VoxelShapes.block());
		case EAST: return SHAPE_EAST.orElse(VoxelShapes.block());
		}
	}
	
	@Override
	public ActionResultType use(BlockState blockstate, World level, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult hitresult)
	{
		if (player instanceof ServerPlayerEntity)
		{
			PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				if (!playerCap.getCovenant().is(Covenants.WARRIORS_OF_SUNLIGHT)) ModNetworkManager.sendToPlayer(new STCOpenJoinCovenantScreen(Covenants.WARRIORS_OF_SUNLIGHT), (ServerPlayerEntity)player);
				else if (player.getItemInHand(hand).getItem() == ModItems.SUNLIGHT_MEDAL.get())
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
				else ModNetworkManager.sendToPlayer(new STCOpenCovenantScreen(Covenants.WARRIORS_OF_SUNLIGHT), (ServerPlayerEntity)player);
			}
		}
		return ActionResultType.SUCCESS;
	}
}
