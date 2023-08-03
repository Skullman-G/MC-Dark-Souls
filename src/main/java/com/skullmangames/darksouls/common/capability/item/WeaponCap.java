package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
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
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.WeaponCategory;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class WeaponCap extends AttributeItemCap
{
	private final WeaponCategory weaponCategory;
	private final ImmutableMap<CoreDamageType, Integer> damage;
	protected final Map<LivingMotion, StaticAnimation> twoHandingOverrides = new HashMap<>();
	protected final Map<LivingMotion, StaticAnimation> animationOverrides = new HashMap<>();
	private final ImmutableMap<Stat, Integer> statRequirements;
	private final ImmutableMap<Stat, Scaling> statScaling;
	private final ImmutableSet<AuxEffect> auxEffects;
	private final float weight;
	private final float critical;

	public WeaponCap(Item item, WeaponCategory category, ImmutableMap<CoreDamageType, Integer> damage, ImmutableSet<AuxEffect> auxEffects,
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
		
		this.twoHandingOverrides.put(LivingMotion.IDLE, Animations.BIPED_IDLE_TH);
		this.twoHandingOverrides.put(LivingMotion.WALKING, Animations.BIPED_WALK_TH);
		this.twoHandingOverrides.put(LivingMotion.RUNNING, Animations.BIPED_RUN_TH);
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
		Stats.changeWeaponScalingAttributes(playerCap.getOriginalEntity());
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
		return this.statRequirements.getOrDefault(stat, 0) <= playerCap.getStatValue(stat);
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
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.STRENGTH, playerCap)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.DEXTERITY, playerCap)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.INTELLIGENCE.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.INTELLIGENCE, playerCap)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.FAITH.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.FAITH, playerCap)));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Scaling:"));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.getScaling(Stats.STRENGTH)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.getScaling(Stats.DEXTERITY)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.FAITH.toString()).getString() + ": "
					+ this.getScaling(Stats.FAITH)));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent("attribute.darksouls.weight").withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+this.weight)));
		}
	}

	private String getStatStringValue(Stat stat, PlayerCap<?> playerCap)
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
}
