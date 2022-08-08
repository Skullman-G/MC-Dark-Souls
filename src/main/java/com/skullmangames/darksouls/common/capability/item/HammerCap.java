package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;

public class HammerCap extends MeleeWeaponCap
{
	public HammerCap(Item item, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling,
			Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.HAMMER, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling, 35F);
	}
	
	@Override
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.HAMMER_LIGHT_ATTACK);
		this.putMove(builder, AttackType.HEAVY, true, Animations.HAMMER_HEAVY_ATTACK);
		this.putMove(builder, AttackType.DASH, true, Animations.HAMMER_DASH_ATTACK);
		return builder;
	}

	@Override
	public float getStaminaDamage()
	{
		return 8.0F;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.TOOL;
	}
}
