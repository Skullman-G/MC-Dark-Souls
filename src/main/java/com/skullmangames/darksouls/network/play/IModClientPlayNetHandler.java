package com.skullmangames.darksouls.network.play;

import net.minecraft.network.chat.Component;

public interface IModClientPlayNetHandler
{
	void setTitle(Component text, int fadein, int stay, int fadeout);
	
	void setOverlayMessage(Component text);
	
	void openFireKeeperScreen(int firekeeperid);
}
