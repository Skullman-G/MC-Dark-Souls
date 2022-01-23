package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class AttributeItemCapability extends ItemCapability
{
	protected final Map<Supplier<Attribute>, AttributeModifier> attributeMap = new HashMap<Supplier<Attribute>, AttributeModifier>();
	
	public AttributeItemCapability(Item item)
	{
		super(item);
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, LivingData<?> entitydata)
	{
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		return map;
    }
	
	public void addAttribute(Supplier<Attribute> attribute, AttributeModifier modifier)
	{
		this.attributeMap.put(attribute, modifier);
	}
	
	protected void registerAttribute() {}
}
