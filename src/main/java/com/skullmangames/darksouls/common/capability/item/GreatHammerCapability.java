package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class GreatHammerCapability extends WeaponCapability
{
	public GreatHammerCapability(Item item)
	{
		super(item, WeaponCategory.GREAT_HAMMER);
	}
	
	@Override
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return Animations.BIPED_HOLDING_GREAT_HAMMER;
	}
	
	@Override
	protected boolean repeatLightAttack()
	{
		return false;
	}
	
	@Override
	protected AttackAnimation[] getLightAttack()
	{
		return Animations.GREAT_HAMMER_LIGHT_ATTACK;
	}
	
	@Override
	protected AttackAnimation getWeakAttack()
	{
		return Animations.GREAT_HAMMER_WEAK_ATTACK;
	}
	
	@Override
	protected AttackAnimation getHeavyAttack()
	{
		return Animations.GREAT_HAMMER_HEAVY_ATTACK;
	}
	
	@Override
	public SoundEvent getSmashSound()
	{
		return ModSoundEvents.GREAT_HAMMER_SMASH;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.great_hammer;
	}
}
