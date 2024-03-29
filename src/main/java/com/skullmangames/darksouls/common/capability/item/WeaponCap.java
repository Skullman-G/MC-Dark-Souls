package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class WeaponCap extends AttributeItemCap
{
	private final WeaponCategory weaponCategory;
	protected final Map<LivingMotion, StaticAnimation> animationSet = new HashMap<>();
	private final Map<Stat, Pair<Integer, Scaling>> statInfo;
	public final float poiseDamage;
	public final float weight;

	public WeaponCap(Item item, WeaponCategory category, int reqStrength, int reqDex, int reqFaith,
			Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item);
		this.weaponCategory = category;
		this.statInfo = ImmutableMap.<Stat, Pair<Integer, Scaling>>builder()
				.put(Stats.STRENGTH, new Pair<Integer, Scaling>(MathUtils.clamp(reqStrength, 0, 99), strengthScaling))
				.put(Stats.DEXTERITY, new Pair<Integer, Scaling>(MathUtils.clamp(reqDex, 0, 99), dexScaling))
				.put(Stats.FAITH, new Pair<Integer, Scaling>(MathUtils.clamp(reqFaith, 0, 99), faithScaling))
				.build();
		this.weight = Math.max(((float)reqStrength - 4F) / 2F, 0F);
		this.poiseDamage = this.weight * 15;
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(slot, this.weight));
		if (slot == EquipmentSlot.MAINHAND) map.put(ModAttributes.POISE_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.poiseDamage));
		return map;
	}
	
	public abstract float getStaminaDamage();
	
	public Scaling getScaling(Stat stat)
	{
		return this.statInfo.get(stat).getSecond();
	}
	
	@OnlyIn(Dist.CLIENT)
	public void performAttack(AttackType type, LocalPlayerCap playerCap) {}

	public boolean meetRequirements(PlayerCap<?> playerCap)
	{
		for (Stat stat : this.statInfo.keySet())
			if (!this.meetsRequirement(stat, playerCap))
				return false;
		return true;
	}

	public boolean meetsRequirement(Stat stat, PlayerCap<?> playerCap)
	{
		return this.statInfo.get(stat).getFirst() <= playerCap.getStats().getStatValue(stat);
	}

	@Nullable
	public boolean hasHoldingAnimation()
	{
		return false;
	}
	
	public abstract float getDamage();

	@Override
	public void modifyItemTooltip(List<Component> itemTooltip, PlayerCap<?> playerCap, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;

		while (itemTooltip.size() >= 2) itemTooltip.remove(1);

		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
					+ ((IForgeRegistryEntry<Item>) this.orgItem).getRegistryName().getPath() + ".extended";
			String description = new TranslatableComponent(languagePath).getString();

			if (!description.contains(languagePath))
				itemTooltip.add(new TextComponent("\u00A77\n" + description));
		}
		else
		{
			itemTooltip.add(new TextComponent("\u00A72Physical Damage: " + this.getDamage()));

			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Requirements:"));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.STRENGTH, playerCap)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.DEXTERITY, playerCap)));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.FAITH.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.FAITH, playerCap)));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TextComponent("Scaling:"));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.statInfo.get(Stats.STRENGTH).getSecond()));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.statInfo.get(Stats.DEXTERITY).getSecond()));
			itemTooltip.add(new TextComponent("  " + new TranslatableComponent(Stats.FAITH.toString()).getString() + ": "
					+ this.statInfo.get(Stats.FAITH).getSecond()));
			
			itemTooltip.add(new TextComponent(""));
			itemTooltip.add(new TranslatableComponent("attribute.darksouls.weight").withStyle(ChatFormatting.BLUE)
					.append(new TextComponent(ChatFormatting.BLUE+": "+MathUtils.round(this.weight, 2))));
		}
	}

	private String getStatStringValue(Stat stat, PlayerCap<?> playerCap)
	{
		return this.getStatColor(stat, playerCap) + this.statInfo.get(stat).getFirst();
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

	@Override
	public boolean canUseOnMount()
	{
		return !this.isTwoHanded();
	}

	public HandProperty getHandProperty()
	{
		return HandProperty.GENERAL;
	}

	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerCap<?> playerCap)
	{
		return this.animationSet;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return !isTwoHanded() && !item.isEmpty();
	}

	public enum WeaponCategory
	{
		NONE_WEAON, MELEE_WEAPON, BOW, CROSSBOW, TALISMAN
	}

	public enum HandProperty
	{
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}
	
	public enum Scaling
	{
		S(1.5F), A(1F), B(0.8F), C(0.5F), D(0.3F), E(0.1F), NONE(0F);
		
		private final float percentage;
		
		private Scaling(float per)
		{
			this.percentage = per;
		}
		
		public float getPercentage()
		{
			return this.percentage;
		}
		
		@Override
		public String toString()
		{
			switch (this)
			{
				case S: return "S";
				case A: return "A";
				case B: return "B";
				case C: return "C";
				case D: return "D";
				case E: return "E";
				default: return "-";
			}
		}
	}
}
