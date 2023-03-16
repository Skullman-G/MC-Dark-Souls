package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;

public class AxeCap extends MeleeWeaponCap
{
	public AxeCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.AXE, Colliders.TOOL, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}

	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.TOOL;
	}
}