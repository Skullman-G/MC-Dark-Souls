package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.world.item.Item;

public class BowCap extends RangedWeaponCap
{
	public BowCap(Item item, float damage, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_REBOUND, damage, requiredStrength, requiredDex, strengthScaling, dexScaling);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.TWO_HANDED;
	}
}