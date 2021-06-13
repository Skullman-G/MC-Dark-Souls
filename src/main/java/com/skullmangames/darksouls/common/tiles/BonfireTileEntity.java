package com.skullmangames.darksouls.common.tiles;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.blocks.Bonfire;
import com.skullmangames.darksouls.common.entities.FireKeeperEntity;
import com.skullmangames.darksouls.core.init.CriteriaTriggerInit;
import com.skullmangames.darksouls.core.init.SoundEventInit;
import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class BonfireTileEntity extends TileEntity
{
	private String name = "";
	private boolean hasFireKeeper;
	
	public BonfireTileEntity(TileEntityType<?> tileEntityTypeIn) 
	{
		super(tileEntityTypeIn);
	}
	
	public BonfireTileEntity() 
	{
		this(TileEntityTypeInit.BONFIRE_TILE_ENTITY.get());
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt)
	{
		super.save(nbt);
		nbt.putString("name", this.name);
		nbt.putBoolean("has_fire_keeper", this.hasFireKeeper);
	    return nbt;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt)
	{
		super.load(state, nbt);
	    this.name = nbt.getString("name");
	    this.hasFireKeeper = nbt.getBoolean("has_fire_keeper");
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
	
	public void setName(String value)
	{
		this.name = value;
		this.setChanged();
	}
	
	public void setLit(World world, @Nullable PlayerEntity player, boolean value)
	{
		world.playSound(null, this.worldPosition, SoundEventInit.BONFIRE_LIT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
		Bonfire bonfire = (Bonfire)this.getBlockState().getBlock();
		bonfire.setLit(this.level, this.getBlockState(), this.worldPosition, value);
		
		if (player != null)
		{
			ServerPlayerEntity serverplayer = player.getServer().getPlayerList().getPlayer(player.getUUID());
			serverplayer.sendMessage(new TranslationTextComponent("gui.darksouls.bonfire_lit_message"), Util.NIL_UUID);
			CriteriaTriggerInit.BONFIRE_LIT.trigger(serverplayer, this.getBlockState().getValue(Bonfire.LIT));
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void addFireKeeper()
	{
		this.hasFireKeeper = true;
	}
	
	public boolean hasFireKeeper()
	{
		return this.hasFireKeeper;
	}
}
