package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import net.minecraft.item.Item;

public class TridentCap extends RangedWeaponCap
{
	public TridentCap(Item item, float damage, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.NONE_WEAON, null, Animations.BIPED_SPEER_AIM, Animations.BIPED_SPEER_REBOUND, damage, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@Override
	public WieldStyle getStyle(LivingCap<?> entityCap)
	{
		return WieldStyle.ONE_HAND;
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.MAINHAND_ONLY;
	}

	@Override
	public float getStaminaDamage()
	{
		return 4.0F;
	}
}