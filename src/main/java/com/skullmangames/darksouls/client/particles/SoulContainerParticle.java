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
public class SoulContainerParticle extends SpriteTexturedParticle
{
	private final double xStart;
	private final double yStart;
	private final double zStart;
	
	protected SoulContainerParticle(ClientWorld world, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed)
	{
		super(world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
		
		this.xd = xSpeed;
	    this.yd = ySpeed;
	    this.zd = zSpeed;
	    this.x = xCoord;
	    this.y = yCoord;
	    this.z = zCoord;
	    this.xStart = this.x;
	    this.yStart = this.y;
	    this.zStart = this.z;
	    this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
	    float f = this.random.nextFloat() * 0.01F;
	    this.rCol = 1.0F;
	    this.gCol = 1.0F;
	    this.bCol = 0.9F + f;
	    this.lifetime = (int)(Math.random() * 10.0D) + 40;
	}
	
	public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_)
	{
	    this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
	    this.setLocationFromBoundingbox();
	}
	
	public float getQuadSize(float p_217561_1_)
	{
	    float f = ((float)this.age + p_217561_1_) / (float)this.lifetime;
	    f = 1.0F - f;
	    f = f * f;
	    f = 1.0F - f;
	    return this.quadSize * f;
	 }
	
	public int getLightColor(float p_189214_1_)
	{
	    return super.getLightColor(p_189214_1_);
	}
	
	public void tick()
	{
	    this.xo = this.x;
	    this.yo = this.y;
	    this.zo = this.z;
	    if (this.age++ >= this.lifetime)
	    {
	       this.remove();
	    }
	    else
	    {
	       float f = (float)this.age / (float)this.lifetime;
	       this.x = this.xStart + this.xd * (double)f * 2.0D;
	       this.y = this.yStart + 0.5D + this.yd * (double)f;
	       this.z = this.zStart + this.zd * (double)f * 2.0D;
	    }
	 }

	@Override
	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
	    public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	SoulContainerParticle soulparticle = new SoulContainerParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
	    	soulparticle.pickSprite(this.sprite);
	         return soulparticle;
	    }
	}
}
