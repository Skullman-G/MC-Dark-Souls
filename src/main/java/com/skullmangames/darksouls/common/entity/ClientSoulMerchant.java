package com.skullmangames.darksouls.common.entity;

import com.skullmangames.darksouls.common.inventory.SoulMerchantOffer;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.player.PlayerEntity;

public class ClientSoulMerchant implements SoulMerchant
{
	private final PlayerEntity player;
	private SoulMerchantOffers offers = new SoulMerchantOffers();
	
	public ClientSoulMerchant(PlayerEntity player)
	{
		this.player = player;
	}
	
	@Override
	public PlayerEntity getTradingPlayer()
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
	public void setTradingPlayer(PlayerEntity player) {}

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
