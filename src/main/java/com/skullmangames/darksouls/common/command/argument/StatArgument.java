package com.skullmangames.darksouls.common.command.argument;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class StatArgument implements ArgumentType<Stat>
{
	public static final SimpleCommandExceptionType NO_STAT_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("argument.stat.notfound"));
	
	public static StatArgument stat()
	{
		return new StatArgument();
	}
	
	@Override
	public Stat parse(StringReader reader) throws CommandSyntaxException
	{
		String statName = reader.readString();
		if (Stats.STATS.containsKey(statName))
		{
			return Stats.STATS.get(statName);
		}
		else throw NO_STAT_FOUND.create();
	}
	
	public static Stat getStat(CommandContext<CommandSourceStack> ctx, String argName) throws CommandSyntaxException
	{
		return ctx.getArgument(argName, Stat.class);
	}
	
	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		for (String statName : Stats.STATS.keySet())
		{
			builder.suggest(statName);
		}
		return builder.buildFuture();
	}
}
