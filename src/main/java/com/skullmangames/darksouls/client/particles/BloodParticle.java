package com.skullmangames.darksouls.client.particles;

import com.skullmangames.darksouls.core.util.math.MathUtils;

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
public class BloodParticle extends TextureSheetParticle
{
	private BloodParticle(ClientLevel level, double posX, double posY, double posZ, double speedX, double speedY, double speedZ)
	{
		super(level, posX, posY, posZ, speedX, speedY, speedZ);

		this.xd = speedX;
		this.yd = speedY;
		this.zd = speedZ;
		this.rCol = (float)MathUtils.clamp(level.random.nextDouble(), 0.8D, 0.9D);
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
			BloodParticle particle = new BloodParticle(level, posX, posY, posZ, speedX, speedY, speedZ);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
