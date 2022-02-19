package com.skullmangames.darksouls.common.entity;

import net.minecraft.nbt.CompoundTag;
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

public abstract class ArmoredMob extends PathfinderMob
{
	protected ArmoredMob(EntityType<? extends ArmoredMob> entitytype, Level level)
	{
		super(entitytype, level);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, SpawnGroupData data, CompoundTag nbt)
	{
		data = super.finalizeSpawn(level, difficulty, type, data, nbt);
		this.populateDefaultEquipmentSlots(difficulty);
		
		return data;
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		int persentage = this.random.nextInt(1, 101);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack itemstack = this.getItemBySlot(slot);
			if (itemstack.isEmpty())
			{
				Item item = this.getEquipmentForSlot(persentage, slot);
				if (item != null)
				{
					this.setItemSlot(slot, new ItemStack(item));
					this.setDropChance(slot, 0.4F);
				}
			}
		}
	}
	
	protected abstract Item getEquipmentForSlot(int percentage, EquipmentSlot slot);
}
