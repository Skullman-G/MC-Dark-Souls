package com.skullmangames.darksouls.common.potion.effect;

import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UndeadCurse extends MobEffect
{
	public UndeadCurse()
	{
		super(MobEffectCategory.NEUTRAL, 0);
	}
	
	public void onPotionAdd(Player playerentity)
	{
		this.onPotionRemove(playerentity);
		playerentity.inventoryMenu.setCarried(new ItemStack(ModItems.DARKSIGN.get()));
	}
	
	public void onPotionRemove(Player playerentity)
	{
		for (ItemStack itemstack : playerentity.inventoryMenu.getItems())
		{
			if (itemstack.getItem() == ModItems.DARKSIGN.get())
			{
				playerentity.getInventory().removeItem(itemstack);
			}
		}
	}
}
