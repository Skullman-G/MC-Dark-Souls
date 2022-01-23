package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.item.Item;

public class AxeCapability extends MaterialItemCapability
{
	public AxeCapability(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.AXE, requiredStrength, requiredDex, strengthScaling, dexScaling);
	}
	
	@Override
	protected AttackAnimation[] getLightAttack()
	{
		return Animations.AXE_LIGHT_ATTACK;
	}
	
	@Override
	protected AttackAnimation getDashAttack()
	{
		return Animations.AXE_DASH_ATTACK;
	}

	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.tools;
	}
}