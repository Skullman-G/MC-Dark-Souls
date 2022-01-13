package com.skullmangames.darksouls.common.capability.item;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;

public class ShovelCapability extends MaterialItemCapability
{
	public ShovelCapability(Item item)
	{
		super(item, WeaponCategory.SHOVEL);
	}
	
	@Override
	protected void registerAttribute()
	{
		double impact = this.itemTier.getLevel() * 0.4D + 0.8D;
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact)));
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