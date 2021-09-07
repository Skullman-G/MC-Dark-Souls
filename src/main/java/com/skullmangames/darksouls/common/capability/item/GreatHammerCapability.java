package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.item.Item;

public class GreatHammerCapability extends WeaponCapability
{
	public GreatHammerCapability(Item item)
	{
		super(item, WeaponCategory.GREAT_HAMMER);
		this.animationSet.put(LivingMotion.IDLE, Animations.BIPED_HOLDING_GREAT_HAMMER);
	}
}
