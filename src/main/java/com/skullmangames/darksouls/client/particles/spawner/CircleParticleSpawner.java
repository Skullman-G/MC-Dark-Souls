package com.skullmangames.darksouls.client.particles.spawner;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class CircleParticleSpawner extends ParticleSpawner
{
	protected final float radius;
	
	public CircleParticleSpawner(RegistryObject<SimpleParticleType> particle, int contactLevel, float distance)
	{
		super(particle, contactLevel);
		this.radius = distance;
	}

	@Override
	public void spawnParticles(ClientLevel world, Vec3 pos)
	{
		SimpleParticleType p = this.particle.get();
		
		for (int i = 0; i < 360; i++)
		{
			if (i % 40 == 0)
			{
				double a = Math.toRadians(i);
				world.addParticle(p, pos.x, pos.y, pos.z, Math.sin(a) * this.radius, 0, Math.cos(a) * this.radius);
			}
		}
	}
}
