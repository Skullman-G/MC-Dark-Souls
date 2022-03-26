package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModEntities;
import com.skullmangames.darksouls.core.init.ModParticles;

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

public class SoulEntity extends Entity implements IEntityAdditionalSpawnData
{
	private Player followingEntity;
	private int value;
	
	public SoulEntity(EntityType<? extends SoulEntity> type, Level level)
	{
		super(type, level);
	}
	
	public SoulEntity(Level level, double posX, double posY, double posZ, int value)
	{
		this(level, posX, posY, posZ);
		this.value = value;
	}

	public SoulEntity(Level level, double posX, double posY, double posZ)
	{
		this(ModEntities.SOUL.get(), level);
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

		this.level.addParticle(ModParticles.SOUL.get(), this.getX(), this.getY() + 0.2D, this.getZ(), 0, 0, 0);

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
			double d1 = vector3d.lengthSqr();
			if (d1 < 64.0D)
			{
				this.setDeltaMovement(this.getDeltaMovement().add(vector3d.normalize().scale(0.01D)));
			}
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	@Override
	public void playerTouch(Player player)
	{
		if (!this.level.isClientSide && this.tickCount > 50)
		{
			if (this.value > 0)
			{
				PlayerCap<?> playerdata = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null)
						.orElse(null);
				if (playerdata != null)
					playerdata.raiseSouls(this.value);
			}

			this.discard();
		}
	}

	@Override
	protected Entity.MovementEmission getMovementEmission()
	{
		return Entity.MovementEmission.NONE;
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
