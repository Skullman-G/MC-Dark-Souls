package com.skullmangames.darksouls.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MiracleCircleParticle extends TextureSheetParticle
{
	private final float maxSize;
	
	protected MiracleCircleParticle(ClientLevel level, float maxSize, double xCoord, double yCoord, double zCoord)
	{
		super(level, xCoord, yCoord, zCoord, 0, 0, 0);
		this.quadSize = 0;
	    this.lifetime = 30;
	    this.xd = 0;
	    this.yd = 0;
	    this.zd = 0;
	    this.alpha = 0;
	    this.maxSize = maxSize;
	}
	
	@Override
	public void tick()
	{
		this.quadSize += this.maxSize / this.lifetime;
		if (this.age < this.lifetime * 0.7F)
		{
			this.alpha += 1.0F / (this.lifetime * 0.7F);
		}
		else
		{
			this.alpha -= 1.0F / (this.lifetime * 0.3F);
		}
		super.tick();
	}
	
	@Override
	public void render(VertexConsumer vertexBuilder, Camera camera, float partialTicks)
	{
		Vec3 vec3 = camera.getPosition();
		float f = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - vec3.x());
		float f1 = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - vec3.y());
		float f2 = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - vec3.z());
		
		Vector3f[] avector3f = new Vector3f[]
		{
				new Vector3f(-1.0F, -1.0F, 0.0F),
				new Vector3f(-1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, -1.0F, 0.0F)
		};
		float f4 = this.getQuadSize(partialTicks);

		for (int i = 0; i < 4; ++i)
		{
			Vector3f vector3f = avector3f[i];
			vector3f.transform(Vector3f.XP.rotationDegrees(90));
			vector3f.mul(f4);
			vector3f.add(f, f1, f2);
		}

		float f7 = this.getU0();
		float f8 = this.getU1();
		float f5 = this.getV0();
		float f6 = this.getV1();
		int j = this.getLightColor(partialTicks);
		
		vertexBuilder.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		vertexBuilder.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		vertexBuilder.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		vertexBuilder.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
	}
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public static Factory tiny(SpriteSet sprite)
	{
		return new Factory(sprite, 1.0F);
	}
	
	public static Factory medium(SpriteSet sprite)
	{
		return new Factory(sprite, 2.0F);
	}
	
	public static Factory large(SpriteSet sprite)
	{
		return new Factory(sprite, 3.0F);
	}
	
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
	    private final SpriteSet sprite;
	    private final float maxSize;

	    public Factory(SpriteSet sprite, float maxSize)
	    {
	    	this.sprite = sprite;
	    	this.maxSize = maxSize;
	    }

	    @Override
	    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	MiracleCircleParticle particle = new MiracleCircleParticle(level, this.maxSize, x, y, z);
	    	particle.pickSprite(this.sprite);
	        return particle;
	    }
	}
}
