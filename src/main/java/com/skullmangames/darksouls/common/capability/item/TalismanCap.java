package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.Item;

public class TalismanCap extends SpellcasterWeaponCap
{
	public TalismanCap(Item item, int reqStrength, int reqDex, int reqFaith,
			Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.TALISMAN, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling, 0);
	}
}
