package com.skullmangames.darksouls.core.init;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.particles.BloodParticle;
import com.skullmangames.darksouls.client.particles.DustCloudParticle;
import com.skullmangames.darksouls.client.particles.EntityboundParticleOptions;
import com.skullmangames.darksouls.client.particles.FireParticle;
import com.skullmangames.darksouls.client.particles.ForceParticle;
import com.skullmangames.darksouls.client.particles.HumanityParticle;
import com.skullmangames.darksouls.client.particles.LightningParticle;
import com.skullmangames.darksouls.client.particles.LightningSpearParticle;
import com.skullmangames.darksouls.client.particles.MiracleCircleParticle;
import com.skullmangames.darksouls.client.particles.MiracleGlowParticle;
import com.skullmangames.darksouls.client.particles.SoulParticle;
import com.skullmangames.darksouls.client.particles.SparkParticle;
import com.skullmangames.darksouls.client.particles.VaseShardParticle;
import com.skullmangames.darksouls.client.particles.WoodSplinterParticle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ModParticles
{
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DarkSouls.MOD_ID);
	
	public static final RegistryObject<SimpleParticleType> DUST_CLOUD = register("dust_cloud", true);
	public static final RegistryObject<SimpleParticleType> SOUL = register("soul", true);
	public static final RegistryObject<SimpleParticleType> HUMANITY = register("humanity", true);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> MIRACLE_GLOW = register("miracle_glow", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> FAST_MIRACLE_GLOW = register("fast_miracle_glow", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<SimpleParticleType> TINY_MIRACLE_CIRCLE = register("tiny_miracle_circle", true);
	public static final RegistryObject<SimpleParticleType> MEDIUM_MIRACLE_CIRCLE = register("medium_miracle_circle", true);
	public static final RegistryObject<SimpleParticleType> LARGE_MIRACLE_CIRCLE = register("large_miracle_circle", true);
	public static final RegistryObject<SimpleParticleType> FORCE = register("force", true);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> LIGHTNING_SPEAR = register("lightning_spear", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<ParticleType<EntityboundParticleOptions>> GREAT_LIGHTNING_SPEAR = register("great_lightning_spear", EntityboundParticleOptions.DESERIALIZER, EntityboundParticleOptions::codec);
	public static final RegistryObject<SimpleParticleType> LIGHTNING = register("lightning", true);
	public static final RegistryObject<SimpleParticleType> BLOOD = register("blood", true);
	public static final RegistryObject<SimpleParticleType> SPARK = register("spark", true);
	public static final RegistryObject<SimpleParticleType> VASE_SHARD = register("vase_shard", true);
	public static final RegistryObject<SimpleParticleType> WOOD_SPLINTER = register("wood_splinter", true);
	public static final RegistryObject<SimpleParticleType> FIRE = register("fire", true);
	
	private static RegistryObject<SimpleParticleType> register(String name, boolean overrideLimiter)
	{
		return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
	}
	
	private static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name,
			@SuppressWarnings("deprecation") ParticleOptions.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec)
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
		ParticleEngine engine = minecraft.particleEngine;
		engine.register(DUST_CLOUD.get(), DustCloudParticle.Factory::new);
		engine.register(SOUL.get(), SoulParticle.Factory::new);
		engine.register(HUMANITY.get(), HumanityParticle.Factory::new);
		engine.register(MIRACLE_GLOW.get(), MiracleGlowParticle::normal);
		engine.register(FAST_MIRACLE_GLOW.get(), MiracleGlowParticle::fast);
		engine.register(TINY_MIRACLE_CIRCLE.get(), MiracleCircleParticle::tiny);
		engine.register(MEDIUM_MIRACLE_CIRCLE.get(), MiracleCircleParticle::medium);
		engine.register(LARGE_MIRACLE_CIRCLE.get(), MiracleCircleParticle::large);
		engine.register(FORCE.get(), ForceParticle.Factory::new);
		engine.register(LIGHTNING_SPEAR.get(), LightningSpearParticle::lightningSpear);
		engine.register(GREAT_LIGHTNING_SPEAR.get(), LightningSpearParticle::greatLightningSpear);
		engine.register(LIGHTNING.get(), LightningParticle.Factory::new);
		engine.register(BLOOD.get(), BloodParticle.Factory::new);
		engine.register(SPARK.get(), SparkParticle.Factory::new);
		engine.register(VASE_SHARD.get(), VaseShardParticle.Factory::new);
		engine.register(WOOD_SPLINTER.get(), WoodSplinterParticle.Factory::new);
		engine.register(FIRE.get(), FireParticle.Factory::new);
	}
}