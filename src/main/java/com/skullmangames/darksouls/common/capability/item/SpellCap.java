package com.skullmangames.darksouls.common.capability.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skullmangames.darksouls.common.animation.AnimationManager;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.StatHolder;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.util.JsonBuilder;
import com.skullmangames.darksouls.core.util.SpellType;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SpellCap extends ItemCapability
{
	private final SpellType spellType;
	private final float fpConsumption;
	private final StaticAnimation castingAnim;
	@Nullable private final StaticAnimation horsebackAnim;
	private final ImmutableMap<Stat, Integer> statRequirements;
	
	public SpellCap(Item item, SpellType spellType, float fpConsumption, StaticAnimation castingAnim, @Nullable StaticAnimation horsebackAnim,
			ImmutableMap<Stat, Integer> statRequirements)
	{
		super(item);
		this.spellType = spellType;
		this.fpConsumption = fpConsumption;
		this.castingAnim = castingAnim;
		this.horsebackAnim = horsebackAnim;
		this.statRequirements = statRequirements;
	}
	
	public SpellType getSpellType()
	{
		return this.spellType;
	}
	
	public float getFPConsumption()
	{
		return this.fpConsumption;
	}
	
	public StaticAnimation getCastingAnimation()
	{
		return this.castingAnim;
	}
	
	@Nullable
	public StaticAnimation getHorsebackAnimation()
	{
		return this.horsebackAnim;
	}
	
	public boolean meetsRequirements(StatHolder stats)
	{
		for (Stat stat : Stats.SPELL_REQUIREMENT_STATS)
		{
			if (stats.getStatValue(stat) < this.statRequirements.get(stat)) return false;
		}
		return true;
	}
	
	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		super.modifyItemTooltip(itemTooltip, playerCap, stack);
		
		itemTooltip.add(new TextComponent(""));
		itemTooltip.add(new TextComponent("Requirements:"));
		for (Stat stat : Stats.SPELL_REQUIREMENT_STATS)
		{
			int requirement = this.statRequirements.get(stat);
			String color = playerCap.getStats().getStatValue(stat) >= requirement ? "\u00A7f" : "\u00A74";
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(stat.toString()).getString() + ": "
					+ color + requirement));
		}
	}
	
	public static Builder builder(Item item, SpellType spellType, float fpConsumption, ResourceLocation castingAnimId)
	{
		return new Builder(item, spellType, fpConsumption, castingAnimId, null);
	}
	
	public static Builder builder(Item item, SpellType spellType, float fpConsumption, ResourceLocation castingAnimId, ResourceLocation horsebackAnimId)
	{
		return new Builder(item, spellType, fpConsumption, castingAnimId, horsebackAnimId);
	}
	
	public static class Builder implements JsonBuilder<SpellCap>
	{
		private Item item;
		private SpellType spellType;
		private float fpConsumption;
		private ResourceLocation castingAnimId;
		@Nullable private ResourceLocation horsebackAnimId;
		private ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		
		private Builder(Item item, SpellType spellType, float fpConsumption, ResourceLocation castingAnimId, @Nullable ResourceLocation horsebackAnimId)
		{
			this.item = item;
			this.spellType = spellType;
			this.fpConsumption = fpConsumption;
			this.castingAnimId = castingAnimId;
			this.horsebackAnimId = horsebackAnimId;
		}
		
		private Builder(ResourceLocation location, JsonObject json)
		{
			ResourceLocation itemId = ResourceLocation.tryParse(json.get("registry_name").getAsString());
			this.item = ForgeRegistries.ITEMS.getValue(itemId);
			
			this.spellType = SpellType.valueOf(json.get("spell_type").getAsString());
			this.fpConsumption = json.get("fp_consumption").getAsFloat();
			
			this.castingAnimId = ResourceLocation.tryParse(json.get("casting_animation").getAsString());
			
			JsonElement horsebackAnimJson = json.get("horseback_animation");
			if (horsebackAnimJson != null) this.horsebackAnimId = ResourceLocation.tryParse(horsebackAnimJson.getAsString());
			
			JsonObject statRequirementsJson = json.get("stat_requirements").getAsJsonObject();
			for (Stat stat : Stats.SPELL_REQUIREMENT_STATS)
			{
				this.statRequirements.put(stat, statRequirementsJson.get(stat.getName()).getAsInt());
			}
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.item.getRegistryName();
		}
		
		public Builder putStatReq(Stat stat, int value)
		{
			this.statRequirements.put(stat, value);
			return this;
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			
			json.addProperty("registry_name", this.item.getRegistryName().toString());
			json.addProperty("spell_type", this.spellType.name());
			json.addProperty("fp_consumption", this.fpConsumption);
			json.addProperty("casting_animation", this.castingAnimId.toString());
			if (this.horsebackAnimId != null) json.addProperty("horseback_animation", this.horsebackAnimId.toString());
			
			JsonObject statReqJson = new JsonObject();
			json.add("stat_requirements", statReqJson);
			this.statRequirements.build().forEach((stat, value) ->
			{
				statReqJson.addProperty(stat.toString(), value);
			});
			
			return json;
		}
		
		public static Builder fromJson(ResourceLocation location, JsonObject json)
		{
			return new Builder(location, json);
		}
		
		@Override
		public SpellCap build()
		{
			StaticAnimation castingAnim = AnimationManager.getAnimation(this.castingAnimId);
			StaticAnimation horsebackAnim = this.horsebackAnimId != null ? AnimationManager.getAnimation(this.horsebackAnimId) : null;
			return new SpellCap(this.item, this.spellType, this.fpConsumption, castingAnim, horsebackAnim, this.statRequirements.build());
		}
	}
}
