package com.skullmangames.darksouls.common.entity.projectile;

import java.util.List;

import com.skullmangames.darksouls.common.block.LightSource;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class Firebomb extends ThrowableItemProjectile
{
	private final float damage;
	
	public Firebomb(EntityType<? extends Firebomb> entityType, Level level)
	{
		super(entityType, level);
		this.damage = 0F;
	}
	
	private Firebomb(EntityType<? extends Firebomb> entityType, Level level, LivingEntity entity, float damage)
	{
		super(entityType, entity, entity.level);
		this.damage = damage;
	}
	
	public static Firebomb firebomb(Level level, LivingEntity entity)
	{
		return new Firebomb(ModEntities.FIREBOMB.get(), level, entity, 100F);
	}
	
	public static Firebomb blackFirebomb(Level level, LivingEntity entity)
	{
		return new Firebomb(ModEntities.BLACK_FIREBOMB.get(), level, entity, 140F);
	}
	
	@Override
	protected Item getDefaultItem()
	{
		return ModItems.FIREBOMB.get();
	}
	
	@Override
	public void tick()
	{
		if (this.level.isClientSide)
		{
			this.level.addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + 0.5F, this.getZ(), 0.0D, 0.0D, 0.0D);
		}
		super.tick();
	}
	
	public void handleEntityEvent(byte event)
	{
		if (event == 3)
		{
			for (int i = 0; i < 4; ++i)
			{
				this.level.addParticle(ModParticles.FIRE.get(), this.getX() + this.random.nextDouble() * 0.5D, this.getY() + 0.5D,
						this.getZ() + this.random.nextDouble() * 0.5D, 0.0D, 0.0D, 0.0D);
			}
		}

	}
	
	@Override
	protected void onHit(HitResult result)
	{
		super.onHit(result);
		if (!this.level.isClientSide)
		{
			this.playSound(ModSoundEvents.BOMB_EXPLOSION.get(), 1.5F, 0.4F / (this.level.getRandom().nextFloat() * 0.4F + 0.8F));
			this.level.broadcastEntityEvent(this, (byte)3);
			LightSource.setLightSource(this.level, this.blockPosition(), 15, 1.5F);
			
			List<Entity> targets = this.level.getEntities(this, this.getBoundingBox().inflate(1.15F));
			for (Entity entity : targets)
			{
				entity.hurt(ExtendedDamageSource.causeProjectileDamage(this, this.getOwner(),
						StunType.HEAVY, 1.0F, 1.0F, Damages.create().put(CoreDamageType.FIRE, this.damage)), this.damage);
			}
			
			this.discard();
		}
	}
}
