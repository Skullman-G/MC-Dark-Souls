package com.skullmangames.darksouls.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.registry.DynamicRegistries.Impl;
import net.minecraft.world.storage.PlayerData;

public class PlayerListOverride extends PlayerList
{
	public PlayerListOverride(MinecraftServer minecraftserver, Impl impl, PlayerData playerdata, int p_i231425_4_)
	{
		super(minecraftserver, impl, playerdata, p_i231425_4_);
	}
}
