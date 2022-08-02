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
public class MiracleGlowParticle extends TextureSheetParticle
{
	private SpriteSet sprites;
	private int lastSprite;
	
	protected MiracleGlowParticle(ClientLevel level, SpriteSet sprites, double xCoord, double yCoord, double zCoord)
	{
		super(level, xCoord, yCoord, zCoord, 0, 0, 0);
		this.quadSize = 0.5F;
		this.sprites = sprites;
	    this.lifetime = 1000;
	    this.xd = 0;
	    this.yd = 0;
	    this.zd = 0;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.setSprite(this.sprites.get(this.lastSprite, 4));
		this.lastSprite = this.lastSprite > 4 ? 0 : this.lastSprite + 1;
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
	    	MiracleGlowParticle particle = new MiracleGlowParticle(level, this.sprite, x, y, z);
	    	particle.pickSprite(this.sprite);
	        return particle;
	    }
	}
}
