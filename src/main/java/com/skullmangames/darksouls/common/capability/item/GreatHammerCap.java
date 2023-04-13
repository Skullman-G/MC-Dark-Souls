package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class GreatHammerCap extends MeleeWeaponCap
{
	public GreatHammerCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.GREAT_HAMMER, Colliders.GREAT_HAMMER, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@Override
	public boolean hasHoldingAnimation()
	{
		return true;
	}
	
	@Override
	public SoundEvent getSmashSound()
	{
		return ModSoundEvents.GREAT_HAMMER_SMASH.get();
	}
}
