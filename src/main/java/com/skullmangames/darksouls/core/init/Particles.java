package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Particles
{
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DarkSouls.MOD_ID);
	
	/*public static final RegistryObject<HitParticleType> BLOOD = PARTICLES.register("blood", ()->new HitParticleType(true));
	public static final RegistryObject<HitParticleType> CUT = PARTICLES.register("cut", ()->new HitParticleType(true));
	public static final RegistryObject<HitParticleType> DUST = PARTICLES.register("shine_dust", ()->new HitParticleType(true, HitParticleType.DIRECTIONAL));
	public static final RegistryObject<HitParticleType> FLASH = PARTICLES.register("flash", ()->new HitParticleType(true));
	public static final RegistryObject<HitParticleType> PORTAL_STRAIGHT = PARTICLES.register("portal_straight", ()->new HitParticleType(true));
	public static final RegistryObject<HitParticleType> HIT_BLUNT = PARTICLES.register("blunt", ()->new HitParticleType(true));
	public static final RegistryObject<HitParticleType> PARRY = PARTICLES.register("parry", ()->new HitParticleType(true));*/
}