package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import net.minecraft.world.item.Item;

public class ShieldCap extends MeleeWeaponCap
{
	public ShieldCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.SHIELD, requiredStrength, requiredDex, strengthScaling, dexScaling, 20F);
	}
	
	@Override
	public AttackAnimation[] getLightAttack()
	{
		return Animations.SHIELD_LIGHT_ATTACK;
	}
	
	@Override
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
