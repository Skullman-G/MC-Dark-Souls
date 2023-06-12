package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.item.SpellItem;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCastSpell;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class SpellcasterWeaponCap extends WeaponCap
{
	private final float spellBuff;
	
	public SpellcasterWeaponCap(Item item, WeaponCategory category, ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects,
			float critical, float spellBuff, float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, damage, auxEffects, critical, weight, statRequirements, statScaling);
		this.spellBuff = spellBuff;
	}
	
	public float getSpellBuff()
	{
		return this.spellBuff;
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		map.put(ModAttributes.SPELL_BUFF.get(), ModAttributes.getAttributeModifierForSlot(slot, this.getSpellBuff()));
		return map;
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
	
	public static Builder builder(Item item, WeaponCategory category, float critical, float spellBuff, float weight)
	{
		return new Builder(item, category, critical, spellBuff, weight);
	}
	
	public static class Builder
	{
		private Item item;
		private WeaponCategory category;
		private float critical;
		private float spellBuff;
		private float weight;
		private ImmutableMap.Builder<CoreDamageType, Integer> damage = ImmutableMap.builder();
		private ImmutableSet.Builder<AuxEffect> auxEffects = ImmutableSet.builder();
		private ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		private ImmutableMap.Builder<Stat, Scaling> statScaling = ImmutableMap.builder();
		
		private Builder(Item item, WeaponCategory category, float critical, float spellBuff, float weight)
		{
			this.item = item;
			this.category = category;
			this.critical = critical;
			this.spellBuff = spellBuff;
			this.weight = weight;
		}
		
		public Builder addAuxEffect(AuxEffect auxEffect)
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
		
		public SpellcasterWeaponCap build()
		{
			return new SpellcasterWeaponCap(this.item, this.category, this.damage.build(), this.auxEffects.build(), this.critical, this.spellBuff, this.weight,
					this.statRequirements.build(), this.statScaling.build());
		}
	}
}
