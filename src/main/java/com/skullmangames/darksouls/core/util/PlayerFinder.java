package com.skullmangames.darksouls.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PlayerFinder
{
	public static ServerPlayerEntity findServerPlayer(ClientPlayerEntity player)
	{
		return player.getServer().getPlayerList().getPlayer(player.getUUID());
	}
	
	public static ClientPlayerEntity findClientPlayer(ServerPlayerEntity player)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (player.getUUID() == minecraft.player.getUUID()) return null;
		return minecraft.player;
	}
}
