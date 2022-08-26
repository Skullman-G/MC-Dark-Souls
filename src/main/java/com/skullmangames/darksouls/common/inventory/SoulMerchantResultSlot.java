package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.SoulMerchant;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SoulMerchantResultSlot extends Slot
{
	private final Player player;
	private final SoulMerchant merchant;
	private final SoulMerchantContainer container;
	private int removeCount;
	
	public SoulMerchantResultSlot(Player player, SoulMerchant merchant, SoulMerchantContainer container, int i, int x, int y)
	{
		super(container, i, x, y);
		this.player = player;
		this.merchant = merchant;
		this.container = container;
	}
	
	@Override
	public boolean mayPlace(ItemStack itemstack)
	{
		return false;
	}
	
	public ItemStack remove(int count)
	{
		if (this.hasItem())
		{
			this.removeCount += Math.min(count, this.getItem().getCount());
		}
		return super.remove(count);
	}

	protected void onQuickCraft(ItemStack itemstack, int count)
	{
		this.removeCount += count;
		this.checkTakeAchievements(itemstack);
	}

	protected void checkTakeAchievements(ItemStack itemstack)
	{
		itemstack.onCraftedBy(this.player.level, this.player, this.removeCount);
		this.removeCount = 0;
	}

	public void onTake(Player player, ItemStack itemstack)
	{
		this.checkTakeAchievements(itemstack);
		SoulMerchantOffer offer = this.container.getOffer();
		if (offer != null && offer.canAccept(player))
		{
			this.merchant.notifyTrade(offer);
			PlayerCap<?> playerCap = (PlayerCap<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (playerCap != null) playerCap.raiseSouls(-offer.getCost());
		}
	}
}
