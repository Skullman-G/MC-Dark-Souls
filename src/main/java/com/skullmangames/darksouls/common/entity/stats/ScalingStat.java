package com.skullmangames.darksouls.common.entity.stats;

import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;

public class ScalingStat extends ModifyingStat
{
	public ScalingStat(String name, String uuid)
	{
		super(name, uuid, () -> Attributes.ATTACK_DAMAGE);
	}

	@Override
	public double getModifyValue(PlayerEntity player, Attribute attribute, int value)
	{
		float addition = 0F;
		MeleeWeaponCap weapon = ModCapabilities.getMeleeWeaponCap(player.getMainHandItem());
		if (weapon != null)
		{
			float weaponBonus = weapon.getScaling(this).getPercentage();
			float scale = -0.000127F * (value - Stats.STANDARD_LEVEL) * (value - 188F);
			addition = weapon.getDamage() * weaponBonus * scale;
		}
		return addition;
	}
	
	public double getModifyValue(PlayerEntity player, Attribute attribute, float baseDamage, int value)
	{
		float addition = 0F;
		MeleeWeaponCap weapon = ModCapabilities.getMeleeWeaponCap(player.getMainHandItem());
		if (weapon != null)
		{
			float weaponBonus = weapon.getScaling(this).getPercentage();
			float scale = -0.000127F * (value - Stats.STANDARD_LEVEL) * (value - 188F);
			addition = baseDamage * weaponBonus * scale;
		}
		return addition;
	}
}
