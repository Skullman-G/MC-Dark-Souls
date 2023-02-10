package com.skullmangames.darksouls.common.blockentity;

import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightSourceBlockEntity extends TileEntity
{
	private float timer;
	
	public LightSourceBlockEntity()
	{
		super(ModBlockEntities.LIGHT_SOURCE.get());
	}
	
	public void setTimer(float value)
	{
		this.timer = value;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		nbt = super.save(nbt);
		nbt.putFloat("timer", this.timer);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);
		this.timer = nbt.getFloat("timer");
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
	    CompoundNBT nbtTag = new CompoundNBT();
	    nbtTag = this.save(nbtTag);
	    return new SUpdateTileEntityPacket(this.getBlockPos(), -1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
	    CompoundNBT tag = pkt.getTag();
	    this.load(this.getBlockState(), tag);
	}
	
	@Override
	public CompoundNBT getUpdateTag()
	{
		return this.save(new CompoundNBT());
	}
	
	public static <T extends TileEntity>void tick(World level, BlockPos pos, BlockState state, T blockEntity)
	{
		LightSourceBlockEntity lightSource = (LightSourceBlockEntity)blockEntity;
		if (lightSource.timer > 0)
		{
			lightSource.timer = Math.max(lightSource.timer - 0.05F, 0);
			if (lightSource.timer == 0) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}
}
