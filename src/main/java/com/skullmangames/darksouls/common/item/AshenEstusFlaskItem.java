package com.skullmangames.darksouls.common.item;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AshenEstusFlaskItem extends EstusFlaskItem
{
	public AshenEstusFlaskItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	protected void activate(LivingEntity entity, ItemStack stack)
	{
		if (!(entity instanceof Player)) return;
		PlayerCap<?> playerCap = (PlayerCap<?>)entity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (playerCap == null) return;
		playerCap.raiseFP(EstusFlaskItem.getHeal(stack));
	}
}
