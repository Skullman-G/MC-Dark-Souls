package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;

import net.minecraft.world.item.Item;

public abstract class RangedWeaponCap extends WeaponCap
{
	public RangedWeaponCap(Item item, WeaponCategory category, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot,
			ImmutableMap<CoreDamageType, Integer> damage, float critical,
			 float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, damage, critical, weight, statRequirements, statScaling);
		if(reload != null)
		{
			this.animationSet.put(LivingMotion.RELOADING, reload);
		}
		if(aiming != null)
		{
			this.animationSet.put(LivingMotion.AIMING, aiming);
		}
		if(shot != null)
		{
			this.animationSet.put(LivingMotion.SHOOTING, shot);
		}
	}

	@Override
	public boolean canUseOnMount()
	{
		return true;
	}
}
