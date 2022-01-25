package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class AttributeItemCap extends ItemCapability
{
	protected final Map<Supplier<Attribute>, AttributeModifier> attributeMap = new HashMap<Supplier<Attribute>, AttributeModifier>();
	
	public AttributeItemCap(Item item)
	{
		super(item);
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, LivingData<?> entitydata)
	{
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		if (entitydata == null) return map;
		
		for (Entry<Supplier<Attribute>, AttributeModifier> entry : this.attributeMap.entrySet())
		{
			map.put(entry.getKey().get(), entry.getValue());
		}

		return map;
    }
	
	public void addAttribute(Supplier<Attribute> attribute, AttributeModifier modifier)
	{
		this.attributeMap.put(attribute, modifier);
	}
	
	protected void registerAttribute() {}
}
