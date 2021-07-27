package com.skullmangames.darksouls.core.init;

import com.mojang.brigadier.CommandDispatcher;
import com.skullmangames.darksouls.common.commands.ClearCommandOverride;
import com.skullmangames.darksouls.common.commands.GiveCommandOverride;

import net.minecraft.command.CommandSource;

public class CommandInit
{
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		ClearCommandOverride.register(dispatcher);
		GiveCommandOverride.register(dispatcher);
	}
}