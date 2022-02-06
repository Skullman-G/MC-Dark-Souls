package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class SpearCap extends MeleeWeaponCap
{
	public SpearCap(Item item, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.SPEAR, requiredStrength, requiredDex, strengthScaling, dexScaling, 20F);
	}
	
	@Override
	public AttackAnimation[] getLightAttack()
	{
		return Animations.SPEAR_LIGHT_ATTACK;
	}
	
	@Override
	public AttackAnimation getHeavyAttack()
	{
		return Animations.SPEAR_HEAVY_ATTACK;
	}
	
	@Override
	public AttackAnimation getDashAttack()
	{
		return Animations.SPEAR_DASH_ATTACK;
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.spear;
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SPEAR_SWING.get();
	}
}
