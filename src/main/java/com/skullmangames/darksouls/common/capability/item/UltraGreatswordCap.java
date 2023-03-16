package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import net.minecraft.world.item.Item;

public class UltraGreatswordCap extends MeleeWeaponCap
{
	public UltraGreatswordCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.ULTRA_GREATSWORD, Colliders.ULTRA_GREATSWORD, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@Override
	public boolean hasHoldingAnimation()
	{
		return true;
	}
}
