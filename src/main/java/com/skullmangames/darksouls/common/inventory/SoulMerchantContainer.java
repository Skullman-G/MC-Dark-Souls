package com.skullmangames.darksouls.common.inventory;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.entity.SoulMerchant;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SoulMerchantContainer implements IInventory
{
	private final SoulMerchant merchant;
	private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
	@Nullable
	private SoulMerchantOffer offer;
	private int selectionHint;
	
	public SoulMerchantContainer(SoulMerchant merchant)
	{
		this.merchant = merchant;
	}
	
	@Nullable
	public SoulMerchantOffer getOffer()
	{
		return this.offer;
	}
	
	public void setSelectionHint(int value)
	{
		this.selectionHint = value;
		this.updateSellingItem();
	}
	
	public void updateSellingItem()
	{
		SoulMerchantOffers offers = this.merchant.getOffers();
		this.offer = null;
		this.itemStacks.set(0, ItemStack.EMPTY);
		if (!offers.isEmpty())
		{
			this.offer = this.merchant.getOffers().get(this.selectionHint);
			if (this.offer.canAccept(this.merchant.getTradingPlayer())) this.itemStacks.set(0, this.offer.getResult().copy());
		}
	}
	
	@Override
	public void clearContent()
	{
		this.itemStacks.clear();
	}

	@Override
	public int getContainerSize()
	{
		return this.itemStacks.size();
	}

	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack : this.itemStacks)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int i)
	{
		return this.itemStacks.get(i);
	}

	@Override
	public ItemStack removeItem(int i, int count)
	{
		ItemStack stack = this.itemStacks.get(i).copy().split(count);
		this.updateSellingItem();
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int i)
	{
		return this.itemStacks.get(i).copy();
	}

	@Override
	public void setItem(int i, ItemStack itemStack)
	{
		if (i < this.itemStacks.size())
		{
			this.itemStacks.set(i, itemStack);
		}
	}

	@Override
	public void setChanged()
	{
		this.updateSellingItem();
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return this.merchant.getTradingPlayer() == player;
	}
}
