package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.world.item.Item;

public class BowCapability extends RangedWeaponCapability
{
	public BowCapability(Item item, int requiredStrength, int requiredDex)
	{
		super(item, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_REBOUND, requiredStrength, requiredDex);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.TWO_HANDED;
	}
}