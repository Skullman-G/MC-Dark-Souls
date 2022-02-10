package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
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
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.DAGGER_LIGHT_ATTACK);
		this.putMove(builder, AttackType.HEAVY, true, Animations.DAGGER_HEAVY_ATTACK);
		this.putMove(builder, AttackType.DASH, true, Animations.STRAIGHT_SWORD_DASH_ATTACK);
		return builder;
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.SWORD_SWING.get();
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.DAGGER;
	}
}
