package com.skullmangames.darksouls.common.entity.stats;

import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ScalingStat extends ModifyingStat
{
	public ScalingStat(String name, String uuid)
	{
		super(name, uuid, () -> Attributes.ATTACK_DAMAGE);
	}

	@Override
	public double getModifyValue(Player player, int value)
	{
		float addition = 0F;
		MeleeWeaponCap weapon = ModCapabilities.getMeleeWeaponCapability(player.getMainHandItem());
		if (weapon != null)
		{
			float weaponBonus = weapon.getScaling(this).getPercentage();
			float scale = -0.000127F * (value - 10F) * (value - 188F);
			addition = weapon.getDamage() * weaponBonus * scale;
		}
		return addition;
	}
}
