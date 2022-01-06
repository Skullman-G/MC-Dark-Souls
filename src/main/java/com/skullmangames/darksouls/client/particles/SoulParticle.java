package com.skullmangames.darksouls.client.particles;

import com.skullmangames.darksouls.core.util.math.MathUtils;

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
public class SoulParticle extends SpriteTexturedParticle
{
	protected SoulParticle(ClientWorld level, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed)
	{
		super(level, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
	    this.quadSize = 0.05F;
	}
	
	@Override
	public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_)
	{
	    this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
	    this.setLocationFromBoundingbox();
	}
	
	@Override
	public float getQuadSize(float partialTick)
	{
	    float per = ((float)this.age + partialTick) / (float)this.lifetime;
	    return MathUtils.clamp(this.quadSize / per, 0.05F, 0.25F);
	 }
	
	@Override
	public int getLightColor(float p_189214_1_)
	{
	    return super.getLightColor(p_189214_1_);
	}
	
	@Override
	public void tick()
	{
	    this.xo = this.x;
	    this.yo = this.y;
	    this.zo = this.z;
	    if (this.age++ >= this.lifetime)
	    {
	       this.remove();
	    }
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
	    	SoulParticle soulparticle = new SoulParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
	    	soulparticle.pickSprite(this.sprite);
	         return soulparticle;
	    }
	}
}
