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
public class DustCloudParticle extends TextureSheetParticle
{
	public DustCloudParticle(ClientLevel world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(world, posX, posY, posZ, speedX, speedY, speedZ);

		this.scale(10.0F);

		this.xd = speedX;
		this.yd = speedY;
		this.zd = speedZ;
	}

	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
		public Particle createParticle(SimpleParticleType particle, ClientLevel world, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			DustCloudParticle dustCloud = new DustCloudParticle(world, posX, posY, posZ, speedX, speedY, speedZ);
			dustCloud.pickSprite(this.sprite);
			return dustCloud;
		}
	}
}
