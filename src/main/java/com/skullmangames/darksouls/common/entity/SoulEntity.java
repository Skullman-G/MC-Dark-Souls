package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class SoulEntity extends Entity implements IEntityAdditionalSpawnData
{
	private PlayerEntity followingEntity;
	private int value;

	public SoulEntity(World level, double posX, double posY, double posZ, int value)
	{
		this(ModEntities.SOUL.get(), level);
		this.setPos(posX, posY, posZ);
		this.yRot = (float) (this.random.nextDouble() * 360.0D);
		this.value = value;
	}
	
	public SoulEntity(World level, double posX, double posY, double posZ)
	{
		this(ModEntities.SOUL.get(), level);
		this.setPos(posX, posY, posZ);
		this.yRot = (float) (this.random.nextDouble() * 360.0D);
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
		
		this.level.addParticle(ModParticles.SOUL.get(), this.getX(), this.getY()+0.2D, this.getZ(), 0, 0, 0);

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
			Vector3d vector3d = new Vector3d(this.followingEntity.getX() - this.getX(),
					this.followingEntity.getY() + (double) this.followingEntity.getEyeHeight() / 2.0D - this.getY(),
					this.followingEntity.getZ() - this.getZ());
			double d1 = vector3d.lengthSqr();
			if (d1 < 64.0D)
			{
				this.setDeltaMovement(this.getDeltaMovement().add(vector3d.normalize().scale(0.01D)));
			}
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	@Override
	public void playerTouch(PlayerEntity player)
	{
		if (!this.level.isClientSide && this.tickCount > 50)
		{
			if (this.value > 0)
			{
				PlayerData<?> playerdata = (PlayerData<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerdata != null)
					playerdata.raiseSouls(this.value);
			}

			this.remove();
		}
	}

	@Override
	protected boolean isMovementNoisy()
	{
		return false;
	}

	@Override
	protected void defineSynchedData()
	{
	}

	@Override
	protected void doWaterSplashEffect()
	{
	}

	@Override
	public boolean hurt(DamageSource p_70097_1_, float p_70097_2_)
	{
		return false;
	}

	@Override
	public boolean isAttackable()
	{
		return false;
	}

	@Override
	public IPacket<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
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

	@Override
	public void writeSpawnData(PacketBuffer buffer)
	{
		buffer.writeInt(this.value);
	}

	@Override
	public void readSpawnData(PacketBuffer buffer)
	{
		this.value = buffer.readInt();
	}
}
