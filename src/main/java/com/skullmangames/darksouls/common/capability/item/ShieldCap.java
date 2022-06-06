package com.skullmangames.darksouls.common.capability.item;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModSoundEvents;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class ShieldCap extends MeleeWeaponCap
{
	private final float physicalDefense;
	private final ShieldType shieldType;
	private final ShieldMat shieldMat;
	
	public ShieldCap(Item item, ShieldType shieldType, ShieldMat shieldMat, float physicalDefense, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.SHIELD, requiredStrength, requiredDex, strengthScaling, dexScaling, 20F);
		this.physicalDefense = Math.min(physicalDefense, 1F);
		this.shieldType = shieldType;
		this.shieldMat = shieldMat;
	}
	
	@Override
	protected Builder<AttackType, Pair<Boolean, AttackAnimation[]>> initMoveset()
	{
		Builder<AttackType, Pair<Boolean, AttackAnimation[]>> builder = super.initMoveset();
		this.putMove(builder, AttackType.LIGHT, true, Animations.SHIELD_LIGHT_ATTACK);
		return builder;
	}
	
	@Override
	public float getPhysicalDefense()
	{
		return this.physicalDefense;
	}

	@Override
	public ShieldType getShieldType()
	{
		return this.shieldType;
	}

	@Override
	public float getStaminaDamage()
	{
		return 6.0F;
	}
	
	@Override
	public SoundEvent getSwingSound()
	{
		return ModSoundEvents.FIST_SWING.get();
	}

	@Override
	public SoundEvent getBlockSound()
	{
		switch(this.shieldMat)
		{
			default:
			case WOOD: return ModSoundEvents.WOODEN_SHIELD_BLOCK.get();
			case METAL: return ModSoundEvents.IRON_SHIELD_BLOCK.get();
		}
	}
	
	public enum ShieldMat
	{
		WOOD, METAL
	}
}
