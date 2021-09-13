package com.skullmangames.darksouls.common.capability.item;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.client.input.ModKeys;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability.WieldStyle;
import com.skullmangames.darksouls.core.init.AttributeInit;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CapabilityItem
{
	protected final Item orgItem;
	protected Map<WieldStyle, Map<Supplier<Attribute>, AttributeModifier>> attributeMap;
	
	public CapabilityItem(Item item)
	{
		this.orgItem = item;
		this.attributeMap = Maps.<WieldStyle, Map<Supplier<Attribute>, AttributeModifier>>newHashMap();
	}
	
	public boolean isTwoHanded()
	{
		return false;
	}
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player)
	{
		return null;
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata)
	{
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		return map;
    }
	
	public void addStyleAttibute(WieldStyle style, Pair<Supplier<Attribute>, AttributeModifier> attributePair)
	{
		this.attributeMap.computeIfAbsent(style, (key) -> Maps.<Supplier<Attribute>, AttributeModifier>newHashMap());
		this.attributeMap.get(style).put(attributePair.getFirst(), attributePair.getSecond());
	}
	
	public void addStyleAttributeSimple(WieldStyle style, double armorNegation, double impact, int maxStrikes)
	{
		this.addStyleAttibute(style, Pair.of(AttributeInit.ARMOR_NEGATION, AttributeInit.getArmorNegationModifier(armorNegation)));
		this.addStyleAttibute(style, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(impact)));
		this.addStyleAttibute(style, Pair.of(AttributeInit.MAX_STRIKES, AttributeInit.getMaxStrikesModifier(maxStrikes)));
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean canBeRenderedBoth(ItemStack item)
	{
		return !item.isEmpty();
	}
	
	public boolean canUsedInOffhand()
	{
		return true;
	}
	
	public boolean canUseOnMount()
	{
		return true;
	}

	protected void registerAttribute() {}
	
	public void modifyItemTooltip(List<ITextComponent> itemTooltip, LivingData<?> entitydata, ItemStack stack)
	{
		if (!(this.orgItem instanceof IForgeRegistryEntry)) return;
		
		String languagePath = "tooltip."+DarkSouls.MOD_ID+"."+((IForgeRegistryEntry<Item>)this.orgItem).getRegistryName().getPath();
		String description = new TranslationTextComponent(languagePath).getString();
		
		while (itemTooltip.size() >= 2) itemTooltip.remove(1);
		if (!description.contains(languagePath)) itemTooltip.add(1, new StringTextComponent("\u00A77" + description));
		
		if (!ClientEngine.INSTANCE.inputController.isKeyDown(ModKeys.SHOW_ITEM_INFO)) return;
		
		languagePath = "tooltip."+DarkSouls.MOD_ID+"."+((IForgeRegistryEntry<Item>)this.orgItem).getRegistryName().getPath()+".extended";
		description = new TranslationTextComponent(languagePath).getString();
		
		if (!description.contains(languagePath)) itemTooltip.add(2, new StringTextComponent("\u00A77\n" + description));
	}
	
	public void onHeld(PlayerData<?> playerdata) {}
}