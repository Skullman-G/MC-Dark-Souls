package com.skullmangames.darksouls.network.play;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import com.skullmangames.darksouls.common.tileentity.BonfireTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
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
	public void openBonfireNameScreen(Player player, BonfireTileEntity tileentity)
	{
		this.minecraft.setScreen(new BonfireNameScreen(player, tileentity));
	}

	@Override
	public void openBonfireScreen(BonfireTileEntity tileentity)
	{
		this.minecraft.setScreen(new BonfireScreen(tileentity));
	}

	@Override
	public void handleSetTitles(Component text, int fadein, int stay, int fadeout)
	{
		this.minecraft.gui.setTimes(fadein, stay, fadeout);
		this.minecraft.gui.setTitle(text);
	}

	@Override
	public void openFireKeeperScreen(int firekeeperid)
	{
		this.minecraft.setScreen(new FireKeeperScreen(firekeeperid));
	}
}
