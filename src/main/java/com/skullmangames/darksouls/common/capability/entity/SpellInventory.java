package com.skullmangames.darksouls.common.capability.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SpellInventory implements Container
{
	public final Player player;
	public static final int MAX_SLOTS = 9;
	private final NonNullList<ItemStack> spells = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
	public int selected;
	
	public SpellInventory(Player player)
	{
		this.player = player;
	}
	
	public ListTag save(ListTag nbt)
	{
		for (int i = 0; i < this.spells.size(); ++i)
		{
			if (!this.spells.get(i).isEmpty())
			{
				CompoundTag compoundtag = new CompoundTag();
				compoundtag.putByte("Slot", (byte) i);
				this.spells.get(i).save(compoundtag);
				nbt.add(compoundtag);
			}
		}

		return nbt;
	}

	public void load(ListTag nbt)
	{
		this.spells.clear();
		for (int i = 0; i < nbt.size(); ++i)
		{
			CompoundTag compoundtag = nbt.getCompound(i);
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
		return this.spells != null && !this.spells.get(index).isEmpty() ? ContainerHelper.removeItem(this.spells, index, count) : ItemStack.EMPTY;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
}
