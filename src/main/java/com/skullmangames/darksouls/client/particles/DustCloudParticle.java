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
	private DustCloudParticle(ClientLevel level, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(level, posX, posY, posZ, speedX, speedY, speedZ);

		this.xd = speedX / 2;
		this.yd = speedY / 2;
		this.zd = speedZ / 2;
		this.lifetime = 40;
	}
	
	@Override
	public void tick()
	{
		if (this.age < 5)
		{
			this.quadSize += 0.2F;
		}
		if (this.age >= this.lifetime - 30 && this.alpha >= 0.03F)
		{
			this.alpha -= 0.03F;
        }
	    
		super.tick();
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
