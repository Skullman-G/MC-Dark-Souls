package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;

import net.minecraft.world.item.Item;

public class TridentCap extends RangedWeaponCap
{
	public TridentCap(Item item, ImmutableMap<CoreDamageType, Integer> damage, float critical, float weight,
			ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, WeaponCategory.NONE_WEAON, null, Animations.BIPED_SPEER_AIM, Animations.BIPED_SPEER_REBOUND, damage,
				critical, weight, statRequirements, statScaling);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.MAINHAND_ONLY;
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
		private ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		private ImmutableMap.Builder<Stat, Scaling> statScaling = ImmutableMap.builder();
		
		private Builder(Item item, float critical, float weight)
		{
			this.item = item;
			this.critical = critical;
			this.weight = weight;
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
		
		public TridentCap build()
		{
			return new TridentCap(this.item, this.damage.build(), this.critical, this.weight, this.statRequirements.build(), this.statScaling.build());
		}
	}
}