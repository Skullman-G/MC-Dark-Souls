package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface IModClientPlayNetHandler
{
	void openBonfireNameScreen(Player player, BonfireTileEntity tileentity);
	
	void openBonfireScreen(BonfireTileEntity tileentity);
	
	void handleSetTitles(Component text, int fadein, int stay, int fadeout);
	
	void openFireKeeperScreen(int firekeeperid);
}
