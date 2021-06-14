package com.skullmangames.darksouls.common.blocks;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.skullmangames.darksouls.client.util.ClientUtils;
import com.skullmangames.darksouls.common.items.Darksign;
import com.skullmangames.darksouls.common.items.EstusFlask;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.init.SoundEventInit;
import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
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
	private static final ImmutableList<Vector3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vector3i(0, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, -1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, 1));
	private static final ImmutableList<Vector3i> RESPAWN_OFFSETS = (new Builder<Vector3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vector3i::above).iterator()).add(new Vector3i(0, 1, 0)).build();
	
	protected static final VoxelShape SHAPE = Stream.of(
			Block.box(2, 0.75, 2, 14, 1.75, 14),
			Block.box(1, 0, 1, 15, 1, 15),
			Block.box(4, 1.5, 4, 12, 2.5, 12),
			Block.box(6, 2.4, 6, 10, 3.4, 10),
			Block.box(7.5, 3, 7.5, 8.5, 19, 8.5),
			Block.box(6.5, 14, 7.5, 7.5, 15, 8.5),
			Block.box(8.5, 14, 7.5, 9.5, 15, 8.5)
			).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();
	
	public BonfireBlock()
	{
		super(AbstractBlock.Properties.of(Material.DIRT)
				.strength(15f)
				.sound(SoundType.GRAVEL));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)).setValue(HORIZONTAL_FACING, Direction.NORTH));
		this.runCalculation(SHAPE);
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) 
	{
	    super.createBlockStateDefinition(container);
		container.add(LIT, HORIZONTAL_FACING);
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result)
	{
		TileEntity tileentity = world.getBlockEntity(pos);
		
		// Bonfire is not lit
		if (!this.isLit(state))
		{
			// Has to hold Darksign to light bonfire
			if (player.getItemInHand(hand).getItem() instanceof Darksign)
			{
				// SERVER SIDE
				if (!world.isClientSide && tileentity instanceof BonfireTileEntity)
				{
					BonfireTileEntity bonfiretileentity = (BonfireTileEntity)tileentity;
					
					if (bonfiretileentity.hasFireKeeper())
					{
						ServerPlayerEntity serverplayer = player.getServer().getPlayerList().getPlayer(player.getUUID());
						serverplayer.sendMessage(new TranslationTextComponent("gui.darksouls.fire_keeper_absent"), Util.NIL_UUID);
					}
					else if (!bonfiretileentity.hasName())
					{
						ClientUtils.openBonfireNameScreen(player, (BonfireTileEntity)tileentity);
					}
				}
			}
			return ActionResultType.sidedSuccess(world.isClientSide);
		}
		
		// Bonfire is lit
		else
		{
			// Cancel when filling Estus Flask
			if (player.getItemInHand(hand).getItem() instanceof EstusFlask)
			{
				return ActionResultType.PASS;
			}
			
			// Heal or hurt
			if (player.hasEffect(EffectInit.UNDEAD_CURSE.get()))
			{
				player.heal(player.getMaxHealth() - player.getHealth());
			}
			else
			{
				player.hurt(DamageSource.IN_FIRE, player.getMaxHealth() / 2.0F);
			}
			
			// SERVER SIDE
			if (!world.isClientSide && tileentity instanceof BonfireTileEntity)
			{
				// Open Screens
				BonfireTileEntity bonfiretileentity = (BonfireTileEntity)tileentity;
				if (!(bonfiretileentity).hasName())
				{
					ClientUtils.openBonfireNameScreen(player, bonfiretileentity);
				}
				else
				{
					ClientUtils.openBonfireScreen(bonfiretileentity);
				}
				
				// Set spawn point
				ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)player;
				Optional<Vector3d> optional = findStandUpPosition(EntityType.PLAYER, world, pos);
				if (optional.isPresent())
				{
					serverplayerentity.setRespawnPosition(world.dimension(), new BlockPos(optional.get()), 0.0F, true, true);
				}
				else
				{
					serverplayerentity.sendMessage(new TranslationTextComponent("gui.darksouls.respawn_position_fail_message"), Util.NIL_UUID);
				}
			}
			
			return ActionResultType.sidedSuccess(world.isClientSide);
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
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
	{
		if (this.isLit(state))
		{
			return 10;
		}
		else
		{
			return 0;
		}
	}
	
	public boolean isLit(BlockState blockstate)
	{
	    return blockstate.getValue(LIT);
	}
	
	public void setLit(World world, BlockState blockstate, BlockPos blockpos, boolean value)
	{
	    if (blockstate.is(this) && blockstate.getValue(LIT) != value)
	    {
	       world.setBlock(blockpos, blockstate.setValue(LIT, Boolean.valueOf(value)), 3);
	    }
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPES.get(state.getValue(HORIZONTAL_FACING));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) 
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) 
	{
		return TileEntityTypeInit.BONFIRE_TILE_ENTITY.get().create();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World worldIn, BlockPos pos, Random random)
	{
		if (this.isLit(state))
		{
			if (random.nextInt(8) == 0)
			{
				worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEventInit.BONFIRE_AMBIENT.get(), SoundCategory.BLOCKS, 0.05F, 1.0F, false);
			}
			worldIn.addParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
	        worldIn.addParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.4D, (double)pos.getY() + 0.3D, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
	        worldIn.addParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.6D, (double)pos.getY() + 0.3D, (double)pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
	        worldIn.addParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D, (double)pos.getZ() + 0.4D, 0.0D, 0.0D, 0.0D);
	        worldIn.addParticle(ParticleTypes.FLAME, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.3D, (double)pos.getZ() + 0.6D, 0.0D, 0.0D, 0.0D);
		}
	}
}
