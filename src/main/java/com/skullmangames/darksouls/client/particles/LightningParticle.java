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
public class LightningParticle extends TextureSheetParticle
{
	private final SpriteSet sprites;
	
	private LightningParticle(ClientLevel level, SpriteSet sprites, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed)
	{
		super(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
		this.lifetime = 10;
		this.quadSize = 0.2F;
		this.roll = (float)Math.random();
		this.sprites = sprites;
		this.friction = 0.75F;
	}
	
	@Override
	public void tick()
	{
	    super.tick();
	    this.pickSprite(this.sprites);
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
	    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	LightningParticle particle = new LightningParticle(level, this.sprite, x, y, z, xSpeed, ySpeed, zSpeed);
	    	particle.pickSprite(this.sprite);
	        return particle;
	    }
	}
}
