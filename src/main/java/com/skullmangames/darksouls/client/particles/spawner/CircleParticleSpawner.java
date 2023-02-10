package com.skullmangames.darksouls.client.particles.spawner;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.RegistryObject;

public class CircleParticleSpawner extends ParticleSpawner
{
	protected final float radius;
	
	public CircleParticleSpawner(RegistryObject<BasicParticleType> particle, int contactLevel, float distance)
	{
		super(particle, contactLevel);
		this.radius = distance;
	}

	@Override
	public void spawnParticles(ClientWorld world, Vector3d pos)
	{
		BasicParticleType p = this.particle.get();
		
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
