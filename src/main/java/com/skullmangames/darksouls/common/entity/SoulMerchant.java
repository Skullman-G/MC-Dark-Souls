package com.skullmangames.darksouls.common.entity;

import java.util.OptionalInt;

import com.skullmangames.darksouls.common.inventory.SoulMerchantMenu;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffer;
import com.skullmangames.darksouls.common.inventory.SoulMerchantOffers;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCSoulMerchantOffers;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.entity.player.PlayerEntity;

public interface SoulMerchant
{
	PlayerEntity getTradingPlayer();
	
	void notifyTrade(SoulMerchantOffer offer);
	
	boolean isClientSide();
	
	SoundEvent getNotifyTradeSound();
	
	void setTradingPlayer(PlayerEntity player);
	
	public void setOffers(SoulMerchantOffers value);

	public SoulMerchantOffers getOffers();
	
	default void openTradingScreen(PlayerEntity player, ITextComponent component)
	{
		OptionalInt optionalint = player.openMenu(new SimpleNamedContainerProvider((id, inventory, p) ->
		{
			return new SoulMerchantMenu(id, inventory, this);
		}, component));
		if (optionalint.isPresent())
		{
			SoulMerchantOffers merchantoffers = this.getOffers();
			if (!merchantoffers.isEmpty() && !player.level.isClientSide)
			{
				ModNetworkManager.sendToPlayer(new STCSoulMerchantOffers(optionalint.getAsInt(), merchantoffers), (ServerPlayerEntity)player);
			}
		}
	}
}
