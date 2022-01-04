package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import com.skullmangames.darksouls.network.server.STCSpawnSoulPacket;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.STitlePacket;

public interface IModClientPlayNetHandler
{
	void handleAddSoulEntity(STCSpawnSoulPacket packet);
	
	void openBonfireNameScreen(PlayerEntity player, BonfireTileEntity tileentity);
	
	void openBonfireScreen(BonfireTileEntity tileentity);
	
	void handleSetTitles(STitlePacket packet);
	
	void openFireKeeperScreen(int firekeeperid);
}
