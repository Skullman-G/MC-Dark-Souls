package com.skullmangames.darksouls.common.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class HumanityCommand
{
	private static final SimpleCommandExceptionType ERROR_COMMAND_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("command.failed"));
	
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("humanity").requires((stack) ->
		{
			return stack.hasPermission(2);
		})
		.then(Commands.literal("give")
				.then(Commands.argument("targets", EntityArgument.players()).executes((command) ->
				{
					return addHumanity(command.getSource(), EntityArgument.getPlayers(command, "targets"), 0);
				})
				.then(Commands.argument("amount", IntegerArgumentType.integer(0, 99)).executes((command) ->
				{
					return addHumanity(command.getSource(), EntityArgument.getPlayers(command, "targets"), IntegerArgumentType.getInteger(command, "amount"));
				}))))
		.then(Commands.literal("remove")
				.then(Commands.argument("targets", EntityArgument.players()).executes((command) ->
				{
					return addHumanity(command.getSource(), EntityArgument.getPlayers(command, "targets"), 0);
				})
				.then(Commands.argument("amount", IntegerArgumentType.integer(0, 99)).executes((command) ->
				{
					return addHumanity(command.getSource(), EntityArgument.getPlayers(command, "targets"), -IntegerArgumentType.getInteger(command, "amount"));
				})))));
	}
	
	private static int addHumanity(CommandSource command, Collection<ServerPlayerEntity> players, int amount) throws CommandSyntaxException
	{
		int i = 0;
		
		for (ServerPlayerEntity player : players)
		{
			ServerPlayerCap playerCap = (ServerPlayerCap)player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null)
			{
				playerCap.raiseHumanity(amount);
				i++;
			}
		}
		
		if (i == 0) throw ERROR_COMMAND_FAILED.create();
		
		return i;
	}
}
