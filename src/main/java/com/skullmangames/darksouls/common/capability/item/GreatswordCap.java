package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.WeaponMovesets;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class GreatswordCap extends MeleeWeaponCap
{
	public GreatswordCap(Item item, int reqStrength, int reqDex,
			int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.GREATSWORD, Colliders.GREATSWORD, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SWORD_SWING.get();
	}
	
	@Override
	public boolean hasHoldingAnimation()
	{
		return true;
	}
}
