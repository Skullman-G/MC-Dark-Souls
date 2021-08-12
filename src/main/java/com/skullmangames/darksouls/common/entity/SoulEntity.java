package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.EntityTypeInit;
import com.skullmangames.darksouls.network.server.SSpawnSoulPacket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SoulEntity extends Entity
{
	private PlayerEntity followingEntity;
	private int value;
	
	public SoulEntity(World level, double posX, double posY, double posZ, int value)
	{
	   this(EntityTypeInit.SOUL.get(), level);
	   this.setPos(posX, posY, posZ);
	   this.yRot = (float)(this.random.nextDouble() * 360.0D);
	   this.value = value;
	}

	public SoulEntity(EntityType<? extends SoulEntity> type, World level)
	{
	   super(type, level);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		this.xo = this.getX();
	    this.yo = this.getY();
	    this.zo = this.getZ();
		
		if (this.followingEntity == null || this.followingEntity.distanceToSqr(this) > 64.0D)
		{
			this.followingEntity = this.level.getNearestPlayer(this, 8.0D);
		}
		
		if (this.followingEntity != null && this.followingEntity.isSpectator())
		{
	        this.followingEntity = null;
	    }
		
		if (this.followingEntity != null)
		{
	       Vector3d vector3d = new Vector3d(this.followingEntity.getX() - this.getX(), this.followingEntity.getY() + (double)this.followingEntity.getEyeHeight() / 2.0D - this.getY(), this.followingEntity.getZ() - this.getZ());
	       double d1 = vector3d.lengthSqr();
	       if (d1 < 64.0D)
	       {
	          double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
	          this.setDeltaMovement(this.getDeltaMovement().add(vector3d.normalize().scale(d2 * d2 * 0.1D)));
	       }
	    }
		
		this.move(MoverType.SELF, this.getDeltaMovement());
	}
	
	@Override
	public void playerTouch(PlayerEntity player)
	{
		if (!this.level.isClientSide)
		{
	        if (this.value > 0)
	        {
	           ModEntityDataManager.raiseSouls(player, this.value);
	        }

	        this.remove();
	    }
	}

	protected boolean isMovementNoisy()
	{
	   return false;
	}

	protected void defineSynchedData()
	{
	}

	protected void doWaterSplashEffect()
	{
	}

	public boolean hurt(DamageSource p_70097_1_, float p_70097_2_)
	{
	   return false;
	}

	public boolean isAttackable()
	{
	   return false;
	}

	@Override
	public IPacket<?> getAddEntityPacket()
	{
		return new SSpawnSoulPacket(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt)
	{
		this.value = nbt.getInt("Value");
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt)
	{
		nbt.putInt("Value", this.value);
	}
	
	public int getValue()
	{
		return this.value;
	}
}
