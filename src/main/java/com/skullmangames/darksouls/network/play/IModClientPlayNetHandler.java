package com.skullmangames.darksouls.network.play;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public interface IModClientPlayNetHandler
{
	void setTitle(ITextComponent text, int fadein, int stay, int fadeout);
	
	void setOverlayMessage(ITextComponent text);
	
	void openBonfireNameScreen(BlockPos blockPos);
	
	void openBonfireScreen(BlockPos blockPos);
	
	void tryPlayBonfireAmbientSound(BlockPos blockPos);
	
	void removeBonfireAmbientSound(BlockPos blockPos);
}
