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
import com.skullmangames.darksouls.core.util.ModUtil;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireNameScreen;
import com.skullmangames.darksouls.network.server.gui.STCOpenBonfireScreen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BonfireBlock extends Block
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final IntegerProperty ESTUS_VOLUME_LEVEL = IntegerProperty.create("estus_volume_level", 1, 4);
	public static final IntegerProperty ESTUS_HEAL_LEVEL = IntegerProperty.create("estus_heal_level", 1, 10);
	
	private static final ImmutableList<Vector3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vector3i(0, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, -1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, 1));
	private static final ImmutableList<Vector3i> RESPAWN_OFFSETS = (new Builder<Vector3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::above).iterator()).add(new Vector3i(0, 1, 0)).build();
	
	private static final Optional<VoxelShape> SHAPE = Stream.of(
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
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR));
	
	public BonfireBlock()
	{
		super(Properties.of(Material.DIRT)
				.strength(15f)
				.sound(SoundType.GRAVEL));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		if (context.getLevel().isEmptyBlock(context.getClickedPos().below())) return null;
		return this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) 
	{
	    super.createBlockStateDefinition(container);
		container.add(LIT, ESTUS_VOLUME_LEVEL, ESTUS_HEAL_LEVEL);
	}
	
	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
	{
		BonfireBlockEntity blockentity = ModUtil.getBlockEntity(level, pos, ModBlockEntities.BONFIRE.get()).orElse(null);
		if (blockentity == null) return ActionResultType.PASS;
		if (!state.getValue(LIT))
		{
			if (player.getItemInHand(hand).getItem() == ModItems.DARKSIGN.get())
			{
				if (blockentity.hasFireKeeper())
				{
					if (player.level.isClientSide) player.sendMessage(new TranslationTextComponent("gui.darksouls.fire_keeper_absent"), Util.NIL_UUID);
				}
				else if (!blockentity.hasName() && !player.level.isClientSide)
				{
					ModNetworkManager.sendToPlayer(new STCOpenBonfireNameScreen(pos), (ServerPlayerEntity) player);
				}
				return ActionResultType.sidedSuccess(level.isClientSide);
			}
			return ActionResultType.PASS;
		}
		else
		{
			Item item = player.getItemInHand(hand).getItem();
			if (item == ModItems.ESTUS_FLASK.get() || item == ModItems.ASHEN_ESTUS_FLASK.get() || item == ModItems.UNDEAD_BONE_SHARD.get())
			{
				return ActionResultType.PASS;
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
					ModNetworkManager.sendToPlayer(new STCOpenBonfireNameScreen(pos), (ServerPlayerEntity) player);
				}
				else
				{
					ModNetworkManager.sendToPlayer(new STCOpenBonfireScreen(pos), (ServerPlayerEntity) player);
				}
			}
			
			Optional<Vector3d> optional = findStandUpPosition(player.getType(), level, pos);
			if (optional.isPresent())
			{
				if (player instanceof ServerPlayerEntity)
				{
					((ServerPlayerEntity)player).setRespawnPosition(level.dimension(), new BlockPos(optional.get()), 0.0F, true, true);
				}
			}
			else
			{
				player.sendMessage(new TranslationTextComponent("gui.darksouls.respawn_position_fail_message"), Util.NIL_UUID);
			}
			
			return ActionResultType.SUCCESS;
		}
	}
	
	public static Optional<Vector3d> findStandUpPosition(EntityType<?> entityType, ICollisionReader collision, BlockPos blockPos)
	{
		Optional<Vector3d> optional = findStandUpPosition(entityType, collision, blockPos, true);
	    return optional.isPresent() ? optional : findStandUpPosition(entityType, collision, blockPos, false);
	}

	private static Optional<Vector3d> findStandUpPosition(EntityType<?> entityType, ICollisionReader collision, BlockPos blockPos, boolean p_242678_3_)
	{
	    BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

	    for(Vector3i vector3i : RESPAWN_OFFSETS)
	    {
	         blockpos$mutable.set(blockPos).move(vector3i);
	         Vector3d vector3d = TransportationHelper.findSafeDismountLocation(entityType, collision, blockpos$mutable, p_242678_3_);
	         if (vector3d != null)
	         {
	            return Optional.of(vector3d);
	         }
	    }

	    return Optional.empty();
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader level, BlockPos vertex)
	{
		return state.getValue(LIT) ? 10 : 0;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos vertex, ISelectionContext context)
	{
		return SHAPE.orElse(VoxelShapes.block());
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld level, BlockPos pos, Random random)
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
	
	public static void kindleEffect(World level, BlockPos pos)
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
	public void animateTick(BlockState state, World level, BlockPos pos, Random random)
	{
		if (state.getValue(LIT))
		{
			ModNetworkManager.connection.tryPlayBonfireAmbientSound(pos);
			
			for (int i = 0; i < state.getValue(ESTUS_VOLUME_LEVEL); i++)
			{
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)i * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.4D, (double)pos.getY() + 0.3D + ((double)i * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.6D, (double)pos.getY() + 0.3D + ((double)i * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)i * 0.1D), (double)pos.getZ() + 0.4D, 0.0D, 0.0D, 0.0D);
		        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)i * 0.1D), (double)pos.getZ() + 0.6D, 0.0D, 0.0D, 0.0D);
		        
		        if (i >= 2)
		        {
		        	level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.35D, (double)pos.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.65D, (double)pos.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)pos.getZ() + 0.35D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 2) * 0.1D), (double)pos.getZ() + 0.65D, 0.0D, 0.0D, 0.0D);
		        }
		        if (i >= 3)
		        {
		        	level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.3D, (double)pos.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.7D, (double)pos.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)pos.getZ() + 0.3D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 3) * 0.1D), (double)pos.getZ() + 0.7D, 0.0D, 0.0D, 0.0D);
		        }
		        if (i == 4)
		        {
		        	level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.25D, (double)pos.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.75D, (double)pos.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)pos.getZ() + 0.25D, 0.0D, 0.0D, 0.0D);
			        level.addAlwaysVisibleParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D + ((double)(i - 4) * 0.1D), (double)pos.getZ() + 0.75D, 0.0D, 0.0D, 0.0D);
		        }
			}
		}
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return ModBlockEntities.BONFIRE.get().create();
	}
}
