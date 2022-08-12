package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;

public class WingedSpearCap extends SpearCap
{
	public WingedSpearCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, requiredStrength, requiredDex, strengthScaling, dexScaling);
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.WINGED_SPEAR;
	}
}
