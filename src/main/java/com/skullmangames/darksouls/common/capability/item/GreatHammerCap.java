package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
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
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, false, Animations.GREAT_HAMMER_LIGHT_ATTACK);
		this.putMove(builder, AttackType.HEAVY, true, Animations.GREAT_HAMMER_HEAVY_ATTACK);
		this.putMove(builder, AttackType.DASH, false, Animations.GREAT_HAMMER_DASH_ATTACK);
		return builder;
	}
	
	@Override
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return Animations.BIPED_HOLDING_BIG_WEAPON;
	}
	
	@Override
	protected AttackAnimation getWeakAttack()
	{
		return Animations.BIG_WEAPON_WEAK_ATTACK;
	}
	
	@Override
	public SoundEvent getSmashSound()
	{
		return ModSoundEvents.GREAT_HAMMER_SMASH.get();
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.GREAT_HAMMER;
	}

	@Override
	public float getStaminaDamage()
	{
		return 9.0F;
	}
}
