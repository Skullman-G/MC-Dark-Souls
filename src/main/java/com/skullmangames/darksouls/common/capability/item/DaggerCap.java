package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class DaggerCap extends MeleeWeaponCap
{
	public DaggerCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.DAGGER, requiredStrength, requiredDex, strengthScaling, dexScaling, 5F);
	}
	
	@Override
	public AttackAnimation[] getLightAttack()
	{
		return Animations.DAGGER_LIGHT_ATTACK;
	}
	
	@Override
	public AttackAnimation getHeavyAttack()
	{
		return Animations.DAGGER_HEAVY_ATTACK;
	}
	
	@Override
	public AttackAnimation getDashAttack()
	{
		return Animations.STRAIGHT_SWORD_DASH_ATTACK;
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SWORD_SWING;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.dagger;
	}
}
