package com.skullmangames.darksouls.common.animation.types.attack;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.RegistryObject;

public abstract class ParticleSpawner
{
	protected final RegistryObject<BasicParticleType> particle;
	protected final int contactLevel;
	
	public ParticleSpawner(RegistryObject<BasicParticleType> particle, int contactLevel)
	{
		this.particle = particle;
		this.contactLevel = contactLevel;
	}
	
	public abstract void spawnParticles(ClientWorld world, Vector3d pos);
}
