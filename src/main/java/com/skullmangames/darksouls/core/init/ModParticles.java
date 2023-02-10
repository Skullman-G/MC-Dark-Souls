package com.skullmangames.darksouls.core.init;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.particles.BloodParticle;
import com.skullmangames.darksouls.client.particles.DustCloudParticle;
import com.skullmangames.darksouls.client.particles.EntityboundParticleOptions;
import com.skullmangames.darksouls.client.particles.ForceParticle;
import com.skullmangames.darksouls.client.particles.HumanityParticle;
import com.skullmangames.darksouls.client.particles.LightningParticle;
import com.skullmangames.darksouls.client.particles.LightningSpearParticle;
import com.skullmangames.darksouls.client.particles.MiracleCircleParticle;
import com.skullmangames.darksouls.client.particles.MiracleGlowParticle;
import com.skullmangames.darksouls.client.particles.SoulParticle;
import com.skullmangames.darksouls.client.particles.SparkParticle;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
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
	
	public static final RegistryObject<BasicParticleType> DUST_CLOUD = register("dust_cloud", true);
	public static final RegistryObject<BasicParticleType> SOUL = register("soul", true);
	public static final RegistryObject<BasicParticleType> HUMANITY = register("humanity", true);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> MIRACLE_GLOW = register("miracle_glow", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> FAST_MIRACLE_GLOW = register("fast_miracle_glow", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<BasicParticleType> TINY_MIRACLE_CIRCLE = register("tiny_miracle_circle", true);
	public static final RegistryObject<BasicParticleType> MEDIUM_MIRACLE_CIRCLE = register("medium_miracle_circle", true);
	public static final RegistryObject<BasicParticleType> LARGE_MIRACLE_CIRCLE = register("large_miracle_circle", true);
	public static final RegistryObject<BasicParticleType> FORCE = register("force", true);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> LIGHTNING_SPEAR = register("lightning_spear", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> GREAT_LIGHTNING_SPEAR = register("great_lightning_spear", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<BasicParticleType> LIGHTNING = register("lightning", true);
	public static final RegistryObject<BasicParticleType> BLOOD = register("blood", true);
	public static final RegistryObject<BasicParticleType> SPARK = register("spark", true);
	
	private static RegistryObject<BasicParticleType> register(String name, boolean overrideLimiter)
	{
		return PARTICLES.register(name, () -> new BasicParticleType(overrideLimiter));
	}
	
	private static <T extends IParticleData> RegistryObject<ParticleType<T>> register(String name,
			@SuppressWarnings("deprecation") IParticleData.IDeserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec)
	{
		ParticleType<T> type = new ParticleType<T>(false, deserializer)
		{
			public Codec<T> codec()
			{
				return codec.apply(this);
			}
		};
		return PARTICLES.register(name, () -> type);
	}
	
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.particleEngine.register(DUST_CLOUD.get(), DustCloudParticle.Factory::new);
		minecraft.particleEngine.register(SOUL.get(), SoulParticle.Factory::new);
		minecraft.particleEngine.register(HUMANITY.get(), HumanityParticle.Factory::new);
		minecraft.particleEngine.register(MIRACLE_GLOW.get(), MiracleGlowParticle::normal);
		minecraft.particleEngine.register(FAST_MIRACLE_GLOW.get(), MiracleGlowParticle::fast);
		minecraft.particleEngine.register(TINY_MIRACLE_CIRCLE.get(), MiracleCircleParticle::tiny);
		minecraft.particleEngine.register(MEDIUM_MIRACLE_CIRCLE.get(), MiracleCircleParticle::medium);
		minecraft.particleEngine.register(LARGE_MIRACLE_CIRCLE.get(), MiracleCircleParticle::large);
		minecraft.particleEngine.register(FORCE.get(), ForceParticle.Factory::new);
		minecraft.particleEngine.register(LIGHTNING_SPEAR.get(), LightningSpearParticle::lightningSpear);
		minecraft.particleEngine.register(GREAT_LIGHTNING_SPEAR.get(), LightningSpearParticle::greatLightningSpear);
		minecraft.particleEngine.register(LIGHTNING.get(), LightningParticle.Factory::new);
		minecraft.particleEngine.register(BLOOD.get(), BloodParticle.Factory::new);
		minecraft.particleEngine.register(SPARK.get(), SparkParticle.Factory::new);
	}
}