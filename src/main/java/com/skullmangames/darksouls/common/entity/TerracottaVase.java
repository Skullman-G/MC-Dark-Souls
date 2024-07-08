package com.skullmangames.darksouls.common.entity;

import java.util.List;

import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class TerracottaVase extends BreakableObject
{
	protected static final EntityDataAccessor<ItemStack> ITEM_INSIDE = SynchedEntityData.defineId(TerracottaVase.class, EntityDataSerializers.ITEM_STACK);
	
	public TerracottaVase(EntityType<? extends TerracottaVase> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		this.entityData.define(ITEM_INSIDE, ItemStack.EMPTY);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if (this.level.isClientSide) return;
		
		AABB inputCheck = new AABB(new BlockPos(this.position()).above(2));
		List<ItemEntity> items = this.level.getEntitiesOfClass(ItemEntity.class, inputCheck);
		if (!items.isEmpty())
		{
			ItemEntity item = items.get(0);
			ItemStack itemInside = this.getItemInside();
			if (itemInside.isEmpty())
			{
				this.setItemInside(item.getItem());
				item.discard();
			}
			else if (itemInside.getItem() == item.getItem().getItem())
			{
				itemInside.grow(item.getItem().getCount());
				item.discard();
			}
		}
	}
	
	public void setItemInside(ItemStack item)
	{
		this.entityData.set(ITEM_INSIDE, item);
	}
	
	public ItemStack getItemInside()
	{
		return this.entityData.get(ITEM_INSIDE);
	}
	
	@Override
	public void die(DamageSource source)
	{
		if (!this.level.isClientSide)
		{
			ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY() + 1, this.getZ(), this.getItemInside(), 0, 0, 0);
			this.level.addFreshEntity(itemEntity);
		}
		super.die(source);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
	{
		super.readAdditionalSaveData(nbt);
		this.setItemInside(ItemStack.of(nbt.getCompound("item_inside")));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		ItemStack itemInside = this.getItemInside();
		if (!itemInside.isEmpty())
		{
			nbt.put("item_inside", itemInside.save(new CompoundTag()));
		}
	}

	@Override
	protected ParticleOptions getBreakParticle()
	{
		return ModParticles.VASE_SHARD.get();
	}

	@Override
	protected SoundEvent getBreakSound()
	{
		return ModSoundEvents.VASE_BREAK.get();
	}
}
