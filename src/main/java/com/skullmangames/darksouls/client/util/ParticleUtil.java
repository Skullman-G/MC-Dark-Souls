package com.skullmangames.darksouls.client.util;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.particles.SoulContainerParticle;
import com.skullmangames.darksouls.core.init.ParticleTypeInit;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.MOD)
public class ParticleUtil
{
	@SuppressWarnings("resource")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerParticles(ParticleFactoryRegisterEvent event)
	{
		Minecraft.getInstance().particleEngine.register(ParticleTypeInit.SOUL_CONTAINER.get(), SoulContainerParticle.Factory::new);
	}
}
