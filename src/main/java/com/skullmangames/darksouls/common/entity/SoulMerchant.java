package com.skullmangames.darksouls.common.entity;

import java.util.OptionalInt;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffer;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSoulMerchantOffers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public interface SoulMerchant
{
	Player getTradingPlayer();
	
	void notifyTrade(SoulMerchantOffer offer);
	
	boolean isClientSide();
	
	SoundEvent getNotifyTradeSound();
	
	void setTradingPlayer(Player player);
	
	public void setOffers(SoulMerchantOffers value);

	public SoulMerchantOffers getOffers();
	
	default void openTradingScreen(Player player, Component component)
	{
		OptionalInt optionalint = player.openMenu(new SimpleMenuProvider((id, inventory, p) ->
		{
			return new SoulMerchantMenu(id, inventory, this);
		}, component));
		if (optionalint.isPresent())
		{
			SoulMerchantOffers merchantoffers = this.getOffers();
			if (!merchantoffers.isEmpty() && !player.level.isClientSide)
			{
				ModNetworkManager.sendToPlayer(new STCSoulMerchantOffers(optionalint.getAsInt(), merchantoffers), (ServerPlayer)player);
			}
		}
	}
}
