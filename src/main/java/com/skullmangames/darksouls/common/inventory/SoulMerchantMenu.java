package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.entity.ClientSoulMerchant;
import com.skullmangames.darksouls.common.entity.SoulMerchant;
import com.skullmangames.darksouls.core.init.ModContainers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class SoulMerchantMenu extends Container
{
	private final SoulMerchant trader;
	private final SoulMerchantContainer container;
	
	public SoulMerchantMenu(int id, PlayerInventory inventory)
	{
		this(id, inventory, new ClientSoulMerchant(inventory.player));
	}
	
	public SoulMerchantMenu(int id, PlayerInventory inventory, SoulMerchant trader)
	{
		super(ModContainers.SOUL_MERCHANT.get(), id);
		this.trader = trader;
		this.container = new SoulMerchantContainer(trader);
		this.addSlot(new SoulMerchantResultSlot(inventory.player, trader, this.container, 0, 220, 37));
		
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k)
		{
			this.addSlot(new Slot(inventory, k, 108 + k * 18, 142));
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return this.trader.getTradingPlayer() == player;
	}

	public void setSelectionHint(int value)
	{
		this.container.setSelectionHint(value);
	}

	public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot)
	{
		return false;
	}

	public ItemStack quickMoveStack(PlayerEntity player, int i)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(i);
		if (slot != null && slot.hasItem())
		{
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (i == 2)
			{
				if (!this.moveItemStackTo(itemstack1, 3, 39, true))
				{
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
				this.playTradeSound();
			} else if (i != 0 && i != 1)
			{
				if (i >= 3 && i < 30)
				{
					if (!this.moveItemStackTo(itemstack1, 30, 39, false))
					{
						return ItemStack.EMPTY;
					}
				} else if (i >= 30 && i < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false))
				{
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 3, 39, false))
			{
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			} else
			{
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	private void playTradeSound()
	{
		if (!this.trader.isClientSide())
		{
			Entity entity = (Entity)this.trader;
			entity.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(),
					this.trader.getNotifyTradeSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
		}

	}

	public void removed(PlayerEntity player)
	{
		super.removed(player);
		this.trader.setTradingPlayer((PlayerEntity)null);
	}

	public void setOffers(SoulMerchantOffers value)
	{
		this.trader.setOffers(value);
	}

	public SoulMerchantOffers getOffers()
	{
		return this.trader.getOffers();
	}
}
