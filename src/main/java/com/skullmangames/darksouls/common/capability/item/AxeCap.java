package com.skullmangames.darksouls.common.capability.item;

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
	public AttackAnimation[] getLightAttack()
	{
		return Animations.AXE_LIGHT_ATTACK;
	}
	
	@Override
	public AttackAnimation getHeavyAttack()
	{
		return Animations.AXE_HEAVY_ATTACK;
	}
	
	@Override
	public AttackAnimation getDashAttack()
	{
		return Animations.AXE_DASH_ATTACK;
	}

	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.tools;
	}
}