package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.core.init.ModContainers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class AttunementsMenu extends ChestMenu
{
	public AttunementsMenu(int id, Inventory inventory)
	{
		this(id, inventory, new SimpleContainer(9));
	}
	
	public AttunementsMenu(int id, Inventory inventory, Container container)
	{
		super(ModContainers.ATTUNEMENTS.get(), id, inventory, container, 1);
	}

	@Override
	public boolean stillValid(Player player)
	{
		return this.getContainer().stillValid(player);
	}
	
	@Override
	public void clicked(int slotIndex, int p_150401_, ClickType type, Player player)
	{
		Slot slot = slotIndex > 0 && slotIndex < this.slots.size() ? this.slots.get(slotIndex) : null;
		if (slot != null && slot.container == this.getContainer() && !this.getCarried().isEmpty() && !(this.getCarried().getItem() instanceof SpellItem)) return;
		super.clicked(slotIndex, p_150401_, type, player);
	}
}
