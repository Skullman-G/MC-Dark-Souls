package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.core.init.EffectInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;

public class CursedFoodStats extends FoodStats
{
	@Override
	public void tick(PlayerEntity playerentity)
	{
		if (playerentity.hasEffect(EffectInit.UNDEAD_CURSE.get()))
		{
			if (this.foodLevel != 20)
			{
				this.foodLevel = 20;
			}
			this.lastFoodLevel = this.foodLevel;
			this.tickTimer = 0;
		}
		else
		{
			super.tick(playerentity);
		}
	}
}
