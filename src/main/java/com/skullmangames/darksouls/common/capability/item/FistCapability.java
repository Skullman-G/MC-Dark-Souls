package com.skullmangames.darksouls.common.capability.item;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;

public class FistCapability extends WeaponCapability
{
	public FistCapability(Item item)
	{
		super(item, WeaponCategory.FIST);
	}
	
	@Override
	protected AttackAnimation[] getLightAttack()
	{
		return Animations.FIST_LIGHT_ATTACK;
	}
	
	@Override
	protected void registerAttribute()
	{
		super.registerAttribute();
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(AttributeInit.IMPACT, AttributeInit.getImpactModifier(0.5D)));
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.fist;
	}
}
