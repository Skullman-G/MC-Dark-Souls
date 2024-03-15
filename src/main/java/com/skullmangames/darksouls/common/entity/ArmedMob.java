package com.skullmangames.darksouls.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class ArmedMob extends PathfinderMob
{
	private static final EntityDataAccessor<Integer> EQUIPMENT_TYPE = SynchedEntityData.defineId(ArmedMob.class, EntityDataSerializers.INT);
	
	protected ArmedMob(EntityType<? extends ArmedMob> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	public boolean isPushable()
	{
		return false;
	}
	
	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
	    this.entityData.define(EQUIPMENT_TYPE, this.random.nextInt(this.getMaxEquipmentTypes()));
	}
	
	public int getEquipmentType()
	{
		return this.entityData.get(EQUIPMENT_TYPE);
	}
	
	public void setEquipmentType(int value)
	{
		this.entityData.set(EQUIPMENT_TYPE, value);
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, SpawnGroupData data, CompoundTag nbt)
	{
		data = super.finalizeSpawn(level, difficulty, type, data, nbt);
		return data;
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		boolean drop = false;
		
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack itemstack = this.getItemBySlot(slot);
			if (itemstack.isEmpty())
			{
				Item item = this.getEquipmentForSlot(this.getEquipmentType(), slot);
				if (item != null)
				{
					this.setItemSlot(slot, new ItemStack(item));
					
					if (this.random.nextBoolean() && !drop)
					{
						this.setDropChance(slot, 0.5F);
						drop = true;
					}
					else this.setDropChance(slot, 0F);
				}
			}
		}
	}
	
	protected abstract Item getEquipmentForSlot(int equipmentType, EquipmentSlot slot);
	
	protected abstract int getMaxEquipmentTypes();
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt)
	{
		super.readAdditionalSaveData(nbt);
		System.out.print("\nreading");
		int equipmentType = nbt.contains("DSEquipmentType") ? nbt.getInt("DSEquipmentType")
				: this.random.nextInt(this.getMaxEquipmentTypes());
		this.setEquipmentType(equipmentType);
		this.populateDefaultEquipmentSlots((DifficultyInstance)null);
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag nbt)
	{
		super.addAdditionalSaveData(nbt);
		nbt.putInt("DSEquipmentType", this.getEquipmentType());
	}
}
