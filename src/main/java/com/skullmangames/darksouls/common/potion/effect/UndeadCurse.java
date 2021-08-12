package com.skullmangames.darksouls.common.potion.effect;

import com.skullmangames.darksouls.core.init.ItemInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class UndeadCurse extends Effect
{
	public UndeadCurse()
	{
		super(EffectType.NEUTRAL, 0);
	}
	
	public void onPotionAdd(PlayerEntity playerentity)
	{
		this.onPotionRemove(playerentity);
		playerentity.inventory.add(new ItemStack(ItemInit.DARKSIGN.get()));
	}
	
	public void onPotionRemove(PlayerEntity playerentity)
	{
		for (ItemStack itemstack : playerentity.inventory.items)
		{
			if (itemstack.getItem() == ItemInit.DARKSIGN.get())
			{
				playerentity.inventory.removeItem(itemstack);
			}
		}
	}
}
