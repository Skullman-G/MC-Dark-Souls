package com.skullmangames.darksouls.common.entity.projectile;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damage;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightningSpear extends Projectile
{
	private int particle;
	private final float damage;
	
	private LightningSpear(EntityType<? extends LightningSpear> type, Level level, float baseDamage)
	{
		super(type, level);
		this.damage = baseDamage;
	}
	
	private LightningSpear(EntityType<? extends LightningSpear> type, LivingCap<?> entityCap, float baseDamage)
	{
		super(type, entityCap.getLevel());
		this.setOwner(entityCap.getOriginalEntity());
		
		Joint joint = entityCap.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName("Tool_R");
		
		PublicMatrix4f rotationTransform = entityCap.getModelMatrix(1.0F);
		PublicMatrix4f localTransform = joint.getAnimatedTransform();
		localTransform.mulFront(rotationTransform);
		Vector3f jpos = localTransform.toTranslationVector();
		jpos.mul(-1, 1, -1);
		
		this.setPos(jpos.x(), jpos.y(), jpos.z());
		this.damage = baseDamage + entityCap.getDamageScalingMultiplier(baseDamage);
	}
	
	public static LightningSpear lightningSpear(EntityType<? extends LightningSpear> type, Level level)
	{
		return new LightningSpear(type, level, 15.0F);
	}
	
	public static LightningSpear lightningSpear(LivingCap<?> entityCap)
	{
		return new LightningSpear(ModEntities.LIGHTNING_SPEAR.get(), entityCap, 15.0F);
	}
	
	public static LightningSpear greatLightningSpear(EntityType<? extends LightningSpear> type, Level level)
	{
		return new LightningSpear(type, level, 20.0F);
	}
	
	public static LightningSpear greatLightningSpear(LivingCap<?> entityCap)
	{
		return new LightningSpear(ModEntities.GREAT_LIGHTNING_SPEAR.get(), entityCap, 20.0F);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.particle = this.random.nextInt(6);
		
		Vec3 vec3 = this.getDeltaMovement();
		Level level = this.level;
		Vec3 vec31 = this.position();
		Vec3 vec32 = vec31.add(vec3);
		HitResult hitresult = level.clip(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, this));
		if (hitresult.getType() != HitResult.Type.MISS)
		{
			vec32 = hitresult.getLocation();
			if (this.isInWater()) vec3.add(5, 5, 5);
			else vec3.add(1, 1, 1);
		}

		HitResult hitresult1 = ProjectileUtil.getEntityHitResult(level, this, vec31, vec32, this.getBoundingBox().expandTowards(vec3).inflate(1.0D), this::canHitEntity);
		if (hitresult1 != null)
		{
			hitresult = hitresult1;
		}
		if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
		{
			this.onHit(hitresult);
		}
		
        this.setPos(this.position().add(this.getDeltaMovement()));
        ProjectileUtil.rotateTowardsMovement(this, 1.0F);
	}
	
	@Override
	protected boolean canHitEntity(Entity entity)
	{
		return super.canHitEntity(entity) && !entity.noPhysics;
	}
	
	@Override
	protected void onHit(HitResult hitresult)
	{
		super.onHit(hitresult);
		if (this.level.isClientSide)
		{
			Vec3 pos = this.position();
			for (int i = 0; i < 360; i++)
			{
				if (i % 20 == 0)
				{
					this.level.addAlwaysVisibleParticle(ModParticles.LIGHTNING.get(), pos.x, pos.y, pos.z, Math.cos(i) * 0.1D, Math.sin(i) * 0.1D, Math.sin(i) * 0.1D);
				}
			}
		}
		else
		{
			this.level.playSound(null, this.blockPosition(), ModSoundEvents.LIGHTNING_SPEAR_IMPACT.get(), this.getSoundSource(), 1.0F, 1.0F);
		}
		this.remove(RemovalReason.DISCARDED);
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		super.onHitEntity(result);
		result.getEntity().hurt(ExtendedDamageSource.causeProjectileDamage(this, this.getOwner(), StunType.DEFAULT, 1.0F, 1.0F, new Damage(DamageType.LIGHTNING, this.damage)), this.damage);
	}
	
	public int getParticle()
	{
		return this.particle;
	}

	@Override
	protected void defineSynchedData() {}
}
