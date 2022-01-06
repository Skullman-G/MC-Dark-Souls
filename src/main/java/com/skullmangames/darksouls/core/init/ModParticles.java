package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.particles.DustCloudParticle;
import com.skullmangames.darksouls.client.particles.SoulParticle;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ModParticles
{
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DarkSouls.MOD_ID);
	
	public static final RegistryObject<BasicParticleType> DUST_CLOUD = PARTICLES.register("dust_cloud", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> SOUL = PARTICLES.register("soul", () -> new BasicParticleType(true));
	
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.particleEngine.register(DUST_CLOUD.get(), DustCloudParticle.Factory::new);
		minecraft.particleEngine.register(SOUL.get(), SoulParticle.Factory::new);
	}
}