package com.skullmangames.darksouls.client.event;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public final class ModClientEvents
{
	private ModClientEvents()
	{
		throw new IllegalAccessError("Attempted to construct utility class.");
	}
	
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event)
	{
		
	}
}
