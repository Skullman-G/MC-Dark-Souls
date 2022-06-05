package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractSoulEntity extends Entity implements IEntityAdditionalSpawnData
{
	private Player followingEntity;
	protected int value;
	private int discardTime = 25;
	
	public AbstractSoulEntity(EntityType<? extends AbstractSoulEntity> type, Level level)
	{
		super(type, level);
	}
	
	public AbstractSoulEntity(EntityType<? extends AbstractSoulEntity> type, Level level, double posX, double posY, double posZ, int value)
	{
		this(type, level, posX, posY, posZ);
		this.value = value;
	}

	public AbstractSoulEntity(EntityType<? extends AbstractSoulEntity> type, Level level, double posX, double posY, double posZ)
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
			Vec3 vector3d = new Vec3(this.followingEntity.getX() - this.getX(),
					this.followingEntity.getY() + (double) this.followingEntity.getEyeHeight() / 2.0D - this.getY(),
					this.followingEntity.getZ() - this.getZ());
			this.setDeltaMovement(vector3d.normalize().scale(0.5D));
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
	}
	
	@Override
	public void playerTouch(Player player)
	{
		if (!this.level.isClientSide)
		{
			if (this.value > 0)
			{
				PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerCap != null) this.realPlayerTouch(playerCap);
			}
			else if (--discardTime <= 0) this.discard();
		}
	}
	
	protected abstract void realPlayerTouch(PlayerCap<?> playerCap);
	
	protected abstract void makeParticles();

	@Override
	protected Entity.MovementEmission getMovementEmission()
	{
		return Entity.MovementEmission.NONE;
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
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt)
	{
		this.value = nbt.getInt("Value");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt)
	{
		nbt.putInt("Value", this.value);
	}

	public int getValue()
	{
		return this.value;
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer)
	{
		buffer.writeInt(this.value);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer)
	{
		this.value = buffer.readInt();
	}
}
