package com.skullmangames.darksouls.common.block;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.skullmangames.darksouls.common.tileentity.BonfireBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireNameScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireScreen;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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

public class BonfireBlock extends BaseHorizontalBlock implements EntityBlock
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final IntegerProperty ESTUS_VOLUME_LEVEL = IntegerProperty.create("estus_volume_level", 1, 4);
	public static final IntegerProperty ESTUS_HEAL_LEVEL = IntegerProperty.create("estus_heal_level", 1, 10);
	
	private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
	private static final ImmutableList<Vec3i> RESPAWN_OFFSETS = (new Builder<Vec3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();
	
	protected static final VoxelShape SHAPE = Stream.of(
			Block.box(2, 0, 2, 14, 1, 14),
			Block.box(4, 0.5, 4, 12, 2.5, 12),
			Block.box(6, 2.4, 6, 10, 3.4, 10),
			Block.box(7.5, 3, 7.5, 9.5, 15, 8.5),
			Block.box(6.5, 14, 7.5, 7.5, 15, 8.5),
			Block.box(9.5, 14, 7.5, 10.5, 15, 8.5),
			Block.box(3, 1, 9, 7, 4, 13),
			Block.box(8, 15, 7.5, 9, 19, 8.5),
			Block.box(7, 1, 3, 8, 3, 6),
			Block.box(8.5, 2, 4.5, 9.5, 4, 7.5),
			Block.box(9.5, 0.10000000000000009, 11.5, 10.5, 1.3000000000000003, 14.5),
			Block.box(12, 1, 7, 14, 1.5, 8),
			Block.box(3, 1, 8, 6, 3, 9)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	
	public BonfireBlock()
	{
		super(BlockBehaviour.Properties.of(Material.DIRT)
				.strength(15f)
				.sound(SoundType.GRAVEL));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1)).setValue(HORIZONTAL_FACING, Direction.NORTH));
		this.runCalculation(SHAPE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		if (context.getLevel().isEmptyBlock(context.getClickedPos().below())) return null;
		return this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1)).setValue(HORIZONTAL_FACING, Direction.NORTH);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> container) 
	{
	    super.createBlockStateDefinition(container);
		container.add(LIT, ESTUS_VOLUME_LEVEL, ESTUS_HEAL_LEVEL, HORIZONTAL_FACING);
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
					player.sendMessage(new TranslatableComponent("gui.darksouls.fire_keeper_absent"), Util.NIL_UUID);
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
			if (item == ModItems.ESTUS_FLASK.get() || item == ModItems.UNDEAD_BONE_SHARD.get())
			{
				return InteractionResult.PASS;
			}
			
			player.heal(player.getMaxHealth() - player.getHealth());
			
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
			
			Optional<Vec3> optional = findStandUpPosition(EntityType.PLAYER, level, pos);
			if (optional.isPresent())
			{
				if (player instanceof ServerPlayer) ((ServerPlayer)player).setRespawnPosition(level.dimension(), new BlockPos(optional.get()), 0.0F, true, true);
			}
			else
			{
				player.sendMessage(new TranslatableComponent("gui.darksouls.respawn_position_fail_message"), Util.NIL_UUID);
			}
			
			return InteractionResult.SUCCESS;
		}
	}
	
	public static Optional<Vec3> findStandUpPosition(EntityType<?> p_235560_0_, CollisionGetter p_235560_1_, BlockPos p_235560_2_)
	{
		Optional<Vec3> optional = findStandUpPosition(p_235560_0_, p_235560_1_, p_235560_2_, true);
	    return optional.isPresent() ? optional : findStandUpPosition(p_235560_0_, p_235560_1_, p_235560_2_, false);
	}

	private static Optional<Vec3> findStandUpPosition(EntityType<?> p_242678_0_, CollisionGetter p_242678_1_, BlockPos p_242678_2_, boolean p_242678_3_)
	{
	    BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

	    for(Vec3i vector3i : RESPAWN_OFFSETS)
	    {
	         blockpos$mutable.set(p_242678_2_).move(vector3i);
	         Vec3 vector3d = DismountHelper.findSafeDismountLocation(p_242678_0_, p_242678_1_, blockpos$mutable, p_242678_3_);
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
		//return SHAPES.get(state.getValue(HORIZONTAL_FACING));
		return super.getShape(state, worldIn, vertex, context);
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
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level worldIn, BlockPos vertex, Random random)
	{
		if (state.getValue(LIT))
		{
			if (random.nextInt(8) == 0)
			{
				worldIn.playLocalSound((double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.5D, (double)vertex.getZ() + 0.5D, ModSoundEvents.BONFIRE_AMBIENT.get(), SoundSource.BLOCKS, 0.3F, random.nextFloat() * 0.7F + 0.3F, false);
			}
			
			for (int i = 0; i < state.getValue(ESTUS_VOLUME_LEVEL); i++)
			{
				worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)i * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.4D, (double)vertex.getY() + 0.3D + ((double)i * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.6D, (double)vertex.getY() + 0.3D + ((double)i * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)i * 0.1D), (double)vertex.getZ() + 0.4D, 0.0D, 0.0D, 0.0D);
		        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)i * 0.1D), (double)vertex.getZ() + 0.6D, 0.0D, 0.0D, 0.0D);
		        
		        if (i >= 2)
		        {
		        	worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.35D, (double)vertex.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.65D, (double)vertex.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)vertex.getZ() + 0.35D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)vertex.getZ() + 0.65D, 0.0D, 0.0D, 0.0D);
		        }
		        if (i >= 3)
		        {
		        	worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.3D, (double)vertex.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.7D, (double)vertex.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)vertex.getZ() + 0.3D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)vertex.getZ() + 0.7D, 0.0D, 0.0D, 0.0D);
		        }
		        if (i == 4)
		        {
		        	worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.25D, (double)vertex.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.75D, (double)vertex.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)vertex.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)vertex.getZ() + 0.25D, 0.0D, 0.0D, 0.0D);
			        worldIn.addParticle(ParticleTypes.FLAME, (double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)vertex.getZ() + 0.75D, 0.0D, 0.0D, 0.0D);
		        }
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new BonfireBlockEntity(pos, state);
	}
}
