package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.common.command.argument.StatArgument;

import net.minecraft.commands.synchronization.ArgumentTypes;

public class ModArgumentTypes
{
	public static void bootstrap()
	{
		ArgumentTypes.register("darksouls:stat", StatArgument.class, new StatArgument.Serializer());
	}
}
