package com.skullmangames.darksouls.common.blockentity;

import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LightSourceBlockEntity extends BlockEntity
{
	private float timer;
	
	public LightSourceBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.LIGHT_SOURCE.get(), pos, state);
	}
	
	public void setTimer(float value)
	{
		this.timer = value;
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putFloat("timer", this.timer);
	}

	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.timer = nbt.getFloat("timer");
	}
	
	public static <T extends BlockEntity>void tick(Level level, BlockPos pos, BlockState state, T blockEntity)
	{
		LightSourceBlockEntity lightSource = (LightSourceBlockEntity)blockEntity;
		if (lightSource.timer > 0)
		{
			lightSource.timer = Math.max(lightSource.timer - 0.05F, 0);
			if (lightSource.timer == 0) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}
}
