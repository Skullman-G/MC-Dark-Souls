package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.command.HumanityCommand;
import com.skullmangames.darksouls.common.command.SoulsCommand;
import com.skullmangames.darksouls.common.command.StatCommand;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE)
public class ModCommands
{
	@SubscribeEvent
	public static void onCommandRegister(RegisterCommandsEvent event)
	{
		SoulsCommand.register(event.getDispatcher());
		HumanityCommand.register(event.getDispatcher());
		StatCommand.register(event.getDispatcher());
	}
}
