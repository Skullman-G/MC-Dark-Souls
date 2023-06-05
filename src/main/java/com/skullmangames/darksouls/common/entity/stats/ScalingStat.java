package com.skullmangames.darksouls.common.entity.stats;

import java.util.List;
import java.util.function.Supplier;

import com.skullmangames.darksouls.core.init.ModAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public abstract class ScalingStat extends Stat
{
	public ScalingStat(String name, String uuid, AttributeList attributes)
	{
		super(name, uuid, attributes);
	}
	
	@Override
	protected void modifyAttribute(Player player, Attribute attribute, int value)
	{
		List<Supplier<Attribute>> dmgAttributes = ModAttributes.damageAttributes();
		for (Supplier<Attribute> s : dmgAttributes)
		{
			if (s.get() == attribute) return;
		}
		super.modifyAttribute(player, attribute, value);
	}
}
