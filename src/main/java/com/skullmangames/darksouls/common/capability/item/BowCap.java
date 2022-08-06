package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.world.item.Item;

public class BowCap extends RangedWeaponCap
{
	public BowCap(Item item, float damage, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.BOW, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_REBOUND, damage, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.TWO_HANDED;
	}

	@Override
	public float getStaminaDamage()
	{
		return 4.0F;
	}
}