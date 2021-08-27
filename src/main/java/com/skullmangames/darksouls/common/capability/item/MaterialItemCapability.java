package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;

public abstract class MaterialItemCapability extends WeaponCapability implements IShield
{
	protected IItemTier itemTier;
	
	public MaterialItemCapability(Item item, WeaponCategory category)
	{
		super(item, category);
		this.itemTier = ((TieredItem)item).getTier();
		this.registerAttribute();
	}
	
	@Override
	public float getPhysicalDefense()
	{
		return 0.2F;
	}
	
	@Override
	public ShieldType getShieldType()
	{
		return ShieldType.NONE;
	}
}