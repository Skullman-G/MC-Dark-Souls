package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;

public class UltraGreatswordCap extends MeleeWeaponCap
{
	public UltraGreatswordCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.ULTRA_GREATSWORD, requiredStrength, requiredDex, strengthScaling, dexScaling, 50);
	}
	
	@Override
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.ULTRA_GREATSWORD_LIGHT_ATTACK);
		this.putMove(builder, AttackType.HEAVY, false, Animations.ULTRA_GREATSWORD_HEAVY_ATTACK);
		this.putMove(builder, AttackType.DASH, false, Animations.ULTRA_GREATSWORD_DASH_ATTACK);
		return builder;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.ULTRA_GREATSWORD;
	}
	
	@Override
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return Animations.BIPED_HOLDING_BIG_WEAPON;
	}
	
	@Override
	protected AttackAnimation getWeakAttack()
	{
		return Animations.BIG_WEAPON_WEAK_ATTACK;
	}

	@Override
	public float getStaminaDamage()
	{
		return 9.0F;
	}
}
