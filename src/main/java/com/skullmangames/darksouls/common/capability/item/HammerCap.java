package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import net.minecraft.world.item.Item;

public class HammerCap extends MeleeWeaponCap
{
	public HammerCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling,
			Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.HAMMER, Colliders.TOOL, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
}
