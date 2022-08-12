package com.skullmangames.darksouls.common.entity;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class LightningSpear extends Projectile
{
	private LivingCap<?> entityCap;
	private Joint joint;
	private int particle;
	private boolean anchored;
	
	public LightningSpear(EntityType<? extends LightningSpear> type, Level level)
	{
		super(type, level);
	}
	
	public LightningSpear(LivingCap<?> entityCap, boolean anchored)
	{
		super(ModEntities.LIGHTNING_SPEAR.get(), entityCap.getLevel());
		this.entityCap = entityCap;
		this.setOwner(this.entityCap.getOriginalEntity());
		this.anchored = anchored;
		
		if (this.anchored)
		{
			this.joint = this.entityCap.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName("Tool_R");
			
			PublicMatrix4f rotationTransform = this.entityCap.getModelMatrix(1.0F);
			PublicMatrix4f localTransform = this.joint.getAnimatedTransform();
			localTransform.mulFront(rotationTransform);
			Vector3f jpos = localTransform.toTranslationVector();
			jpos.mul(-1, 1, -1);
			
			this.setPos(jpos.x(), jpos.y(), jpos.z());
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.particle = this.random.nextInt(6);
		
		if (this.joint != null && this.anchored)
		{
			PublicMatrix4f rotationTransform = this.entityCap.getModelMatrix(1.0F);
			PublicMatrix4f localTransform = this.joint.getAnimatedTransform();
			localTransform.mulFront(rotationTransform);
			Vector3f jpos = localTransform.toTranslationVector();
			jpos.mul(-1, 1, -1);
			
			this.setPos(jpos.x(), jpos.y(), jpos.z());
		}
	}
	
	public int getParticle()
	{
		return this.particle;
	}

	@Override
	protected void defineSynchedData() {}
}
