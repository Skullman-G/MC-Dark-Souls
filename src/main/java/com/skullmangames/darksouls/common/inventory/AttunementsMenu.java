package com.skullmangames.darksouls.common.inventory;

import com.skullmangames.darksouls.core.init.ModContainers;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;

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
}
