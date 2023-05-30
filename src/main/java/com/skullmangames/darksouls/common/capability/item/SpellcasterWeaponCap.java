package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCastSpell;
import net.minecraft.world.item.Item;

public class SpellcasterWeaponCap extends WeaponCap
{
	public SpellcasterWeaponCap(Item item, WeaponCategory category, ImmutableMap<CoreDamageType, Integer> damage, float critical,
			float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, damage, critical, weight, statRequirements, statScaling);
	}
	
	@Override
	public void performAttack(AttackType type, LocalPlayerCap playerCap)
	{
		Item item = playerCap.getAttunements().getSelected().getItem();
		if (item instanceof SpellItem)
		{
			ModNetworkManager.sendToServer(new CTSCastSpell((SpellItem)item));
		}
	}
	
	public static Builder builder(Item item, WeaponCategory category, float critical, float weight)
	{
		return new Builder(item, category, critical, weight);
	}
	
	public static class Builder
	{
		private Item item;
		private WeaponCategory category;
		private float critical;
		private float weight;
		private ImmutableMap.Builder<CoreDamageType, Integer> damage = ImmutableMap.builder();
		private ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		private ImmutableMap.Builder<Stat, Scaling> statScaling = ImmutableMap.builder();
		
		private Builder(Item item, WeaponCategory category, float critical, float weight)
		{
			this.item = item;
			this.category = category;
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
		
		public SpellcasterWeaponCap build()
		{
			return new SpellcasterWeaponCap(this.item, this.category, this.damage.build(), this.critical, this.weight,
					this.statRequirements.build(), this.statScaling.build());
		}
	}
}
