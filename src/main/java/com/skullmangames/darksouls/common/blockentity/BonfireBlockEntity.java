package com.skullmangames.darksouls.common.blockentity;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCBonfireKindleEffect;
import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;

public class BonfireBlockEntity extends TileEntity
{
	private String name = "";
	private boolean hasFireKeeper;
	private String fireKeeperStringUUID = "";

	public BonfireBlockEntity()
	{
		super(ModBlockEntities.BONFIRE.get());
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		super.save(nbt);
		nbt.putString("name", this.name);
		nbt.putBoolean("has_fire_keeper", this.hasFireKeeper);
		nbt.putString("fire_keeper_string_uuid", this.fireKeeperStringUUID);
		return nbt;
	}

	@Override
	public void load(BlockState blockstate, CompoundNBT nbt)
	{
		super.load(blockstate, nbt);
		this.name = nbt.getString("name");
		this.hasFireKeeper = nbt.getBoolean("has_fire_keeper");
		this.fireKeeperStringUUID = nbt.getString("fire_keeper_string_uuid");
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

	private void markDirty()
	{
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
	}

	public void setName(String value)
	{
		this.name = value;
		this.markDirty();
	}

	public void setLit(boolean value)
	{
		if (this.getBlockState().getValue(BonfireBlock.LIT) == value) return;
		if (value) this.triggerKindleEffects();
		this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.LIT, value), 3);
	}
	
	public boolean isLit()
	{
		return this.getBlockState().getValue(BonfireBlock.LIT);
	}

	public String getName()
	{
		return this.name;
	}

	public void kindle()
	{
		int volumelevel = this.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL);
		if (volumelevel >= 4) return;
		this.triggerKindleEffects();
		level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.ESTUS_VOLUME_LEVEL, volumelevel + 1), 3);
	}
	
	public boolean canKindle()
	{
		return this.getBlockState().getValue(BonfireBlock.ESTUS_VOLUME_LEVEL) < 2;
	}

	private void triggerKindleEffects()
	{
		ModNetworkManager.sendToAll(new STCBonfireKindleEffect(this.worldPosition));
		this.level.playSound(null, this.worldPosition, ModSoundEvents.BONFIRE_LIT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	public void raiseEstusHealLevel()
	{
		int heallevel = this.getBlockState().getValue(BonfireBlock.ESTUS_HEAL_LEVEL);
		if (heallevel >= 10) return;
		this.triggerKindleEffects();
		this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.ESTUS_HEAL_LEVEL, heallevel + 1), 3);
	}

	public boolean hasName()
	{
		return !this.name.isEmpty();
	}

	public void addFireKeeper(String uuid)
	{
		this.fireKeeperStringUUID = uuid;
		this.hasFireKeeper = true;
		this.triggerKindleEffects();
		this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BonfireBlock.LIT, true).setValue(BonfireBlock.ESTUS_VOLUME_LEVEL, 2), 3);
		this.markDirty();
	}

	public boolean hasFireKeeper()
	{
		return this.hasFireKeeper;
	}

	public String getFireKeeperStringUUID()
	{
		return this.fireKeeperStringUUID;
	}
}
