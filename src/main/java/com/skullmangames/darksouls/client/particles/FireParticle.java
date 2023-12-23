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
public class FireParticle extends TextureSheetParticle
{
	private final SpriteSet sprites;
	
	private FireParticle(ClientLevel level, SpriteSet sprites, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(level, posX, posY, posZ, speedX, speedY, speedZ);

		this.sprites = sprites;
		this.xd = speedX;
		this.yd = 0.01F;
		this.zd = speedZ;
		this.lifetime = 50;
		this.quadSize = 1.0F;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (this.age % 2 == 0) this.pickSprite(this.sprites);
		
		if (this.age > 30)
		{
			this.alpha = Math.max(0, this.alpha - 0.25F);
		}
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
		public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			FireParticle particle = new FireParticle(level, this.sprite, posX, posY, posZ, speedX, speedY, speedZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
