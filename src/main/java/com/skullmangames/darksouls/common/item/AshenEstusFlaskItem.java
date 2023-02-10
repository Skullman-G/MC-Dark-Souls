package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AshenEstusFlaskItem extends EstusFlaskItem
{
	public AshenEstusFlaskItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	protected void activate(LivingEntity entity, ItemStack stack)
	{
		if (!(entity instanceof PlayerEntity)) return;
		PlayerCap<?> playerCap = (PlayerCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (playerCap == null) return;
		playerCap.raiseFP(EstusFlaskItem.getHeal(stack));
	}
}
