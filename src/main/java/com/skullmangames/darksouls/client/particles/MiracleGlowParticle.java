package com.skullmangames.darksouls.client.particles;

import javax.annotation.Nullable;

import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MiracleGlowParticle extends EntityboundParticle
{
	private IAnimatedSprite sprites;
	private int lastSprite;
	@Nullable private LivingCap<?> entityCap;
	@Nullable private Joint joint;
	
	protected MiracleGlowParticle(ClientWorld level, int entityId, int lifetime, IAnimatedSprite sprites, double xCoord, double yCoord, double zCoord)
	{
		super(level, entityId, xCoord, yCoord, zCoord, 0, 0, 0);
		this.quadSize = 0.3F;
		this.sprites = sprites;
	    this.lifetime = lifetime;
	    this.xd = 0;
	    this.yd = 0;
	    this.zd = 0;
	    this.alpha = 0;
	    
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
		
		if (this.age < this.lifetime * 0.2F)
		{
			this.quadSize += 0.2F / (this.lifetime * 0.2F);
			this.alpha += 1.0F / (this.lifetime * 0.2F);
		}
		else if (this.age > this.lifetime * 0.8F)
		{
			this.quadSize -= 0.2F / (this.lifetime * 0.2F);
			this.alpha = Math.max(0, this.alpha - 1.0F / (this.lifetime * 0.2F));
		}
		
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
	public IParticleRenderType getRenderType()
	{
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public static Factory normal(IAnimatedSprite sprite)
	{
		return new Factory(75, sprite);
	}
	
	public static Factory fast(IAnimatedSprite sprite)
	{
		return new Factory(40, sprite);
	}
	
	public static class Factory implements IParticleFactory<EntityboundParticleOptions>
	{
	    private final IAnimatedSprite sprite;
	    private final int lifetime;

	    public Factory(int lifetime, IAnimatedSprite sprite)
	    {
	    	this.sprite = sprite;
	    	this.lifetime = lifetime;
	    }

	    @Override
	    public Particle createParticle(EntityboundParticleOptions options, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
	    {
	    	MiracleGlowParticle particle = new MiracleGlowParticle(level, options.getEntityId(), this.lifetime, this.sprite, x, y, z);
	    	particle.pickSprite(this.sprite);
	        return particle;
	    }
	}
}
