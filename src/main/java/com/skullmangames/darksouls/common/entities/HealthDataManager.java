package com.skullmangames.darksouls.common.entities;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;

public class HealthDataManager
{
	private static final Map<EntityType<? extends LivingEntity>, Integer> baseHealths = (ImmutableMap.<EntityType<? extends LivingEntity>, Integer>builder()
			.put(EntityType.PLAYER, 400).build());
	
	public static void initMaxHealth(LivingEntity livingentity)
	{
		if (livingentity.getPersistentData().getInt("ModdedMaxHealth") == 0)
		{
			setMaxHealth(livingentity, baseHealths.get(livingentity.getType()) != null ? baseHealths.get(livingentity.getType()).intValue() : 400);
		}
		else
		{
			setMaxHealth(livingentity, livingentity.getPersistentData().getInt("ModdedMaxHealth"));
		}
	}
	
	private static void setMaxHealth(LivingEntity livingentity, int value)
	{
		double currentvalue = livingentity.getAttributeValue(Attributes.MAX_HEALTH);
		if (currentvalue != value)
		{
			AttributeModifier modifier = new AttributeModifier("set max health", value - currentvalue, Operation.ADDITION);
			livingentity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(modifier);
			livingentity.getPersistentData().putInt("ModdedMaxHealth", value);
			livingentity.setHealth(value);
		}
	}
	
	public double getMaxHealth(LivingEntity livingentity)
	{
		return livingentity.getAttributeValue(Attributes.MAX_HEALTH);
	}
}
