package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCAttunements;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;

public class SpellInventory implements IInventory
{
	public final PlayerEntity player;
	public static final int MAX_SLOTS = 9;
	private final NonNullList<ItemStack> spells = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
	public int selected;
	
	public SpellInventory(PlayerEntity player)
	{
		this.player = player;
	}
	
	public void updateSize()
	{
		for (int i = (int)this.player.getAttributeValue(ModAttributes.ATTUNEMENT_SLOTS.get()); i < MAX_SLOTS; i++)
		{
			ItemStack spell = this.spells.get(i);
			if (!spell.isEmpty())
			{
				this.player.drop(spell.copy(), true);
				this.removeItem(i, 1);
			}
		}
	}
	
	public ItemStack getSelected()
	{
		return this.spells.get(this.selected);
	}
	
	public ListNBT save(ListNBT nbt)
	{
		for (int i = 0; i < this.spells.size(); ++i)
		{
			if (!this.spells.get(i).isEmpty())
			{
				CompoundNBT compoundtag = new CompoundNBT();
				compoundtag.putByte("Slot", (byte) i);
				this.spells.get(i).save(compoundtag);
				nbt.add(compoundtag);
			}
		}

		return nbt;
	}

	public void load(ListNBT nbt)
	{
		this.spells.clear();
		for (int i = 0; i < nbt.size(); ++i)
		{
			CompoundNBT compoundtag = nbt.getCompound(i);
			int j = compoundtag.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.of(compoundtag);
			if (!itemstack.isEmpty())
			{
				if (j >= 0 && j < this.spells.size())
				{
					this.spells.set(j, itemstack);
				}
			}
		}
	}
	
	@Override
	public void clearContent()
	{
		this.spells.clear();
	}

	@Override
	public int getContainerSize()
	{
		return MAX_SLOTS;
	}

	@Override
	public boolean isEmpty()
	{
		return this.spells.isEmpty();
	}

	@Override
	public ItemStack getItem(int index)
	{
		if (index > this.spells.size() || index < 0) return ItemStack.EMPTY;
		return this.spells.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count)
	{
		return this.spells != null && !this.spells.get(index).isEmpty() ? ItemStackHelper.removeItem(this.spells, index, count) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index)
	{
		if (!this.spells.get(index).isEmpty())
		{
			ItemStack itemstack = this.spells.get(index);
			this.spells.set(index, ItemStack.EMPTY);
			return itemstack;
		}
		else
		{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setItem(int index, ItemStack stack)
	{
		this.spells.set(index, stack);
	}

	@Override
	public void setChanged()
	{
		if (!this.player.level.isClientSide)
		{
			ModNetworkManager.sendToPlayer(new STCAttunements(this.player.getId(), this.spells), (ServerPlayerEntity)this.player);
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return true;
	}
}
