package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.item.Item;

public abstract class RangedWeaponCap extends WeaponCap
{
	private final float damage;
	
	public RangedWeaponCap(Item item, WeaponCategory category, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot, float damage,
			int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, category, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling, 0F);
		
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
			this.animationSet.put(LivingMotion.SHOOTING, shot);
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
