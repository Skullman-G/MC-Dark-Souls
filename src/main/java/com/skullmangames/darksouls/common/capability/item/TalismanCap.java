package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.Item;

public class TalismanCap extends SpellcasterWeaponCap
{
	public TalismanCap(Item item, int requiredStrength, int requiredDex,
			Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.TALISMAN, requiredStrength, requiredDex, strengthScaling, dexScaling, 0);
	}
}
