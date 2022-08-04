package com.skullmangames.darksouls.client.particles;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MiracleGlowParticle extends EntityboundParticle
{
	private SpriteSet sprites;
	private int lastSprite;
	@Nullable private LivingCap<?> entityCap;
	@Nullable private Joint joint;
	
	protected MiracleGlowParticle(ClientLevel level, int entityId, SpriteSet sprites, double xCoord, double yCoord, double zCoord)
	{
		super(level, entityId, xCoord, yCoord, zCoord, 0, 0, 0);
		this.quadSize = 0.5F;
		this.sprites = sprites;
	    this.lifetime = 75;
	    this.xd = 0;
	    this.yd = 0;
	    this.zd = 0;
	    
	    if (this.entity instanceof LivingEntity)
	    {
	    	LivingCap<?> cap = (LivingCap<?>)this.entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
	    	if (cap != null)
	    	{
	    		this.entityCap = cap;
	    		this.joint = cap.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName("Tool_R");
	    		
	    		PublicMatrix4f rotationTransform = this.entityCap.getModelMatrix(1.0F);
	    		PublicMatrix4f localTransform = this.joint.getAnimatedTransform();
	    		localTransform.mulFront(rotationTransform);
	    		Vector3f jpos = localTransform.toTranslationVector();
	    		jpos.mul(-1, 1, -1);
	    		
	    		this.setPos(jpos.x(), jpos.y(), jpos.z());
	    	}
	    }
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.setSprite(this.sprites.get(this.lastSprite, 4));
		this.lastSprite = this.lastSprite > 4 ? 0 : this.lastSprite + 1;
		
		if (this.joint != null)
		{
			PublicMatrix4f rotationTransform = this.entityCap.getModelMatrix(1.0F);
			PublicMatrix4f localTransform = this.joint.getAnimatedTransform();
			localTransform.mulFront(rotationTransform);
			Vector3f jpos = localTransform.toTranslationVector();
			jpos.mul(-1, 1, -1);
			
			this.setPos(jpos.x(), jpos.y(), jpos.z());
		}
	}
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<EntityboundParticleOptions>
	{
	    private final SpriteSet sprite;

	    public Factory(SpriteSet sprite)
	    {
	    	this.sprite = sprite;
	    }

	    @Override
	    public Particle createParticle(EntityboundParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	MiracleGlowParticle particle = new MiracleGlowParticle(level, options.getEntityId(), this.sprite, x, y, z);
	    	particle.pickSprite(this.sprite);
	        return particle;
	    }
	}
}
