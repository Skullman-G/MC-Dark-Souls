package com.skullmangames.darksouls.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DustCloudParticle extends SpriteTexturedParticle
{
	public DustCloudParticle(ClientWorld world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(world, posX, posY, posZ, speedX, speedY, speedZ);

		this.scale(10.0F);

		this.xd = speedX;
		this.yd = speedY;
		this.zd = speedZ;
	}

	@Override
	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType>
	{
		private final IAnimatedSprite sprite;

		public Factory(IAnimatedSprite sprite)
		{
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(BasicParticleType particle, ClientWorld world, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			DustCloudParticle dustCloud = new DustCloudParticle(world, posX, posY, posZ, speedX, speedY, speedZ);
			dustCloud.pickSprite(this.sprite);
			return dustCloud;
		}
	}
}
