package com.skullmangames.darksouls.common.capability.item;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class PickaxeCapability extends MaterialItemCapability
{
	public PickaxeCapability(Item item)
	{
		super(item, WeaponCategory.PICKAXE);
	}
	
	@Override
	protected void registerAttribute()
	{
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(-0.4D + 0.1D * this.itemTier.getLevel())));
	}

	@Override
	public SoundEvent getHitSound()
	{
		return null;
	}
	
	@Override
	public HitParticleType getHitParticle()
	{
		return null;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.tools;
	}
}