package com.skullmangames.darksouls.common.animation.types.attack;

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
		float incr = this.radius * 0.5F;
		
		for (double x = this.radius, z = 0D; x >= 0D && z <= this.radius; x -= incr, z += incr)
		{
			world.addParticle(p, pos.x, pos.y, pos.z, x, 0D, z);
		}
		for (double x = 0D - incr, z = this.radius - incr; x >= -this.radius && z >= 0D; x -= incr, z -= incr)
		{
			world.addParticle(p, pos.x, pos.y, pos.z, x, 0D, z);
		}
		for (double x = -this.radius + incr, z = 0D - incr; x <= 0D && z >= -this.radius; x += incr, z -= incr)
		{
			world.addParticle(p, pos.x, pos.y, pos.z, x, 0D, z);
		}
		for (double x = 0D + incr, z = -this.radius + incr; x <= this.radius && z <= 0D; x += incr, z += incr)
		{
			world.addParticle(p, pos.x, pos.y, pos.z, x, 0D, z);
		}
	}
}
