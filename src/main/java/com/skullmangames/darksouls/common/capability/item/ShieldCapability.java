package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.core.init.Skills;

import net.minecraft.item.Item;

public class ShieldCapability extends WeaponCapability implements IShield
{
	public ShieldCapability(Item item)
	{
		super(item, WeaponCategory.SHIELD);
	}
	
	@Override
	public Skill getLightAttack()
	{
		return Skills.SHIELD_ATTACK;
	}
	
	public float getPhysicalDefense()
	{
		return 0.93F;
	}

	@Override
	public ShieldType getShieldType()
	{
		return ShieldType.NORMAL;
	}
}
