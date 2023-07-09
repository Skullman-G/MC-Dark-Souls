package com.skullmangames.darksouls.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public abstract class BreakableObject extends Entity
{
	public BreakableObject(EntityType<? extends BreakableObject> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
		this.move(MoverType.SELF, this.getDeltaMovement());
	}
	
	@Override
	protected void checkFallDamage(double distance, boolean isOnGround, BlockState blockstate, BlockPos blockpos)
	{
		if (isOnGround && this.fallDistance > 1)
		{
			this.hurt(null, 0);
		}
		super.checkFallDamage(distance, isOnGround, blockstate, blockpos);
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount)
	{
		this.kill();
		return true;
	}
	
	@Override
	public void kill()
	{
		if (!this.level.isClientSide)
		{
			this.playSound(this.getBreakSound(), this.random.nextFloat() * 0.5F + 1.0F, 1.0F);
		}
		super.kill();
	}
	
	@Override
	public void onRemovedFromWorld()
	{
		if (this.level.isClientSide)
		{
			for (int r = 0; r < 3; r++)
			{
				float radius = r * 0.5F;
				for (int i = 0; i < 360; i++)
				{
					if (i % 40 == 0)
					{
						double a = Math.toRadians(i);
						this.level.addParticle(this.getBreakParticle(), this.getX() + Math.sin(a) * radius, this.random.nextFloat() * 2.0F + this.getY(),
								this.getZ() + Math.cos(a) * radius, Math.sin(a) * 0.1F, -0.25, Math.cos(a) * 0.1F);
					}
				}
			}
		}
		super.onRemovedFromWorld();
	}
	
	@Override
	public SoundSource getSoundSource()
	{
		return SoundSource.BLOCKS;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	protected abstract ParticleOptions getBreakParticle();
	
	protected abstract SoundEvent getBreakSound();

	@Override
	protected void defineSynchedData() {}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt) {}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt) {}

	@Override
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
