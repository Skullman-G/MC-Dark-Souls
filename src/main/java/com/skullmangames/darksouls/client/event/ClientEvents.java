package com.skullmangames.darksouls.client.event;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents
{
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event)
	{
		
	}
}
