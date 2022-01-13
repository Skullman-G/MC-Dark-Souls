package com.skullmangames.darksouls.common.animation.types.attack;

import com.mojang.math.Vector3d;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
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
	
	public abstract void spawnParticles(ClientLevel world, Vector3d pos);
}
