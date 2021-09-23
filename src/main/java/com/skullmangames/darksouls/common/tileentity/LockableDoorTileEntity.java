package com.skullmangames.darksouls.common.tileentity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.block.LockableBlock;
import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class LockableDoorTileEntity extends TileEntity
{
	private UUID keyUUID;
	private String keyName = "";
	private boolean locked = false;
	
	public LockableDoorTileEntity()
	{
		super(TileEntityTypeInit.LOCKABLE_BLOCK.get());
	}
	
	public void tryInteract(PlayerEntity player, UUID uuid)
	{
		this.tryInteract(player, uuid, null);
	}
	
	public void tryInteract(PlayerEntity player, UUID uuid, @Nullable String name)
	{
		if (uuid == null) return;
		if (this.keyUUID == null) this.setKeyUUID(uuid);
		else if (!this.keyUUID.toString().contentEquals(uuid.toString()))
		{
			if (player instanceof ServerPlayerEntity)
			{
				((ServerPlayerEntity)player).connection.send(new STitlePacket(STitlePacket.Type.ACTIONBAR, new StringTextComponent("Locked by "+this.getKeyName())));
			}
			return;
		}
		boolean flag = !this.locked;
		for (BlockPos pos : this.getBlock().getPartPositions(this.level, this.worldPosition))
	    {
	    	LockableDoorTileEntity tileentity = (LockableDoorTileEntity)this.level.getBlockEntity(pos);
	    	if (tileentity == null) continue;
	    	tileentity.setKeyUUID(this.keyUUID);
	    	if (name != null) tileentity.setKeyName(name);
	    	tileentity.setLocked(flag);
	    }
		
		if (player instanceof ServerPlayerEntity)
		{
			((ServerPlayerEntity)player).connection.send(new STitlePacket(STitlePacket.Type.ACTIONBAR, new StringTextComponent(flag ? "Locked" : "Unlocked")));
		}
	}
	
	private LockableBlock getBlock()
	{
		if (!(this.getBlockState().getBlock() instanceof LockableBlock)) return null;
		return (LockableBlock)this.getBlockState().getBlock();
	}
	
	public void setKeyUUID(UUID uuid)
	{
		this.keyUUID = uuid;
		this.setChanged();
	}
	
	public void setKeyName(String name)
	{
		this.keyName = name;
		this.setChanged();
	}
	
	public String getKeyName()
	{
		return this.keyName;
	}
	
	public void setLocked(boolean value)
	{
		this.locked = value;
		this.setChanged();
	}
	
	public boolean isLocked()
	{
		return this.locked;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		super.save(nbt);
		if (this.keyUUID != null) nbt.putUUID("key_uuid", this.keyUUID);
		nbt.putBoolean("locked", this.locked);
		nbt.putString("key_name", this.keyName);
	    return nbt;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);
		if (nbt.hasUUID("key_uuid")) this.keyUUID = nbt.getUUID("key_uuid");
		this.keyName = nbt.getString("key_name");
		this.locked = nbt.getBoolean("locked");
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
		this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
	}
}
