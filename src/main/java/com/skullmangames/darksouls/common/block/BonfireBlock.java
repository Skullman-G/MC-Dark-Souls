package com.skullmangames.darksouls.common.block;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireNameScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireScreen;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BonfireBlock extends Block implements EntityBlock
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final IntegerProperty ESTUS_VOLUME_LEVEL = IntegerProperty.create("estus_volume_level", 1, 4);
	public static final IntegerProperty ESTUS_HEAL_LEVEL = IntegerProperty.create("estus_heal_level", 1, 10);
	
	private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
	private static final ImmutableList<Vec3i> RESPAWN_OFFSETS = (new Builder<Vec3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();
	
	private static final Optional<VoxelShape> SHAPE = Stream.of(
			Block.box(1, 0, 1, 15, 5, 15)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR));
	
	
	
	public BonfireBlock()
	{
		super(BlockBehaviour.Properties.of(Material.DIRT)
				.strength(15f)
				.sound(SoundType.GRAVEL));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		if (context.getLevel().isEmptyBlock(context.getClickedPos().below())) return null;
		return this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> container) 
	{
	    super.createBlockStateDefinition(container);
		container.add(LIT, ESTUS_VOLUME_LEVEL, ESTUS_HEAL_LEVEL);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		BonfireBlockEntity blockentity = level.getBlockEntity(pos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (blockentity == null) return InteractionResult.PASS;
		if (!state.getValue(LIT))
		{
			if (player.getItemInHand(hand).getItem() == ModItems.DARKSIGN.get())
			{
				if (blockentity.hasFireKeeper())
				{
					if (player.level.isClientSide) player.sendMessage(new TranslatableComponent("gui.darksouls.fire_keeper_absent"), Util.NIL_UUID);
				}
				else if (!blockentity.hasName() && !player.level.isClientSide)
				{
					ModNetworkManager.sendToPlayer(new STCOpenBonfireNameScreen(pos), (ServerPlayer) player);
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			return InteractionResult.PASS;
		}
		else
		{
			Item item = player.getItemInHand(hand).getItem();
			if (item == ModItems.ESTUS_FLASK.get() || item == ModItems.ASHEN_ESTUS_FLASK.get() || item == ModItems.UNDEAD_BONE_SHARD.get())
			{
				return InteractionResult.PASS;
			}
			
			player.heal(player.getMaxHealth() - player.getHealth());
			
			PlayerCap<?> playerCap = (PlayerCap<?>)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.setFP(playerCap.getMaxFP());
				playerCap.addTeleport(blockentity);
			}
			
			if (!player.level.isClientSide)
			{
				if (!blockentity.hasName())
				{
					ModNetworkManager.sendToPlayer(new STCOpenBonfireNameScreen(pos), (ServerPlayer) player);
				}
				else
				{
					ModNetworkManager.sendToPlayer(new STCOpenBonfireScreen(pos), (ServerPlayer) player);
				}
			}
			
			Optional<Vec3> optional = findStandUpPosition(player.getType(), level, pos);
			if (optional.isPresent())
			{
				if (player instanceof ServerPlayer)
				{
					((ServerPlayer)player).setRespawnPosition(level.dimension(), new BlockPos(optional.get()), 0.0F, true, true);
				}
			}
			else
			{
				player.sendMessage(new TranslatableComponent("gui.darksouls.respawn_position_fail_message"), Util.NIL_UUID);
			}
			
			return InteractionResult.SUCCESS;
		}
	}
	
	public static Optional<Vec3> findStandUpPosition(EntityType<?> entityType, CollisionGetter collision, BlockPos blockPos)
	{
		Optional<Vec3> optional = findStandUpPosition(entityType, collision, blockPos, true);
	    return optional.isPresent() ? optional : findStandUpPosition(entityType, collision, blockPos, false);
	}

	private static Optional<Vec3> findStandUpPosition(EntityType<?> entityType, CollisionGetter collision, BlockPos blockPos, boolean p_242678_3_)
	{
	    BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

	    for(Vec3i vector3i : RESPAWN_OFFSETS)
	    {
	         blockpos$mutable.set(blockPos).move(vector3i);
	         Vec3 vector3d = DismountHelper.findSafeDismountLocation(entityType, collision, blockpos$mutable, p_242678_3_);
	         if (vector3d != null)
	         {
	            return Optional.of(vector3d);
	         }
	    }

	    return Optional.empty();
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos vertex)
	{
		return state.getValue(LIT) ? 10 : 0;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos vertex, CollisionContext context)
	{
		return SHAPE.orElse(Shapes.block());
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
	{
		if (random.nextInt(10) == 1 && state.getValue(BonfireBlock.LIT))
		{
			int i = (random.nextInt(1)) * (random.nextBoolean() ? -1 : 1);
			int j = (random.nextInt(1)) * (random.nextBoolean() ? -1 : 1);
			BlockPos blockpos = pos.offset(i, pos.getZ(), j);
			ItemEntity homewardbone = new ItemEntity(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
					new ItemStack(ModItems.HOMEWARD_BONE.get()));
			level.addFreshEntity(homewardbone);
		}
	}
	
	public static void kindleEffect(Level level, BlockPos pos)
	{
		int r = 4;
		for (int i = 0; i < 180; i++)
		{
			if (i % 20 == 0)
			{
				double xd = Math.cos(Math.toRadians(i)) / r;
				double yd = Math.sin(Math.toRadians(i)) / r;
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, xd, yd, 0);
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0, yd, xd);
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.75D * xd, yd, 0.75D * xd);
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, -0.75D * xd, yd, 0.75D * xd);
			}
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
	{
		if (state.getValue(LIT))
		{
			ModNetworkManager.connection.tryPlayBonfireAmbientSound(pos);
			double yOffset = 0.35D;
			double xOffset = 0.5D;
			double zOffset = 0.5D;
			
			double r = 0.1D;
			for (int s = state.getValue(ESTUS_VOLUME_LEVEL) - 1; s >= 0; s--)
			{
				for (int i = 0; i < 359; i++)
				{
					if (i % 40 == 0)
					{
						double xd = Math.cos(Math.toRadians(i)) * r;
						double yd = Math.sin(Math.toRadians(i)) * r;
						level.addAlwaysVisibleParticle(ParticleTypes.FLAME, pos.getX() + xOffset + xd, pos.getY() + yOffset + s * 0.1D, pos.getZ() + zOffset + yd,
								0, 0, 0);
					}
				}
				r += 0.1D;
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new BonfireBlockEntity(pos, state);
	}
}
