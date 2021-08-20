package com.skullmangames.darksouls.common.capability.item;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;

import net.minecraft.item.Item;

public class RangedWeaponCapability extends CapabilityItem
{
	protected Map<LivingMotion, StaticAnimation> rangeAnimationSet;

	public RangedWeaponCapability(Item item, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot)
	{
		super(item, WeaponCategory.NONE_WEAON);
		this.rangeAnimationSet = new HashMap<LivingMotion, StaticAnimation> ();
		
		if(reload != null)
		{
			this.rangeAnimationSet.put(LivingMotion.RELOADING, reload);
		}
		if(aiming != null)
		{
			this.rangeAnimationSet.put(LivingMotion.AIMING, aiming);
		}
		if(shot != null)
		{
			this.rangeAnimationSet.put(LivingMotion.SHOTING, shot);
		}
		this.registerAttribute();
	}
	
	@Override
	protected void registerAttribute()
	{
		this.addStyleAttributeSimple(WieldStyle.TWO_HAND, 0.0D, 1.1D, 3);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> playerdata)
	{
		return rangeAnimationSet;
	}

	@Override
	public boolean canUseOnMount()
	{
		return true;
	}
}
