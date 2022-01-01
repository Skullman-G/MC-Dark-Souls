package com.skullmangames.darksouls.client.util;

import com.skullmangames.darksouls.client.gui.screens.FireKeeperScreen;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils
{
	private ClientUtils()
	{
		throw new IllegalAccessError("Attempted to construct utility class.");
	}
	
	public static void openFireKeeperScreen(FireKeeperEntity firekeeper, ServerPlayerEntity serverplayer)
	{
		Minecraft.getInstance().setScreen(new FireKeeperScreen(firekeeper, serverplayer));
	}
}
