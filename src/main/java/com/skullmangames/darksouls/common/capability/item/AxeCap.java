package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;

public class AxeCap extends MeleeWeaponCap
{
	public AxeCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.AXE, requiredStrength, requiredDex, strengthScaling, dexScaling, 35F);
	}
	
	@Override
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.AXE_LIGHT_ATTACK);
		this.putMove(builder, AttackType.HEAVY, true, Animations.AXE_HEAVY_ATTACK);
		this.putMove(builder, AttackType.DASH, true, Animations.AXE_DASH_ATTACK);
		return builder;
	}

	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.TOOL;
	}

	@Override
	public float getStaminaDamage()
	{
		return 6.0F;
	}
}