package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;

public class PickaxeCap extends MeleeWeaponCap
{
	public PickaxeCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.PICKAXE, requiredStrength, requiredDex, strengthScaling, dexScaling, 10F);
	}

	@Override
	public SoundEvent getHitSound()
	{
		return null;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.tools;
	}
}