package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.item.Item;

public class GreatHammerCapability extends WeaponCapability
{
	public GreatHammerCapability(Item item)
	{
		super(item, WeaponCategory.GREAT_HAMMER);
	}
	
	@Override
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return Animations.BIPED_HOLDING_GREAT_HAMMER;
	}
}
