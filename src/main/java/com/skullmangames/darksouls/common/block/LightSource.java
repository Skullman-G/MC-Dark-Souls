package com.skullmangames.darksouls.common.block;

import com.skullmangames.darksouls.common.blockentity.LightSourceBlockEntity;
import com.skullmangames.darksouls.core.init.ModBlockEntities;
import com.skullmangames.darksouls.core.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LightSource extends Block implements EntityBlock
{
	public static final IntegerProperty LIGHT_EMISSION = IntegerProperty.create("light_emission", 0, 100);
	
	public LightSource()
	{
		super(BlockBehaviour.Properties.of(Material.AIR));
		this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_EMISSION, 1));
	}
	
	public static void setLightSource(Level level, BlockPos pos, int lightEmission, float time)
	{
		BlockState state = ModBlocks.LIGHT_SOURCE.get().defaultBlockState().setValue(LIGHT_EMISSION, lightEmission);
		if (level.getBlockState(pos).isAir())
		{
			level.setBlock(pos, state, UPDATE_ALL);
			level.getBlockEntity(pos, ModBlockEntities.LIGHT_SOURCE.get()).ifPresent((blockEntity) ->
			{
				blockEntity.setTimer(time);
			});
		}
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(LIGHT_EMISSION);
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
	{
		return state.getValue(LIGHT_EMISSION);
	}
	
	public RenderShape getRenderShape(BlockState p_48758_)
	{
		return RenderShape.INVISIBLE;
	}

	public VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_)
	{
		return Shapes.empty();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new LightSourceBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return type == ModBlockEntities.LIGHT_SOURCE.get() ? LightSourceBlockEntity::tick : null;
	}
}
