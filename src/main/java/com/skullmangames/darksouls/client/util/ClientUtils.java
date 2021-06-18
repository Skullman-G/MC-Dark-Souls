package com.skullmangames.darksouls.client.util;

import com.skullmangames.darksouls.client.gui.screens.BonfireNameScreen;
import com.skullmangames.darksouls.client.gui.screens.BonfireScreen;
import com.skullmangames.darksouls.common.tiles.BonfireTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils
{
	private ClientUtils()
	{
		throw new IllegalAccessError("Attempted to construct utility class.");
	}
	
	public static void openBonfireNameScreen(PlayerEntity player, BonfireTileEntity tileentity)
	{
		Minecraft.getInstance().setScreen(new BonfireNameScreen(player, tileentity));
	}
	
	public static void openBonfireScreen(BonfireTileEntity tileentity, PlayerEntity player)
	{
		Minecraft.getInstance().setScreen(new BonfireScreen(tileentity, player));
	}
}
