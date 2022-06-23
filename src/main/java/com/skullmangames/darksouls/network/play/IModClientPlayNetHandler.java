package com.skullmangames.darksouls.network.play;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public interface IModClientPlayNetHandler
{
	void setTitle(Component text, int fadein, int stay, int fadeout);
	
	void setOverlayMessage(Component text);
	
	void openBonfireNameScreen(BlockPos blockPos);
	
	void openBonfireScreen(BlockPos blockPos);
	
	void tryPlayBonfireAmbientSound(BlockPos blockPos);
	
	void removeBonfireAmbientSound(BlockPos blockPos);
}
