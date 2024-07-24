package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class RangedWeaponCap extends WeaponCap
{
	private final HandProperty handProperty;
	
	public RangedWeaponCap(Item item, WeaponCategory category, HandProperty handProperty, Map<LivingMotion, StaticAnimation> animOverrides,
			ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects, float critical,
			 float weight, ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item, category, null, damage, auxEffects, critical, weight, statRequirements, statScaling);
		this.animationOverrides.putAll(animOverrides);
		this.handProperty = handProperty;
	}
	
	@Override
	public HandProperty getHandProperty()
	{
		return this.handProperty;
	}
	
	public static Builder builder(Item item, WeaponCategory category, float critical, float weight, HandProperty handProperty)
	{
		return new Builder(item, category, critical, weight, handProperty);
	}
	
	public static class Builder extends WeaponCap.Builder<RangedWeaponCap>
	{
		private Map<LivingMotion, StaticAnimation> animOverrides = new HashMap<>();
		private HandProperty handProperty;
		
		private Builder() {}
		
		private Builder(Item item, WeaponCategory category, float critical, float weight, HandProperty handProperty)
		{
			super(item, category, weight);
			this.handProperty = handProperty;
			this.critical = critical;
		}
		
		public Builder putAnimOverride(LivingMotion motion, StaticAnimation anim)
		{
			this.animOverrides.put(motion, anim);
			return this;
		}
		
		public Builder setSkill(ResourceLocation id)
		{
			this.skillId = id;
			return this;
		}
		
		public Builder addAuxEffect(AuxEffect auxEffect)
		{
			this.auxEffects.add(auxEffect);
			return this;
		}
		
		public Builder putDamage(CoreDamageType damageType, int damage)
		{
			this.damage.put(damageType, damage);
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
			
			JsonObject jsonAnimOverrides = new JsonObject();
			json.add("animation_overrides", jsonAnimOverrides);
			this.animOverrides.forEach((motion, anim) ->
			{
				jsonAnimOverrides.addProperty(motion.name(), anim.getId().toString());
			});
			
			json.addProperty("hand_property", this.handProperty.name());
			
			return json;
		}
		
		@Override
		public void initFromJson(ResourceLocation location, JsonObject json)
		{
			super.initFromJson(location, json);
			
			JsonObject jsonAnimOverrides = json.get("animation_overrides").getAsJsonObject();
			jsonAnimOverrides.entrySet().forEach((entry) ->
			{
				this.animOverrides.put(LivingMotion.valueOf(entry.getKey()), AnimationManager.getAnimation(new ResourceLocation(entry.getValue().getAsString())));
			});
			
			this.handProperty = HandProperty.valueOf(json.get("hand_property").getAsString());
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			Builder builder = new Builder();
			builder.initFromJson(location, json);
			return builder;
		}
		
		@Override
		public RangedWeaponCap build()
		{
			return new RangedWeaponCap(this.item, this.category, this.handProperty, this.animOverrides, this.damage.build(),
					this.auxEffects.build(), this.critical, this.weight, this.statRequirements.build(), this.statScaling.build());
		}
	}
}
