package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.item.Item;

public class RangedWeaponCapability extends WeaponCapability
{
	public RangedWeaponCapability(Item item, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.NONE_WEAON, requiredStrength, requiredDex, strengthScaling, dexScaling);
		
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
		this.registerAttribute();
	}

	@Override
	public boolean canUseOnMount()
	{
		return true;
	}
}
