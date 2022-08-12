package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.blockentity.BonfireBlockEntity;
import com.skullmangames.darksouls.common.inventory.container.ReinforceEstusFlaskContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;

public abstract class AbstractFireKeeper extends QuestEntity
{
	private BlockPos linkedBonfirePos;
	private boolean hasLinkedBonfire = false;

	public AbstractFireKeeper(EntityType<? extends QuestEntity> entity, World level)
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
		TileEntity tileentity = this.level.getBlockEntity(this.linkedBonfirePos);
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
	public void addAdditionalSaveData(CompoundNBT nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("LinkedBonfireX", this.linkedBonfirePos.getX());
		nbt.putInt("LinkedBonfireY", this.linkedBonfirePos.getY());
		nbt.putInt("LinkedBonfireZ", this.linkedBonfirePos.getZ());
		nbt.putBoolean("HasLinkedBonfire", this.hasLinkedBonfire);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.linkedBonfirePos = new BlockPos(nbt.getInt("LinkedBonfireX"), nbt.getInt("LinkedBonfireY"), nbt.getInt("LinkedBonfireZ"));
		this.hasLinkedBonfire = nbt.getBoolean("HasLinkedBonfire");
	}

	@Override
	public void tick()
	{
		super.tick();
		
		if (!this.hasLinkedBonfire)
		{
			for (BlockPos pos : this.level.getChunk(this.blockPosition()).getBlockEntitiesPos())
			{
				TileEntity tileentity = this.level.getBlockEntity(pos);
				BonfireBlockEntity t = tileentity instanceof BonfireBlockEntity ? (BonfireBlockEntity)tileentity : null;
				if (t != null && !t.hasFireKeeper() && t.getBlockPos().distSqr(this.blockPosition()) <= 1000)
				{
					this.linkBonfire(t.getBlockPos());
					break;
				}
			}
		}
		
		if (!this.level.isClientSide() && !this.isDeadOrDying())
		{
			if (this.hasLinkedBonfire && this.getLinkedBonfire() == null)
			{
				this.hurt(DamageSource.STARVE, this.getHealth());
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

	public void openContainer(ServerPlayerEntity serverplayer)
	{
		SimpleNamedContainerProvider container = new SimpleNamedContainerProvider((id, inventory, p_235576_4_) ->
		{
			return new ReinforceEstusFlaskContainer(id, inventory, IWorldPosCallable.create(this.level, this.blockPosition()));
		}, new TranslationTextComponent("container.reinforce_estus_flask.title"));
		serverplayer.openMenu(container);
	}
}