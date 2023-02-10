package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModContainers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class AttunementsMenu extends ChestContainer
{
	public AttunementsMenu(int id, PlayerInventory inventory)
	{
		this(id, inventory, new Inventory(9));
	}
	
	public AttunementsMenu(int id, PlayerInventory inventory, IInventory container)
	{
		super(ModContainers.ATTUNEMENTS.get(), id, inventory, container, 1);
	}

	@Override
	public boolean stillValid(PlayerEntity player)
	{
		return this.getContainer().stillValid(player);
	}
	
	@Override
	public ItemStack clicked(int slotIndex, int p_150401_, ClickType type, PlayerEntity player)
	{
		PlayerInventory inventory = player.inventory;
		Slot slot = slotIndex >= 0 && slotIndex < this.slots.size() ? this.slots.get(slotIndex) : null;
		if (slot != null)
		{
			if (slot.container == this.getContainer())
			{
				if (!inventory.getCarried().isEmpty() && !(inventory.getCarried().getItem() instanceof SpellItem)
						|| slotIndex > (int)player.getAttributeValue(ModAttributes.ATTUNEMENT_SLOTS.get()) - 1) return ItemStack.EMPTY;
			}
			else if (type == ClickType.QUICK_MOVE)
			{
				if (!(slot.getItem().getItem() instanceof SpellItem)) return ItemStack.EMPTY;
				else
				{
					boolean flag = true;
					for (int i = 0; i < (int)player.getAttributeValue(ModAttributes.ATTUNEMENT_SLOTS.get()) - 1; i++)
					{
						if (!this.slots.get(i).hasItem()) flag = false;
					}
					if (flag) return ItemStack.EMPTY;
				}
			}
		}
		return super.clicked(slotIndex, p_150401_, type, player);
	}
}
