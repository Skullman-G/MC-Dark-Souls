package com.skullmangames.darksouls.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.MinecraftServer;

public class ServerPlayNetHandlerOverride extends ServerPlayNetHandler
{
	public ServerPlayNetHandlerOverride(MinecraftServer p_i1530_1_, NetworkManager p_i1530_2_, ServerPlayerEntity p_i1530_3_)
	{
		super(p_i1530_1_, p_i1530_2_, p_i1530_3_);
	}
}
