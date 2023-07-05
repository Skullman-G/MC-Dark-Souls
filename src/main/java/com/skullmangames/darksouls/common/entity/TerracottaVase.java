package com.skullmangames.darksouls.common.entity;

import java.util.List;

import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class TerracottaVase extends BreakableObject
{
	private ItemStack itemInside = ItemStack.EMPTY;
	
	public TerracottaVase(EntityType<? extends TerracottaVase> type, Level level)
	{
		super(type, level);
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
			if (this.itemInside.isEmpty())
			{
				this.itemInside = item.getItem();
				item.discard();
			}
			else if (this.itemInside.getItem() == item.getItem().getItem())
			{
				this.itemInside.grow(item.getItem().getCount());
				item.discard();
			}
		}
	}
	
	@Override
	public void kill()
	{
		if (!this.level.isClientSide)
		{
			ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY() + 1, this.getZ(), this.itemInside, 0, 0, 0);
			this.level.addFreshEntity(itemEntity);
		}
		super.kill();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt)
	{
		this.itemInside = ItemStack.of(nbt.getCompound("item_inside"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt)
	{
		if (!this.itemInside.isEmpty())
		{
			nbt.put("item_inside", this.itemInside.save(new CompoundTag()));
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
