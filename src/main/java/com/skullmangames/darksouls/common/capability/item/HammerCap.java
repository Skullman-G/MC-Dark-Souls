package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.world.item.Item;

public class HammerCap extends MeleeWeaponCap
{
	public HammerCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling,
			Scaling dexScaling)
	{
		super(item, WeaponCategory.HAMMER, requiredStrength, requiredDex, strengthScaling, dexScaling, 35F);
	}
	
	@Override
	public AttackAnimation[] getLightAttack()
	{
		return Animations.HAMMER_LIGHT_ATTACK;
	}
	
	@Override
	public AttackAnimation getHeavyAttack()
	{
		return Animations.HAMMER_HEAVY_ATTACK;
	}
	
	@Override
	public AttackAnimation getDashAttack()
	{
		return Animations.HAMMER_DASH_ATTACK;
	}
}
