package com.skullmangames.darksouls.common.entity.projectile;

import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.ProjectileUtil;
import com.skullmangames.darksouls.core.util.math.ModMath;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightningSpear extends MagicProjectile
{
	private int particle;
	private float damage;
	
	public LightningSpear(EntityType<? extends LightningSpear> type, Level level)
	{
		super(type, level);
		this.damage = 0F;
	}
	
	@Override
	public void initProjectile(LivingCap<?> cap)
	{
		this.setOwner(cap.getOriginalEntity());
		
		double yRot = Math.toRadians(ModMath.toNormalRot(cap.getYRot()));
		this.setPos(cap.getX() + Math.sin(yRot) * 0.5F, cap.getY() + 1.75F, cap.getZ() + Math.cos(yRot) * 1.75F);
		this.yRot = cap.getYRot();
		this.damage = 0F;
		if (this.getType() == ModEntities.LIGHTNING_SPEAR.get()) this.damage += 145F;
		else if (this.getType() == ModEntities.GREAT_LIGHTNING_SPEAR.get()) this.damage += 185;
		this.damage *= cap.getSpellBuff();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		this.particle = this.random.nextInt(6);
		
		Vec3 vec3 = this.getDeltaMovement();
		Vec3 vec31 = this.position();
		Vec3 vec32 = vec31.add(vec3);
		HitResult hitresult = this.level.clip(new ClipContext(vec31, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, this));
		if (hitresult.getType() != HitResult.Type.MISS)
		{
			vec32 = hitresult.getLocation();
			if (this.isInWater()) vec3.add(5, 5, 5);
			else vec3.add(1, 1, 1);
		}

		HitResult hitresult1 = ProjectileUtil.getEntityHitResult(this.level, this, vec31, vec32, this.getBoundingBox().expandTowards(vec3).inflate(1.0D), this::canHitEntity);
		if (hitresult1 != null)
		{
			hitresult = hitresult1;
		}
		if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
		{
			this.onHit(hitresult);
		}
		else
		{
			LightSource.setLightSource(this.level, this.blockPosition(), 15, 0.1F);
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
		if (!this.level.isClientSide)
		{
			this.level.playSound(null, this.blockPosition(), ModSoundEvents.LIGHTNING_SPEAR_IMPACT.get(), this.getSoundSource(), 1.0F, 1.0F);
			LightSource.setLightSource(this.level, this.blockPosition(), 15, 0.5F);
			this.level.broadcastEntityEvent(this, (byte)3);
			this.discard();
		}
	}
	
	@Override
	public void handleEntityEvent(byte event)
	{
		if (event == 3)
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
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		super.onHitEntity(result);
		result.getEntity().hurt(ExtendedDamageSource.causeProjectileDamage(this, this.getOwner(),
				StunType.LIGHT, 1.0F, 1.0F, Damages.create().put(CoreDamageType.LIGHTNING, this.damage)), this.damage);
	}
	
	public int getParticle()
	{
		return this.particle;
	}

	@Override
	protected void defineSynchedData() {}
}
