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
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class WeaponCap extends AttributeItemCap
{
	private final WeaponCategory weaponCategory;
	protected final Map<LivingMotion, StaticAnimation> animationSet = new HashMap<LivingMotion, StaticAnimation>();
	private final Map<Stat, Pair<Integer, Scaling>> statInfo;
	public final float poiseDamage;
	public final float weight;

	public WeaponCap(Item item, WeaponCategory category, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling, float poiseDamage)
	{
		super(item);
		this.weaponCategory = category;
		this.statInfo = ImmutableMap.<Stat, Pair<Integer, Scaling>>builder()
				.put(Stats.STRENGTH, new Pair<Integer, Scaling>(MathUtils.clamp(requiredStrength, 0, 99), strengthScaling))
				.put(Stats.DEXTERITY, new Pair<Integer, Scaling>(MathUtils.clamp(requiredDex, 0, 99), dexScaling)).build();
		this.poiseDamage = poiseDamage;
		this.weight = Math.max(((float)requiredStrength - 4F) / 2F, 0F);
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot)
	{
		Multimap<Attribute, AttributeModifier> map = super.getAttributeModifiers(slot);
		map.put(ModAttributes.EQUIP_LOAD.get(), ModAttributes.getAttributeModifierForSlot(slot, this.weight));
		if (slot == EquipmentSlotType.MAINHAND) map.put(ModAttributes.POISE_DAMAGE.get(), ModAttributes.getAttributeModifierForSlot(slot, this.poiseDamage));
		return map;
	}
	
	public abstract float getStaminaDamage();
	
	public Scaling getScaling(Stat stat)
	{
		return this.statInfo.get(stat).getSecond();
	}

	public boolean meetRequirements(PlayerCap<?> playerdata)
	{
		for (Stat stat : this.statInfo.keySet())
			if (!this.meetsRequirement(stat, playerdata))
				return false;
		return true;
	}

	public boolean meetsRequirement(Stat stat, PlayerCap<?> playerdata)
	{
		return this.statInfo.get(stat).getFirst() <= playerdata.getStats().getStatValue(stat);
	}

	@Nullable
	public boolean hasHoldingAnimation()
	{
		return false;
	}
	
	public abstract float getDamage();

	@Override
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, PlayerCap<?> playerdata, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry))
			return;

		while (itemTooltip.size() >= 2)
			itemTooltip.remove(1);

		if (ClientManager.INSTANCE.inputManager.isKeyDown(ModKeys.SHOW_ITEM_INFO))
		{
			String languagePath = "tooltip." + DarkSouls.MOD_ID + "."
					+ ((IForgeRegistryEntry<Item>) this.orgItem).getRegistryName().getPath() + ".extended";
			String description = new TranslationTextComponent(languagePath).getString();

			if (!description.contains(languagePath))
				itemTooltip.add(new StringTextComponent("\u00A77\n" + description));
		}
    else
		{
			itemTooltip.add(new StringTextComponent("\u00A72Physical Damage: " + this.getDamage()));

			itemTooltip.add(new StringTextComponent(""));
			itemTooltip.add(new StringTextComponent("Requirements:"));
			itemTooltip.add(new StringTextComponent("  " + new TranslationTextComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.STRENGTH, playerdata)));
			itemTooltip.add(new StringTextComponent("  " + new TranslationTextComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.getStatStringValue(Stats.DEXTERITY, playerdata)));
			
			itemTooltip.add(new StringTextComponent(""));
			itemTooltip.add(new StringTextComponent("Scaling:"));
			itemTooltip.add(new StringTextComponent("  " + new TranslationTextComponent(Stats.STRENGTH.toString()).getString() + ": "
					+ this.statInfo.get(Stats.STRENGTH).getSecond()));
			itemTooltip.add(new StringTextComponent("  " + new TranslationTextComponent(Stats.DEXTERITY.toString()).getString() + ": "
					+ this.statInfo.get(Stats.DEXTERITY).getSecond()));
			
			itemTooltip.add(new StringTextComponent(""));
			itemTooltip.add(new TranslationTextComponent("attribute.darksouls.weight").withStyle(TextFormatting.BLUE)
					.append(new StringTextComponent(TextFormatting.BLUE+": "+MathUtils.round(this.weight, 100))));
		}
	}

	private String getStatStringValue(Stat stat, PlayerCap<?> playerdata)
	{
		return this.getStatColor(stat, playerdata) + this.statInfo.get(stat).getFirst();
	}

	private String getStatColor(Stat stat, PlayerCap<?> playerdata)
	{
		return this.meetsRequirement(stat, playerdata) ? "\u00A7f" : "\u00A74";
	}

	public WeaponCategory getWeaponCategory()
	{
		return this.weaponCategory;
	}

	public WieldStyle getStyle(LivingCap<?> entityCap)
	{
		if (this.isTwoHanded())
		{
			return WieldStyle.TWO_HAND;
		} else
		{
			if (this.isMainhandOnly())
			{
				return entityCap.getOriginalEntity().getMainHandItem().isEmpty() ? WieldStyle.TWO_HAND
						: WieldStyle.ONE_HAND;
			} else
			{
				return WieldStyle.ONE_HAND;
			}
		}
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
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerCap<?> playerdata)
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
		NONE_WEAON, AXE, FIST, STRAIGHT_SWORD, SHIELD, GREAT_HAMMER, DAGGER, HAMMER, SPEAR, ULTRA_GREATSWORD, BOW, CROSSBOW
	}

	public enum HandProperty
	{
		TWO_HANDED, MAINHAND_ONLY, GENERAL
	}

	public enum WieldStyle
	{
		ONE_HAND, TWO_HAND, SHEATH, MOUNT
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
