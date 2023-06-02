package com.skullmangames.darksouls.common.entity.stats;

import com.skullmangames.darksouls.core.init.ModAttributes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public abstract class ScalingStat extends Stat
{
	public ScalingStat(String name, String uuid, AttributeList attributes)
	{
		super(name, uuid, attributes);
	}
	
	@Override
	public Operation getOperation(Attribute attribute)
	{
		if (attribute == ModAttributes.STANDARD_PROTECTION.get() || attribute == ModAttributes.STRIKE_PROTECTION.get()
				|| attribute == ModAttributes.SLASH_PROTECTION.get() || attribute == ModAttributes.THRUST_PROTECTION.get()
				|| attribute == ModAttributes.FIRE_PROTECTION.get() || attribute == ModAttributes.DARK_PROTECTION.get()
				|| attribute == ModAttributes.LIGHTNING_PROTECTION.get() || attribute == ModAttributes.MAGIC_PROTECTION.get()
				|| attribute == ModAttributes.HOLY_PROTECTION.get())
		{
			return Operation.ADDITION;
		}
		else return Operation.MULTIPLY_TOTAL;
	}
}
