package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import net.minecraft.world.item.Item;

public class ShieldCap extends MeleeWeaponCap
{
	private final float physicalDefense;
	
	public ShieldCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling, float physicalDefense)
	{
		super(item, WeaponCategory.SHIELD, requiredStrength, requiredDex, strengthScaling, dexScaling, 20F);
		this.physicalDefense = Math.min(physicalDefense, 1F);
	}
	
	@Override
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.SHIELD_LIGHT_ATTACK);
		return builder;
	}
	
	@Override
	public float getPhysicalDefense()
	{
		return this.physicalDefense;
	}

	@Override
	public ShieldType getShieldType()
	{
		return ShieldType.NORMAL;
	}
}
