package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.WeaponMovesets;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

public class ShieldCap extends MeleeWeaponCap
{
	private final float physicalDefense;
	private final float fireDefense;
	private final float lightningDefense;
	private final ShieldType shieldType;
	private final ShieldMat shieldMat;
	
	public ShieldCap(Item item, ShieldType shieldType, ShieldMat shieldMat, float physicalDef, float fireDef, float lightningDef, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponMovesets.SHIELD, Colliders.FIST, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
		this.physicalDefense = Math.min(physicalDef, 1F);
		this.fireDefense = Math.min(fireDef, 1F);
		this.lightningDefense = Math.min(lightningDef, 1F);
		this.shieldType = shieldType;
		this.shieldMat = shieldMat;
	}
	
	@Override
	public float getDefense(DamageType damageType)
	{
		switch(damageType)
		{
			case FIRE: return this.fireDefense;
			case LIGHTNING: return this.lightningDefense;
			default: return this.physicalDefense;
		}
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
			case GOLD: return ModSoundEvents.IRON_SHIELD_BLOCK.get();
		}
	}
	
	public enum ShieldMat
	{
		WOOD, METAL, GOLD
	}
}
