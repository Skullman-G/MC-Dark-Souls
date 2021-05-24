package com.skullmangames.darksouls.client.event;

import com.skullmangames.darksouls.DarkSouls;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

}
