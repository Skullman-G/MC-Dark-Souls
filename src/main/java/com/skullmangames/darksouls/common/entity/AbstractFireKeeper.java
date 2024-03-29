package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.inventory.ReinforceEstusFlaskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractFireKeeper extends QuestEntity
{
	private BlockPos linkedBonfirePos;
	private boolean hasLinkedBonfire = false;

	public AbstractFireKeeper(EntityType<? extends QuestEntity> entity, Level level)
	{
		super(entity, level);
	}

	private void linkBonfire(BlockPos blockpos)
	{
		this.linkedBonfirePos = blockpos;
		this.getLinkedBonfire().addFireKeeper(this.stringUUID);
		this.hasLinkedBonfire = true;
	}

	public BlockPos getLinkedBonfirePos()
	{
		return this.linkedBonfirePos;
	}

	public boolean hasLinkedBonfire()
	{
		return this.hasLinkedBonfire;
	}

	public BonfireBlockEntity getLinkedBonfire()
	{
		BlockEntity tileentity = this.level.getBlockEntity(this.linkedBonfirePos);
		return tileentity instanceof BonfireBlockEntity
				? (BonfireBlockEntity) tileentity
				: null;
	}

	@Override
	public boolean removeWhenFarAway(double p_213397_1_)
	{
		return !this.hasLinkedBonfire;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("LinkedBonfireX", this.linkedBonfirePos.getX());
		nbt.putInt("LinkedBonfireY", this.linkedBonfirePos.getY());
		nbt.putInt("LinkedBonfireZ", this.linkedBonfirePos.getZ());
		nbt.putBoolean("HasLinkedBonfire", this.hasLinkedBonfire);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.linkedBonfirePos = new BlockPos(nbt.getInt("LinkedBonfireX"), nbt.getInt("LinkedBonfireY"), nbt.getInt("LinkedBonfireZ"));
		this.hasLinkedBonfire = nbt.getBoolean("HasLinkedBonfire");
	}

	@Override
	public void tick()
	{
		super.tick();
		
		if (!this.level.isClientSide)
		{
			if (!this.hasLinkedBonfire)
			{
				for (BlockPos pos : this.level.getChunk(this.blockPosition()).getBlockEntitiesPos())
				{
					BlockEntity t = this.level.getBlockEntity(pos);
					if (t instanceof BonfireBlockEntity)
					{
						BonfireBlockEntity b = (BonfireBlockEntity)t;
						if (b != null
								&& !b.hasFireKeeper()
								&& b.getBlockPos().distSqr(this.blockPosition()) <= 1000)
						{
							this.linkBonfire(b.getBlockPos());
							break;
						}
					}
				}
			}
			
			if (!this.isDeadOrDying())
			{
				if (this.hasLinkedBonfire && this.getLinkedBonfire() == null)
				{
					this.hurt(DamageSource.STARVE, this.getHealth());
				}
			}
		}
	}

	@Override
	public void die(DamageSource source)
	{
		if (!this.level.isClientSide())
		{
			if (this.hasLinkedBonfire && this.getLinkedBonfire() != null && this.getLinkedBonfire().getFireKeeperStringUUID() == this.stringUUID)
			{
				this.getLinkedBonfire().setLit(false);
			}
		}
		super.die(source);
	}

	public void openContainer(ServerPlayer serverplayer)
	{
		SimpleMenuProvider container = new SimpleMenuProvider((id, inventory, p_235576_4_) ->
		{
			return new ReinforceEstusFlaskMenu(id, inventory, ContainerLevelAccess.create(this.level, this.blockPosition()));
		}, new TranslatableComponent("container.reinforce_estus_flask.title"));
		serverplayer.openMenu(container);
	}
}