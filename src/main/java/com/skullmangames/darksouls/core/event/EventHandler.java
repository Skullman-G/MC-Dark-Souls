package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE)
public class EventHandler
{
	@SubscribeEvent
	public static void onItemUseStart(final LivingEntityUseItemEvent event)
    {
    }
}
