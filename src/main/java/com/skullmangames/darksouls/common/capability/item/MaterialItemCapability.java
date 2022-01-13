package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;

public abstract class MaterialItemCapability extends WeaponCapability
{
	protected Tier itemTier;
	
	public MaterialItemCapability(Item item, WeaponCategory category)
	{
		super(item, category);
		this.itemTier = ((TieredItem)item).getTier();
		this.registerAttribute();
	}
}