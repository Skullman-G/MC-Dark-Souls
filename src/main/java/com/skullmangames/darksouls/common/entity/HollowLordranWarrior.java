package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class HollowLordranWarrior extends Hollow
{
	public HollowLordranWarrior(EntityType<? extends HollowLordranWarrior> p_i48576_1_, Level p_i48576_2_)
	{
		super(p_i48576_1_, p_i48576_2_);
	}
	
	@Override
	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
	{
		int r = this.random.nextInt(2);
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack itemstack = this.getItemBySlot(slot);
			if (itemstack.isEmpty())
			{
				Item item = getEquipmentForSlot(r, slot);
				if (item != null)
				{
					this.setItemSlot(slot, new ItemStack(item));
					this.setDropChance(slot, 0.4F);
				}
			}
		}
	}
	
	protected Item getEquipmentForSlot(int i, EquipmentSlot slot)
	{
		switch (slot)
		{
		default: return Items.AIR;
		case HEAD: return ModItems.LORDRAN_WARRIOR_HELM.get();
		case CHEST: return ModItems.LORDRAN_WARRIOR_ARMOR.get();
		case LEGS: return ModItems.LORDRAN_WARRIOR_WAISTCLOTH.get();
		case FEET: return ModItems.LORDRAN_WARRIOR_BOOTS.get();
		case MAINHAND:
			switch(i)
			{
			default: case 0: return Items.IRON_SWORD;
			case 1: return ModItems.BATTLE_AXE.get();
			}
		case OFFHAND:
			switch(i)
			{
			default: case 0: return ModItems.CRACKED_ROUND_SHIELD.get();
			case 1: return Items.AIR;
			}
		}
	}
}
