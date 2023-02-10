package com.skullmangames.darksouls.common.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ArmoredMob extends CreatureEntity
{
	protected ArmoredMob(EntityType<? extends ArmoredMob> entitytype, World level)
	{
		super(entitytype, level);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld level, DifficultyInstance difficulty, SpawnReason type, ILivingEntityData data, CompoundNBT nbt)
	{
		data = super.finalizeSpawn(level, difficulty, type, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		
		return data;
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		int percentage = this.random.nextInt(100);
		boolean drop = false;
		
		for (EquipmentSlotType slot : EquipmentSlotType.values())
		{
			ItemStack itemstack = this.getItemBySlot(slot);
			if (itemstack.isEmpty())
			{
				Item item = this.getEquipmentForSlot(percentage, slot);
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
	
	protected abstract Item getEquipmentForSlot(int percentage, EquipmentSlotType slot);
}
