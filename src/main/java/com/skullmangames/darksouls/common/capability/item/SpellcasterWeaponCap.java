package com.skullmangames.darksouls.common.capability.item;

import net.minecraft.world.item.Item;

public class SpellcasterWeaponCap extends WeaponCap
{
	public SpellcasterWeaponCap(Item item, WeaponCategory category, int requiredStrength, int requiredDex,
			Scaling strengthScaling, Scaling dexScaling, float poiseDamage)
	{
		super(item, category, requiredStrength, requiredDex, strengthScaling, dexScaling, poiseDamage);
	}

	@Override
	public float getStaminaDamage()
	{
		return 0;
	}

	@Override
	public float getDamage()
	{
		return 0;
	}
}
