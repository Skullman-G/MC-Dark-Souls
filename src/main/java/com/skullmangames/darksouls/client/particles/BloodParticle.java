package com.skullmangames.darksouls.client.particles;

import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodParticle extends SpriteTexturedParticle
{
	public BloodParticle(ClientWorld world, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(world, posX, posY, posZ, speedX, speedY, speedZ);

		this.xd = speedX;
		this.yd = speedY;
		this.zd = speedZ;
		this.rCol = (float)MathUtils.clamp(world.random.nextDouble(), 0.8D, 0.9D);
		this.lifetime = 5;
		this.quadSize = 0.05F;
	}
	
	@Override
	public void tick()
	{
		if (this.age < 5)
		{
			this.quadSize += 0.01F;
		}
		
		this.yd = Math.max(-1D, this.yd - 0.1D);
	    
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
		public Particle createParticle(BasicParticleType particleType, ClientWorld world, double posX, double posY, double posZ, double speedX,
				double speedY, double speedZ)
		{
			BloodParticle particle = new BloodParticle(world, posX, posY, posZ, speedX, speedY, speedZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
