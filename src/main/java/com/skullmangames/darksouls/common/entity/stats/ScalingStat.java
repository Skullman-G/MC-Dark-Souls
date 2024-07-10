package com.skullmangames.darksouls.common.entity.stats;

import java.util.List;
import java.util.function.Supplier;

import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attribute;

public abstract class ScalingStat extends Stat
{
	public ScalingStat(String name, String uuid, AttributeList attributes)
	{
		super(name, uuid, attributes);
	}
	
	protected double scalingPercentage(PlayerCap<?> playerCap, int value, double softCap1, double softCap2, double hardCap)
	{
		WeaponCap weapon = playerCap.getHeldWeaponCap(InteractionHand.MAIN_HAND);
		if (weapon == null) return 0D;
		double percentage = 0D;
		if (value <= 40) percentage = value * (softCap1 / 40D);
		else if (value <= 60) percentage = softCap1 + (value - 40) * ((softCap2 - softCap1) / 20D);
		else if (value <= 99) percentage = softCap2 +  (value - 60) * ((hardCap - softCap2) / 39D);
		percentage *= weapon.getScaling(this).getPercentage();
		if (!weapon.meetsRequirement(this, playerCap)) percentage -= 1F;
		return percentage;
	}
	
	@Override
	protected void modifyAttribute(PlayerCap<?> playerCap, Attribute attribute, int value)
	{
		List<Supplier<Attribute>> dmgAttributes = ModAttributes.damageAttributes();
		for (Supplier<Attribute> s : dmgAttributes)
		{
			if (s.get() == attribute) return;
		}
		super.modifyAttribute(playerCap, attribute, value);
	}
}
