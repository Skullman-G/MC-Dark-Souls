package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSetMaxPlayerLevel;

import net.minecraft.world.level.GameRules;

public class ModGameRules
{
	public static final GameRules.Key<GameRules.IntegerValue> MAX_PLAYER_LEVEL = GameRules.register("max_player_level", GameRules.Category.PLAYER, GameRules.IntegerValue.create(500, (server, value) ->
	{
		ModNetworkManager.sendToAll(new STCSetMaxPlayerLevel(value.get()));
	}));
	
	public static void call() {}
}
