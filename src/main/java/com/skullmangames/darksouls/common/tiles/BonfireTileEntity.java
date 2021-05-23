package com.skullmangames.darksouls.common.tiles;

import com.skullmangames.darksouls.core.init.TileEntityTypeInit;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class BonfireTileEntity extends TileEntity
{
	public BonfireTileEntity(TileEntityType<?> tileEntityTypeIn) 
	{
		super(tileEntityTypeIn);
	}
	
	public BonfireTileEntity() 
	{
		this(TileEntityTypeInit.BONFIRE_TILE_ENTITY.get());
	}
}
