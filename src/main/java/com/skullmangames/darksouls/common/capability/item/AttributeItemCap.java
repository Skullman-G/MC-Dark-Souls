package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class AttributeItemCap extends ItemCapability
{
	public AttributeItemCap(Item item)
	{
		super(item);
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		return HashMultimap.<Attribute, AttributeModifier>create();
    }
}
