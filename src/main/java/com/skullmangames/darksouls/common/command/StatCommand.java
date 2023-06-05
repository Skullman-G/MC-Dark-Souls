package com.skullmangames.darksouls.common.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.command.argument.StatArgument;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.StatHolder.ChangeRequest;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCStat;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class StatCommand
{
	private static final SimpleCommandExceptionType ERROR_COMMAND_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("command.failed"));
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("dsstats").requires((stack) ->
		{
			return stack.hasPermission(2);
		})
		.then(Commands.argument("targets", EntityArgument.players())
		.then(Commands.argument("stat", StatArgument.stat())
		.then(Commands.argument("value", IntegerArgumentType.integer(Stats.STANDARD_LEVEL, Stats.MAX_LEVEL))
		.executes((command) ->
		{
			return setStat(command.getSource(), EntityArgument.getPlayers(command, "targets"),
									StatArgument.getStat(command, "stat"),
									IntegerArgumentType.getInteger(command, "value"));
		})))));
	}
	
	private static int setStat(CommandSourceStack command, Collection<ServerPlayer> players, Stat stat, int value) throws CommandSyntaxException
	{
		int i = 0;
		if (stat != null)
		{
			for (ServerPlayer player : players)
			{
				ServerPlayerCap playerCap = (ServerPlayerCap) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				if (playerCap != null)
				{
					ChangeRequest changes = playerCap.getStats().requestChange();
					changes.set(stat, value);
					ModNetworkManager.sendToPlayer(new STCStat(player.getId(), changes), player);
					i++;
				}
			}
		}

		if (i == 0) throw ERROR_COMMAND_FAILED.create();

		return i;
	}
}
