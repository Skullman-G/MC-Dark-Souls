package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;

public abstract class MaterialItemCapability extends CapabilityItem
{
	protected IItemTier itemTier;
	
	public MaterialItemCapability(Item item, WeaponCategory category)
	{
		super(item, category);
		this.itemTier = ((TieredItem)item).getTier();
		if (DarkSouls.isPhysicalClient())
		{
			loadClientThings();
		}
		this.registerAttribute();
	}
}