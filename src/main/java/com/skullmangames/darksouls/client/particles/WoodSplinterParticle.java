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
public class WoodSplinterParticle extends TextureSheetParticle
{
	private WoodSplinterParticle(ClientLevel level, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(level, posX, posY, posZ, speedX, speedY, speedZ);

		this.xd = speedX;
		this.yd = speedY;
		this.zd = speedZ;
		this.lifetime = 40;
	}

	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_LIT;
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
		public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			WoodSplinterParticle particle = new WoodSplinterParticle(level, posX, posY, posZ, speedX, speedY, speedZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
