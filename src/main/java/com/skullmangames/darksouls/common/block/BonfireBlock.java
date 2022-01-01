package com.skullmangames.darksouls.common.block;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.ModTileEntities;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BonfireBlock extends BaseHorizontalBlock
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final IntegerProperty ESTUS_VOLUME_LEVEL = IntegerProperty.create("estus_volume_level", 1, 4);
	public static final IntegerProperty ESTUS_HEAL_LEVEL = IntegerProperty.create("estus_heal_level", 1, 10);
	
	private static final ImmutableList<Vector3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vector3i(0, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, -1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, 1));
	private static final ImmutableList<Vector3i> RESPAWN_OFFSETS = (new Builder<Vector3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::above).iterator()).add(new Vector3i(0, 1, 0)).build();
	
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
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	
	public BonfireBlock()
	{
		super(AbstractBlock.Properties.of(Material.DIRT)
				.strength(15f)
				.sound(SoundType.GRAVEL));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1)).setValue(HORIZONTAL_FACING, Direction.NORTH));
		this.runCalculation(SHAPE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		if (context.getLevel().isEmptyBlock(context.getClickedPos().below())) return null;
		return this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(1)).setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(1)).setValue(HORIZONTAL_FACING, Direction.NORTH);
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) 
	{
	    super.createBlockStateDefinition(container);
		container.add(LIT, ESTUS_VOLUME_LEVEL, ESTUS_HEAL_LEVEL, HORIZONTAL_FACING);
	}
	
	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
	{
		if (player.hasEffect(ModEffects.UNDEAD_CURSE.get()))
		{
			BonfireTileEntity tileentity = (BonfireTileEntity)level.getBlockEntity(pos);
			if (!state.getValue(LIT))
			{
				if (player.getItemInHand(hand).getItem() == ModItems.DARKSIGN.get())
				{
					if (tileentity.hasFireKeeper())
					{
						player.sendMessage(new TranslationTextComponent("gui.darksouls.fire_keeper_absent"), Util.NIL_UUID);
						return ActionResultType.SUCCESS;
					}
					else if (!tileentity.hasName() && ModNetworkManager.connection != null)
					{
						ModNetworkManager.connection.openBonfireNameScreen(player, tileentity);
						return ActionResultType.SUCCESS;
					}
				}
				return ActionResultType.PASS;
			}
			else
			{
				Item item = player.getItemInHand(hand).getItem();
				if (item == ModItems.ESTUS_FLASK.get() || item == ModItems.UNDEAD_BONE_SHARD.get())
				{
					return ActionResultType.PASS;
				}
				
				player.heal(player.getMaxHealth() - player.getHealth());
				
				if (ModNetworkManager.connection != null)
				{
					if (!tileentity.hasName())
					{
						ModNetworkManager.connection.openBonfireNameScreen(player, tileentity);
					}
					else
					{
						ModNetworkManager.connection.openBonfireScreen(tileentity);
					}
				}
				
				Optional<Vector3d> optional = findStandUpPosition(EntityType.PLAYER, level, pos);
				if (optional.isPresent())
				{
					if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity)player).setRespawnPosition(level.dimension(), new BlockPos(optional.get()), 0.0F, true, true);
				}
				else
				{
					player.sendMessage(new TranslationTextComponent("gui.darksouls.respawn_position_fail_message"), Util.NIL_UUID);
				}
				
				return ActionResultType.SUCCESS;
			}
		}
		else
		{
			player.hurt(DamageSource.IN_FIRE, player.getMaxHealth() / 2.0F);
			return ActionResultType.SUCCESS;
		}
	}
	
	public static Optional<Vector3d> findStandUpPosition(EntityType<?> p_235560_0_, ICollisionReader p_235560_1_, BlockPos p_235560_2_)
	{
	    Optional<Vector3d> optional = findStandUpPosition(p_235560_0_, p_235560_1_, p_235560_2_, true);
	    return optional.isPresent() ? optional : findStandUpPosition(p_235560_0_, p_235560_1_, p_235560_2_, false);
	}

	private static Optional<Vector3d> findStandUpPosition(EntityType<?> p_242678_0_, ICollisionReader p_242678_1_, BlockPos p_242678_2_, boolean p_242678_3_)
	{
	    BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

	    for(Vector3i vector3i : RESPAWN_OFFSETS)
	    {
	         blockpos$mutable.set(p_242678_2_).move(vector3i);
	         Vector3d vector3d = TransportationHelper.findSafeDismountLocation(p_242678_0_, p_242678_1_, blockpos$mutable, p_242678_3_);
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
	
	public void setLit(World level, BlockState blockstate, BlockPos blockpos, boolean value)
	{
	    if (blockstate.is(this) && blockstate.getValue(LIT) != value)
	    {
	       level.setBlock(blockpos, blockstate.setValue(LIT, Boolean.valueOf(value)), 3);
	    }
	}
	
	public void raiseEstusHealLevel(World level, BlockState blockstate, BlockPos blockpos)
	{
		if (blockstate.is(this) && blockstate.getValue(LIT) && blockstate.getValue(ESTUS_HEAL_LEVEL) < 10)
	    {
	       level.setBlock(blockpos, blockstate.setValue(ESTUS_HEAL_LEVEL, Integer.valueOf(blockstate.getValue(ESTUS_HEAL_LEVEL) + 1)), 3);
	    }
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos vertex, ISelectionContext context)
	{
		return SHAPES.get(state.getValue(HORIZONTAL_FACING));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) 
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) 
	{
		return ModTileEntities.BONFIRE.get().create();
	}
	
	public void kindle(World level, BlockState blockstate, BlockPos blockpos)
	{
	    if (blockstate.is(this) && blockstate.getValue(ESTUS_VOLUME_LEVEL) < 4)
	    {
	       level.setBlock(blockpos, blockstate.setValue(ESTUS_VOLUME_LEVEL, Integer.valueOf(blockstate.getValue(ESTUS_VOLUME_LEVEL) + 1)), 3);
	    }
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World worldIn, BlockPos vertex, Random random)
	{
		if (state.getValue(LIT))
		{
			if (random.nextInt(8) == 0)
			{
				worldIn.playLocalSound((double)vertex.getX() + 0.5D, (double)vertex.getY() + 0.5D, (double)vertex.getZ() + 0.5D, ModSoundEvents.BONFIRE_AMBIENT, SoundCategory.BLOCKS, 0.3F, random.nextFloat() * 0.7F + 0.3F, false);
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
}
