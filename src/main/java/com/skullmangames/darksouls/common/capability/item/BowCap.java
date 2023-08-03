package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.world.item.Item;

public class BowCap extends RangedWeaponCap
{
	public BowCap(Item item, ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects,
			float critical, float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, WeaponCategory.BOW, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_REBOUND, damage, auxEffects, critical,
				weight, statRequirements, statScaling);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.TWO_HANDED;
	}
	
	public static Builder builder(Item item, float critical, float weight)
	{
		return new Builder(item, critical, weight);
	}
	
	public static class Builder
	{
		private Item item;
		private float critical;
		private float weight;
		private ImmutableMap.Builder<CoreDamageType, Integer> damage = ImmutableMap.builder();
		private ImmutableSet.Builder<AuxEffect> auxEffects = ImmutableSet.builder();
		private ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		private ImmutableMap.Builder<Stat, Scaling> statScaling = ImmutableMap.builder();
		
		private Builder(Item item, float critical, float weight)
		{
			this.item = item;
			this.critical = critical;
			this.weight = weight;
		}
		
		public Builder putAuxEffect(AuxEffect auxEffect)
		{
			this.auxEffects.add(auxEffect);
			return this;
		}
		
		public Builder putDamage(CoreDamageType type, int value)
		{
			this.damage.put(type, value);
			return this;
		}
		
		public Builder putStatInfo(Stat stat, int requirement, Scaling scaling)
		{
			this.statRequirements.put(stat, requirement);
			this.statScaling.put(stat, scaling);
			return this;
		}
		
		public BowCap build()
		{
			return new BowCap(this.item, this.damage.build(), this.auxEffects.build(),
					this.critical, this.weight, this.statRequirements.build(), this.statScaling.build());
		}
	}
}