package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;

public class ShovelCapability extends MaterialItemCapability
{
	public ShovelCapability(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.SHOVEL, requiredStrength, requiredDex, strengthScaling, dexScaling);
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