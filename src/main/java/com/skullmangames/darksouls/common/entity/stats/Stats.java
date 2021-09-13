package com.skullmangames.darksouls.common.entity.stats;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;

public class Stats
{
	private static List<Stat> stats = new ArrayList<Stat>();
	
	public static final Stat VIGOR = register(new Stat("vigor", "35031b47-45fa-401b-92dc-12b6d258e553")
		{
			@Override		
			public void onChange(LivingEntity livingentity, boolean isinit, int value)
			{
				ModifiableAttributeInstance attribute = livingentity.getAttribute(Attributes.MAX_HEALTH);
				if (attribute.getModifier(this.getModifierUUID()) != null)
				{
					attribute.removeModifier(this.getModifierUUID());
				}
				AttributeModifier modifier = new AttributeModifier(this.getModifierUUID(), "change_with_vigor", value - 1, Operation.ADDITION);
				attribute.addPermanentModifier(modifier);
				if (!isinit) livingentity.setHealth(livingentity.getMaxHealth());
			}
		});
	
	public static final Stat STRENGTH = register(new Stat("strength", "e3d34cb6-3708-462b-8598-c9a124604de6"));
	
	private static Stat register(Stat stat)
	{
		stats.add(stat);
		return stat;
	}
	
	public static List<Stat> getStats()
	{
		return stats;
	}
	
	public static int getLevel(LivingEntity livingentity)
	{
		int level = 1;
		for (Stat stat : getStats())
		{
			level += stat.getValue(livingentity) - 1;
		}
		
		return level;
	}
}
