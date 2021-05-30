package com.skullmangames.darksouls.common.tiles;

import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.server.ServerWorld;

public class BonfireTileEntity extends TileEntity
{
	private String name = "";
	private boolean lit;
	
	public BonfireTileEntity(TileEntityType<?> tileEntityTypeIn) 
	{
		super(tileEntityTypeIn);
	}
	
	public BonfireTileEntity() 
	{
		this(TileEntityTypeInit.BONFIRE_TILE_ENTITY.get());
	}
	
	public void setRespawnPos(PlayerEntity playerentity)
	{
		if (!this.level.isClientSide)
		{
			System.out.print("respawnpointset");
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)playerentity;
			serverplayerentity.setRespawnPosition(this.level.dimension(), serverplayerentity.blockPosition(), 0.0F, false, true);
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		super.save(nbt);
		nbt.putBoolean("lit", this.isLit());
		nbt.putString("name", this.getName());
	    return nbt;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);
	    this.lit = nbt.getBoolean("lit");
	    this.name = nbt.getString("name");
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		CompoundNBT nbt = this.save(new CompoundNBT());
		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		CompoundNBT nbt = pkt.getTag();
		this.load(getBlockState(), nbt);
		this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
	}
	
	public void setName(String value)
	{
		this.name = value;
		this.setChanged();
	}
	
	public boolean isLit()
	{
		return this.lit;
	}
	
	public void setLit(boolean value)
	{
		this.lit = value;
		this.setChanged();
	}
	
	public String getName()
	{
		return this.name;
	}
}
