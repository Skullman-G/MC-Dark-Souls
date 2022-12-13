package com.skullmangames.darksouls.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SparkParticle extends TextureSheetParticle
{
	public SparkParticle(ClientLevel world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(world, posX, posY, posZ, speedX, speedY, speedZ);

		this.xd = speedX;
		this.yd = speedY;
		this.zd = speedZ;
		this.lifetime = 5;
		this.quadSize = 0.05F;
	}
	
	@Override
	public void tick()
	{
		this.roll = 45;
		super.tick();
	}

	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet sprite;

		public Factory(SpriteSet sprite)
		{
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType particleType, ClientLevel world, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			SparkParticle particle = new SparkParticle(world, posX, posY, posZ, speedX, speedY, speedZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
