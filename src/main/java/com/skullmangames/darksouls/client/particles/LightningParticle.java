package com.skullmangames.darksouls.client.particles;

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
public class LightningParticle extends SpriteTexturedParticle
{
	private final IAnimatedSprite sprites;
	
	protected LightningParticle(ClientWorld level, IAnimatedSprite sprites, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed)
	{
		super(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
		this.lifetime = 10;
		this.quadSize = 0.2F;
		this.roll = (float)Math.random();
		this.sprites = sprites;
	}
	
	@Override
	public void tick()
	{
	    super.tick();
	    this.pickSprite(this.sprites);
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
	    public Particle createParticle(BasicParticleType type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	LightningParticle particle = new LightningParticle(level, this.sprite, x, y, z, xSpeed, ySpeed, zSpeed);
	    	particle.pickSprite(this.sprite);
	         return particle;
	    }
	}
}
