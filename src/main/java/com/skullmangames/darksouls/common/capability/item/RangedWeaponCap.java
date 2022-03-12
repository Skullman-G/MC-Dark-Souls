package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class RangedWeaponCap extends WeaponCap
{
	private final float damage;
	
	public RangedWeaponCap(Item item, WeaponCategory category, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot, float damage, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, category, requiredStrength, requiredDex, strengthScaling, dexScaling, 0F);
		
		this.damage = damage;
		
		if(reload != null)
		{
			this.animationSet.put(LivingMotion.RELOADING, reload);
		}
		if(aiming != null)
		{
			this.animationSet.put(LivingMotion.AIMING, aiming);
		}
		if(shot != null)
		{
			this.animationSet.put(LivingMotion.SHOTING, shot);
		}
	}

	@Override
	public boolean canUseOnMount()
	{
		return true;
	}

	@Override
	public float getDamage()
	{
		return this.damage;
	}
}
