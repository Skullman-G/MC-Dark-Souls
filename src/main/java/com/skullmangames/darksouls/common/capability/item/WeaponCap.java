package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AuxEffects;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.JsonBuilder;
import com.skullmangames.darksouls.core.util.WeaponCategory;
import com.skullmangames.darksouls.core.util.WeaponSkill;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class WeaponCap extends AttributeItemCap
{
	private final WeaponCategory weaponCategory;
	private final ImmutableMap<CoreDamageType, Integer> damage;
	protected final Map<LivingMotion, StaticAnimation> twoHandingOverrides = new HashMap<>();
	protected final Map<LivingMotion, StaticAnimation> animationOverrides = new HashMap<>();
	private final ImmutableMap<Stat, Integer> statRequirements;
	private final ImmutableMap<Stat, Scaling> statScaling;
	private final ImmutableSet<AuxEffect> auxEffects;
	public final float weight;
	private final float critical;
	private final WeaponSkill skill;

	public WeaponCap(Item item, WeaponCategory category, WeaponSkill skill, ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects,
			float critical, float weight,
			ImmutableMap<Stat, Integer> statRequirements, ImmutableMap<Stat, Scaling> statScaling)
	{
		super(item);
		this.weaponCategory = category;
		this.damage = damage;
		this.auxEffects = auxEffects;
		this.critical = critical;
		this.statRequirements = statRequirements;
		this.statScaling = statScaling;
		this.weight = weight;
		this.skill = skill;
		
		this.twoHandingOverrides.put(LivingMotion.IDLE, Animations.BIPED_IDLE_TH);
		this.twoHandingOverrides.put(LivingMotion.WALKING, Animations.BIPED_WALK_TH);
		this.twoHandingOverrides.put(LivingMotion.RUNNING, Animations.BIPED_RUN_TH);
	}
	
	public void performSkill(LivingCap<?> cap, InteractionHand hand)
	{
		this.skill.perform(cap, hand);
	}
	
	public boolean hasSkill()
	{
		return this.skill != null;
	}
	
	public Set<AuxEffect> getAuxEffects()
	{
		return this.auxEffects;
	}
	
	public float getCritical()
	{
		return this.critical;
	}
	
	@Override
	public void onHeld(PlayerCap<?> playerCap)
	{
		super.onHeld(playerCap);
		Stats.changeWeaponScalingAttributes(playerCap);
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(slot, this.weight));
		
		if (slot == EquipmentSlot.MAINHAND)
		{
			map.put(Attributes.ATTACK_DAMAGE, ModAttributes.getAttributeModifierForSlot(slot, this.getDamage(CoreDamageType.PHYSICAL) - ModAttributes.PLAYER_FIST_DAMAGE));
			map.put(ModAttributes.MAGIC_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.getDamage(CoreDamageType.MAGIC)));
			map.put(ModAttributes.FIRE_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.getDamage(CoreDamageType.FIRE)));
			map.put(ModAttributes.LIGHTNING_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.getDamage(CoreDamageType.LIGHTNING)));
			map.put(ModAttributes.HOLY_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.getDamage(CoreDamageType.HOLY)));
			map.put(ModAttributes.DARK_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.getDamage(CoreDamageType.DARK)));
		}
		return map;
	}
	
	public Scaling getScaling(Stat stat)
	{
		return this.statScaling.get(stat);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void performAttack(AttackType type, LocalPlayerCap playerCap) {}

	public boolean meetRequirements(PlayerCap<?> playerCap)
	{
		for (Stat stat : this.statRequirements.keySet())
			if (!this.meetsRequirement(stat, playerCap))
				return false;
		return true;
	}

	public boolean meetsRequirement(Stat stat, PlayerCap<?> playerCap)
	{
		int statValue = stat == Stats.STRENGTH && playerCap.isTwohanding() ? (int)(playerCap.getStatValue(stat) * 1.5F)
				: playerCap.getStatValue(stat);
		return this.statRequirements.getOrDefault(stat, 0) <= statValue;
	}
	
	public int getDamage(CoreDamageType type)
	{
		return this.damage.getOrDefault(type, 0);
	}

	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		ResourceLocation id = this.orgItem.getRegistryName();
		if (id == null) return;

		while (itemTooltip.size() >= 2) itemTooltip.remove(1);

		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
					+ id.getPath() + ".extended";
			String description = new TranslatableComponent(languagePath).getString();

			if (!description.contains(languagePath))
				itemTooltip.add(new TextComponent("\u00A77\n" + description));
		}
		else
		{
			itemTooltip.add(new TextComponent("\u00A77Physical Damage: " + this.getDamage(CoreDamageType.PHYSICAL)));
			itemTooltip.add(new TextComponent("\u00A73Magic Damage: " + this.getDamage(CoreDamageType.MAGIC)));
			itemTooltip.add(new TextComponent("\u00A7cFire Damage: " + this.getDamage(CoreDamageType.FIRE)));
			itemTooltip.add(new TextComponent("\u00A7eLightning Damage: " + this.getDamage(CoreDamageType.LIGHTNING)));
			itemTooltip.add(new TextComponent("\u00A76Holy Damage: " + this.getDamage(CoreDamageType.HOLY)));
			itemTooltip.add(new TextComponent("\u00A75Dark Damage: " + this.getDamage(CoreDamageType.DARK)));

			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Requirements:"));
			for (Stat stat : Stats.SCALING_STATS)
			{
				itemTooltip.add(new TextComponent("  " + new TranslatableComponent(stat.toString()).getString() + ": "
						+ this.getStatStringValue(stat, playerCap)));
			}
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Scaling:"));
			for (Stat stat : Stats.SCALING_STATS)
			{
				itemTooltip.add(new TextComponent("  " + new TranslatableComponent(stat.toString()).getString() + ": "
						+ this.getScaling(stat)));
			}
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent("attribute.darksouls.weight").withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+this.weight)));
		}
	}

	public String getStatStringValue(Stat stat, PlayerCap<?> playerCap)
	{
		return this.getStatColor(stat, playerCap) + this.statRequirements.get(stat);
	}

	private String getStatColor(Stat stat, PlayerCap<?> playerCap)
	{
		return this.meetsRequirement(stat, playerCap) ? "\u00A7f" : "\u00A74";
	}

	public WeaponCategory getWeaponCategory()
	{
		return this.weaponCategory;
	}

	@Override
	public boolean canUsedInOffhand()
	{
		return this.getHandProperty() == HandProperty.GENERAL ? true : false;
	}

	@Override
	public boolean isTwoHanded()
	{
		return this.getHandProperty() == HandProperty.TWO_HANDED;
	}

	public final boolean isMainhandOnly()
	{
		return this.getHandProperty() == HandProperty.MAINHAND_ONLY;
	}

	public HandProperty getHandProperty()
	{
		return HandProperty.GENERAL;
	}

	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(LivingCap<?> cap)
	{
		return cap.isTwohanding() ? this.twoHandingOverrides : this.animationOverrides;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return !isTwoHanded() && !item.isEmpty();
	}

	public enum HandProperty
	{
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum Scaling
	{
		S("S", 1.5F), A("A", 1F), B("B", 0.8F), C("C", 0.5F), D("D", 0.3F), E("E", 0.1F), NONE("-", 0F);
		
		private final String name;
		private final float percentage;
		
		private Scaling(String name, float per)
		{
			this.name = name;
			this.percentage = per;
		}
		
		public float getPercentage()
		{
			return this.percentage;
		}
		
		@Override
		public String toString()
		{
			return this.name;
		}
		
		public static Scaling fromString(String id)
		{
			for (Scaling scaling : Scaling.values())
			{
				if (scaling.name.equals(id)) return scaling;
			}
			return null;
		}
	}
	
	public static abstract class Builder<T extends WeaponCap> implements JsonBuilder<T>
	{
		protected Item item;
		protected WeaponCategory category;
		protected ResourceLocation skillId;
		protected ImmutableMap.Builder<CoreDamageType, Integer> damage = ImmutableMap.builder();
		protected ImmutableSet.Builder<AuxEffect> auxEffects = ImmutableSet.builder();
		protected float critical = 1.00F;
		protected float weight;
		protected ImmutableMap.Builder<Stat, Integer> statRequirements = ImmutableMap.builder();
		protected ImmutableMap.Builder<Stat, Scaling> statScaling = ImmutableMap.builder();
		
		protected Builder() {}
		
		protected Builder(Item item, WeaponCategory category, float weight)
		{
			this.item = item;
			this.category = category;
			this.skillId = new ResourceLocation("empty");
			this.weight = weight;
		}
		
		public ResourceLocation getId()
		{
			return this.item.getRegistryName();
		}
		
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("registry_name", this.item.getRegistryName().toString());
			json.addProperty("category", this.category.toString());
			json.addProperty("skill", this.skillId.toString());
			json.addProperty("weight", this.weight);
			json.addProperty("critical", this.critical);
			
			JsonObject damage = new JsonObject();
			json.add("damage", damage);
			this.damage.build().forEach((type, dam) ->
			{
				damage.addProperty(type.toString(), dam);
			});
			
			JsonArray auxEffects = new JsonArray();
			json.add("aux_effects", auxEffects);
			this.auxEffects.build().forEach((auxEffect) ->
			{
				auxEffects.add(auxEffect.toString());
			});
			
			JsonObject statRequirements = new JsonObject();
			json.add("stat_requirements", statRequirements);
			this.statRequirements.build().forEach((stat, req) ->
			{
				statRequirements.addProperty(stat.getName(), req);
			});
			
			JsonObject statScaling = new JsonObject();
			json.add("stat_scaling", statScaling);
			this.statScaling.build().forEach((stat, scaling) ->
			{
				statScaling.addProperty(stat.getName(), scaling.toString());
			});
			return json;
		}
		
		public void initFromJson(ResourceLocation location, JsonObject json)
		{
			ResourceLocation itemId = ResourceLocation.tryParse(json.get("registry_name").getAsString());
			this.item = ForgeRegistries.ITEMS.getValue(itemId);
			
			this.category = WeaponCategory.fromString(json.get("category").getAsString());
			
			this.weight = json.get("weight").getAsFloat();
			
			JsonElement skillJson = json.get("skill");
			if (skillJson != null) this.skillId = new ResourceLocation(skillJson.getAsString());
			
			JsonElement criticalJson = json.get("critical");
			if (criticalJson != null) this.critical = criticalJson.getAsFloat();
			
			JsonObject statRequirements = json.get("stat_requirements").getAsJsonObject();
			JsonObject statScaling = json.get("stat_scaling").getAsJsonObject();
			for (Stat stat : Stats.SCALING_STATS)
			{
				int requirement = statRequirements.get(stat.getName()).getAsInt();
				String statName = statScaling.get(stat.getName()).getAsString();
				Scaling scaling = Scaling.fromString(statName);
				if (scaling == null)
				{
					DarkSouls.LOGGER.error("Error while reading weapon config for "+location+". Could not find scaling for "+stat.getName()+" with name "+statName);
					continue;
				}
				this.statRequirements.put(stat, requirement);
				this.statScaling.put(stat, scaling);
			}
			
			JsonObject damage = json.get("damage").getAsJsonObject();
			for (CoreDamageType type : CoreDamageType.values())
			{
				int dam = Optional.ofNullable(damage.get(type.toString())).orElse(new JsonPrimitive(0)).getAsInt();
				this.damage.put(type, dam);
			}
			
			JsonElement auxEffects = json.get("aux_effects");
			if (auxEffects != null)
			{
				for (JsonElement auxEffect : auxEffects.getAsJsonArray())
				{
					this.auxEffects.add(AuxEffects.fromId(ResourceLocation.tryParse(auxEffect.getAsString())));
				}
			}
		}
	}
}
