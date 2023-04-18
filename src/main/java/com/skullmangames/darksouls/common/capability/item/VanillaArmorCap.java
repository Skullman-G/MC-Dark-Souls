package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.Item;

public class VanillaArmorCap extends ArmorCap
{
	public VanillaArmorCap(Item item)
	{
		super(item);

		this.poise = 1.0F;
		this.weight = this.getOriginalItem().getMaterial().getDefenseForSlot(this.getOriginalItem().getSlot()) * 1.5F;
	}
}