package com.skullmangames.darksouls.client.particles.spawner;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public abstract class ParticleSpawner
{
	protected final RegistryObject<SimpleParticleType> particle;
	protected final int contactLevel;
	
	public ParticleSpawner(RegistryObject<SimpleParticleType> particle, int contactLevel)
	{
		this.particle = particle;
		this.contactLevel = contactLevel;
	}
	
	public abstract void spawnParticles(ClientLevel world, Vec3 pos);
}
