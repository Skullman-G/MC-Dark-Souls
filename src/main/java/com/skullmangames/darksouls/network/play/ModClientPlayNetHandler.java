package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientPlayNetHandler implements IModClientPlayNetHandler
{
	private final Minecraft minecraft;
	
	public ModClientPlayNetHandler()
	{
		this.minecraft = Minecraft.getInstance();
	}

	@Override
	public void setTitle(Component text, int fadein, int stay, int fadeout)
	{
		this.minecraft.gui.setTimes(fadein, stay, fadeout);
		this.minecraft.gui.setTitle(text);
	}
	
	@Override
	public void setOverlayMessage(Component text)
	{
		this.minecraft.gui.setOverlayMessage(text, false);
	}

	@Override
	public void openFireKeeperScreen(int firekeeperid)
	{
		this.minecraft.setScreen(new FireKeeperScreen(firekeeperid));
	}
}
