package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class GreatHammerCap extends MeleeWeaponCap
{
	public GreatHammerCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.GREAT_HAMMER, requiredStrength, requiredDex, strengthScaling, dexScaling, 50F);
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
	public AttackAnimation[] getLightAttack()
	{
		return Animations.GREAT_HAMMER_LIGHT_ATTACK;
	}
	
	@Override
	protected AttackAnimation getWeakAttack()
	{
		return Animations.GREAT_HAMMER_WEAK_ATTACK;
	}
	
	@Override
	public AttackAnimation getHeavyAttack()
	{
		return Animations.GREAT_HAMMER_HEAVY_ATTACK;
	}
	
	@Override
	public SoundEvent getSmashSound()
	{
		return ModSoundEvents.GREAT_HAMMER_SMASH.get();
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.great_hammer;
	}
}
