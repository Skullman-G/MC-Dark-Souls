package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSCastSpell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class SpellcastingWeaponCap extends WeaponCap
{
	private final float spellBuff;
	
	public SpellcastingWeaponCap(Item item, WeaponCategory category, ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects,
			float critical, float spellBuff, float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, null, damage, auxEffects, critical, weight, statRequirements, statScaling);
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
		SpellCap spell = (SpellCap)playerCap.getAttunements().getSelected().getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null);
		if (spell != null)
		{
			ModNetworkManager.sendToServer(new CTSCastSpell(spell));
		}
	}
	
	public static Builder builder(Item item, WeaponCategory category, float critical, float spellBuff, float weight)
	{
		return new Builder(item, category, critical, spellBuff, weight);
	}
	
	public static class Builder extends WeaponCap.Builder<SpellcastingWeaponCap>
	{
		private float spellBuff;
		
		private Builder(Item item, WeaponCategory category, float critical, float spellBuff, float weight)
		{
			super(item, category, weight);
			this.critical = critical;
			this.spellBuff = spellBuff;
		}
		
		private Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.spellBuff = json.get("spell_buff").getAsFloat();
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.item.getRegistryName();
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
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("spell_buff", this.spellBuff);
			return json;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return new Builder(location, json);
		}
		
		public SpellcastingWeaponCap build()
		{
			return new SpellcastingWeaponCap(this.item, this.category, this.damage.build(), this.auxEffects.build(), this.critical, this.spellBuff, this.weight,
					this.statRequirements.build(), this.statScaling.build());
		}
	}
}
