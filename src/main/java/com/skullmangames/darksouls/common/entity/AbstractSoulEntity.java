package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;

public abstract class AbstractSoulEntity extends Entity implements IEntityAdditionalSpawnData
{
	private PlayerEntity followingEntity;
	protected int value;
	private int discardTime = 25;
	
	public AbstractSoulEntity(EntityType<? extends AbstractSoulEntity> type, World level)
	{
		super(type, level);
	}
	
	public AbstractSoulEntity(EntityType<? extends AbstractSoulEntity> type, World level, double posX, double posY, double posZ, int value)
	{
		this(type, level, posX, posY, posZ);
		this.value = value;
	}

	public AbstractSoulEntity(EntityType<? extends AbstractSoulEntity> type, World level, double posX, double posY, double posZ)
	{
		this(type, level);
		this.setPos(posX, posY, posZ);
		this.yRot = (float) (this.random.nextDouble() * 360.0D);
	}

	@Override
	public void tick()
	{
		super.tick();

		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();

		this.makeParticles();

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
			this.setDeltaMovement(vector3d.normalize().scale(0.5D));
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
				PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerCap != null) this.realPlayerTouch(playerCap);
			}
			else if (--discardTime <= 0) this.remove();
		}
	}
	
	protected abstract void realPlayerTouch(PlayerCap<?> playerCap);
	
	protected abstract void makeParticles();

	@Override
	protected boolean isMovementNoisy()
	{
		return false;
	}

	@Override
	protected void defineSynchedData() {}

	@Override
	protected void doWaterSplashEffect() {}

	@Override
	public boolean hurt(DamageSource source, float damage) { return false; }

	@Override
	public boolean isAttackable() { return false; }

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
