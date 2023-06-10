package com.skullmangames.darksouls.common.entity;

import java.util.List;

import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

public class TerracottaVase extends Entity
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
			ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY() + 1, this.getZ(), this.itemInside, 0, 0, 0);
			this.level.addFreshEntity(itemEntity);
			this.playSound(ModSoundEvents.VASE_BREAK.get(), this.random.nextFloat() * 0.5F + 1.0F, 1.0F);
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
						this.level.addParticle(ModParticles.VASE_SHARD.get(), this.getX() + Math.sin(a) * radius, this.random.nextFloat() * 2.0F + this.getY(),
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

	@Override
	protected void defineSynchedData() {}

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
	public Packet<?> getAddEntityPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
