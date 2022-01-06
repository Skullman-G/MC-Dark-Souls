package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.server.STitlePacket;

public interface IModClientPlayNetHandler
{
	void openBonfireNameScreen(PlayerEntity player, BonfireTileEntity tileentity);
	
	void openBonfireScreen(BonfireTileEntity tileentity);
	
	void handleSetTitles(STitlePacket packet);
	
	void openFireKeeperScreen(int firekeeperid);
}
