package com.skullmangames.darksouls.common.animation.types.attack;

import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.fml.RegistryObject;

public class ParticleSpawner
{
	protected final RegistryObject<BasicParticleType> particle;
	protected final int contactLevel;
	
	public ParticleSpawner(RegistryObject<BasicParticleType> particle, int contactLevel)
	{
		this.particle = particle;
		this.contactLevel = contactLevel;
	}
}
