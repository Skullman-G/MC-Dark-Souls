package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.world.item.Item;

public abstract class RangedWeaponCap extends WeaponCap
{
	public RangedWeaponCap(Item item, WeaponCategory category, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot,
			ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects, float critical,
			 float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, null, damage, auxEffects, critical, weight, statRequirements, statScaling);
		if(reload != null)
		{
			this.animationOverrides.put(LivingMotion.RELOADING, reload);
		}
		if(aiming != null)
		{
			this.animationOverrides.put(LivingMotion.AIMING, aiming);
		}
		if(shot != null)
		{
			this.animationOverrides.put(LivingMotion.SHOOTING, shot);
		}
	}
}
