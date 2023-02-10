package com.skullmangames.darksouls.client.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ForceParticle extends SpriteTexturedParticle
{
	public ForceParticle(ClientWorld world, double posX, double posY, double posZ)
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
		public Particle createParticle(BasicParticleType type, ClientWorld world, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			ForceParticle particle = new ForceParticle(world, posX, posY, posZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
