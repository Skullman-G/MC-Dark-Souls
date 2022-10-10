package com.skullmangames.darksouls.common.blockentity;

import com.skullmangames.darksouls.common.block.BonfireBlock;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BonfireBlockEntity extends BlockEntity
{
	private String name = "";
	private boolean hasFireKeeper;
	private String fireKeeperStringUUID = "";
	private boolean requestKindleEffect;

	public BonfireBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.BONFIRE.get(), pos, state);
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putString("name", this.name);
		nbt.putBoolean("has_fire_keeper", this.hasFireKeeper);
		nbt.putString("fire_keeper_string_uuid", this.fireKeeperStringUUID);
		nbt.putBoolean("request_kindle_effect", this.requestKindleEffect);
	}

	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		this.name = nbt.getString("name");
		this.hasFireKeeper = nbt.getBoolean("has_fire_keeper");
		this.fireKeeperStringUUID = nbt.getString("fire_keeper_string_uuid");
		this.requestKindleEffect = nbt.getBoolean("request_kindle_effect");
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}

	private void markDirty()
	{
		this.setChanged();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
	}
	
	public boolean requestsKindleEffect()
	{
		return this.requestKindleEffect;
	}
	
	public void setRequestKindleEffect(boolean value)
	{
		this.requestKindleEffect = value;
		this.markDirty();
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
		this.setRequestKindleEffect(true);
		this.level.playSound(null, this.worldPosition, ModSoundEvents.BONFIRE_LIT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
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
		return !this.name.isBlank();
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
