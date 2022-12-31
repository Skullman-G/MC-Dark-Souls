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
public class ForceParticle extends TextureSheetParticle
{
	public ForceParticle(ClientLevel world, double posX, double posY, double posZ)
	{
		super(world, posX, posY, posZ, 0, 0, 0);
		this.quadSize = 0;
		this.xd = 0;
		this.yd = 0;
		this.zd = 0;
		this.lifetime = 10;
		this.alpha = 0;
	}
	
	@Override
	public void tick()
	{
		this.quadSize += 4.0F / this.lifetime;
		if (this.age < this.lifetime * 0.7F)
		{
			this.alpha += 1.0F / (this.lifetime * 0.7F);
		}
		else
		{
			this.alpha = Math.max(0, this.alpha - 1.0F / (this.lifetime * 0.3F));
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
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			ForceParticle particle = new ForceParticle(world, posX, posY, posZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
