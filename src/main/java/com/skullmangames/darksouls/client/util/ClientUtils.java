package com.skullmangames.darksouls.client.util;

import com.skullmangames.darksouls.client.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.screens.BonfireScreen;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils
{
	private ClientUtils()
	{
		throw new IllegalAccessError("Attempted to construct utility class.");
	}
	
	public static void openBonfireNameScreen(BonfireTileEntity tileentity)
	{
		Minecraft.getInstance().setScreen(new BonfireNameScreen(tileentity));
	}
	
	public static void openBonfireScreen(BonfireTileEntity tileentity)
	{
		Minecraft.getInstance().setScreen(new BonfireScreen(tileentity));
	}
}
