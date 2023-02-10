package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;

public class AttributeItemCap extends ItemCapability
{
	public AttributeItemCap(Item item)
	{
		super(item);
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot)
	{
		return HashMultimap.<Attribute, AttributeModifier>create();
    }
}
