package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;
import net.minecraft.world.item.Item;

public class TridentCap extends RangedWeaponCap
{
	public TridentCap(Item item, float damage, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.NONE_WEAON, null, Animations.BIPED_SPEER_AIM, Animations.BIPED_SPEER_REBOUND, damage, requiredStrength, requiredDex, strengthScaling, dexScaling);
	}
	
	@Override
	public WieldStyle getStyle(LivingCap<?> entitydata)
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