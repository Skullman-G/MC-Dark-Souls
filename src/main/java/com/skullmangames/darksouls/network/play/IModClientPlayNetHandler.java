package com.skullmangames.darksouls.network.play;

import net.minecraft.network.chat.Component;

public interface IModClientPlayNetHandler
{
	void handleSetTitles(Component text, int fadein, int stay, int fadeout);
	
	void openFireKeeperScreen(int firekeeperid);
}
