package com.skullmangames.darksouls.common.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.property.Property.DamageProperty;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ValueCorrector;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class HeavyAttackSkill extends AttackSkill
{
	public HeavyAttackSkill(String skillName)
	{
		this(0, skillName);
	}
	
	public HeavyAttackSkill(int duration, String skillName)
	{
		super(duration, true, skillName);
	}
	
	@Override
	public float getRegenTimePerTick(PlayerData<?> player)
	{
		return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap)
	{
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		
		list.add(new TranslationTextComponent("skill." + DarkSouls.MOD_ID + "." + this.registryName.getPath()).withStyle(TextFormatting.WHITE));
		list.add(new TranslationTextComponent("skill." + DarkSouls.MOD_ID + "." + this.registryName.getPath() + "_tooltip").withStyle(TextFormatting.DARK_GRAY));
		
		return list;
	}
	
	protected void generateTooltipforPhase(List<ITextComponent> list, ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap, Map<DamageProperty<?>, Object> propertyMap, String title)
	{
		Multimap<Attribute, AttributeModifier> attributes = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
		Multimap<Attribute, AttributeModifier> capAttributes = cap.getAttributeModifiers(EquipmentSlotType.MAINHAND, playerCap);
		double damage = playerCap.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
		double armorNegation = playerCap.getOriginalEntity().getAttribute(AttributeInit.ARMOR_NEGATION.get()).getBaseValue();
		double impact = playerCap.getOriginalEntity().getAttribute(AttributeInit.IMPACT.get()).getBaseValue();
		double maxStrikes = playerCap.getOriginalEntity().getAttribute(AttributeInit.MAX_STRIKES.get()).getBaseValue();
		ValueCorrector damageCorrector = ValueCorrector.base();
		ValueCorrector armorNegationCorrector = ValueCorrector.base();
		ValueCorrector impactCorrector = ValueCorrector.base();
		ValueCorrector maxStrikesCorrector = ValueCorrector.base();
		
		for (AttributeModifier modifier : attributes.get(Attributes.ATTACK_DAMAGE))
		{
			damage += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(AttributeInit.ARMOR_NEGATION.get()))
		{
			armorNegation += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(AttributeInit.IMPACT.get()))
		{
			impact += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(AttributeInit.MAX_STRIKES.get()))
		{
			maxStrikes += modifier.getAmount();
		}
		
		this.getProperty(DamageProperty.DAMAGE, propertyMap).ifPresent(damageCorrector::merge);
		this.getProperty(DamageProperty.ARMOR_NEGATION, propertyMap).ifPresent(armorNegationCorrector::merge);
		this.getProperty(DamageProperty.IMPACT, propertyMap).ifPresent(impactCorrector::merge);
		this.getProperty(DamageProperty.MAX_STRIKES, propertyMap).ifPresent(maxStrikesCorrector::merge);
		
		damage = damageCorrector.get((float)damage);
		armorNegation = armorNegationCorrector.get((float)armorNegation);
		impact = impactCorrector.get((float)impact);
		maxStrikes = maxStrikesCorrector.get((float)maxStrikes);
		
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(title).withStyle(TextFormatting.UNDERLINE).withStyle(TextFormatting.GRAY));
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(TextFormatting.RED + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage) + TextFormatting.DARK_GRAY + " Damage"));
		
		if (armorNegation != 0.0D)
		{
			list.add(new StringTextComponent(TextFormatting.GOLD + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(armorNegation) + TextFormatting.DARK_GRAY + " Armor negation"));
		}
		if (impact != 0.0D)
		{
			list.add(new StringTextComponent(TextFormatting.AQUA + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(impact) + TextFormatting.DARK_GRAY + " Impact"));
		}
		
		list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Hit " + TextFormatting.WHITE + ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxStrikes) + TextFormatting.DARK_GRAY + " Enemies per Swing"));
		
		Optional<StunType> stunOption = this.getProperty(DamageProperty.STUN_TYPE, propertyMap);
		
		stunOption.ifPresent((stunType) ->
		{
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Apply " + stunType.toString()));
		});
		if (!stunOption.isPresent())
		{
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Apply " + StunType.SHORT.toString()));
		}	
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer)
	{
		WeaponCapability item = executer.getHeldWeaponCapability(Hand.MAIN_HAND);
		if (item == null) return false;
		if (this != item.getHeavyAttack(executer.getOriginalEntity())) return false;
		
		return super.canExecute(executer);
	}
}