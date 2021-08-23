package com.skullmangames.darksouls.common.capability.item;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.Skills;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;

public class FistCapability extends CapabilityItem
{
	public FistCapability(Item item)
	{
		super(item, WeaponCategory.FIST);
	}
	
	@Override
	public Skill getLightAttack()
	{
		return Skills.FIST_LIGHT_ATTACK;
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
