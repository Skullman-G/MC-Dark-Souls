package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.inventory.SoulMerchantOffer;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

public class ClientSoulMerchant implements SoulMerchant
{
	private final Player player;
	private SoulMerchantOffers offers = new SoulMerchantOffers();
	
	public ClientSoulMerchant(Player player)
	{
		this.player = player;
	}
	
	@Override
	public Player getTradingPlayer()
	{
		return this.player;
	}

	@Override
	public void notifyTrade(SoulMerchantOffer offer) {}

	@Override
	public boolean isClientSide()
	{
		return this.player.level.isClientSide;
	}

	@Override
	public SoundEvent getNotifyTradeSound()
	{
		return SoundEvents.VILLAGER_YES;
	}

	@Override
	public void setTradingPlayer(Player player) {}

	@Override
	public void setOffers(SoulMerchantOffers value)
	{
		this.offers = value;
	}

	@Override
	public SoulMerchantOffers getOffers()
	{
		return this.offers;
	}
}
