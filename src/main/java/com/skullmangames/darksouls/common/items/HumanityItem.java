package com.skullmangames.darksouls.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HumanityItem extends SoulContainerItem
{
	public HumanityItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, World world, LivingEntity livingentity)
	{
		livingentity.heal(livingentity.getMaxHealth() - livingentity.getHealth());
		return super.finishUsingItem(itemstack, world, livingentity);
	}
}
