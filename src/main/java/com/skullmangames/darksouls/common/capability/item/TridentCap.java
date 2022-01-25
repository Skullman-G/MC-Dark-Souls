package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.Animations;
import net.minecraft.world.item.Item;

public class TridentCap extends RangedWeaponCap
{
	public TridentCap(Item item, float damage, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, null, Animations.BIPED_SPEER_AIM, Animations.BIPED_SPEER_REBOUND, damage, requiredStrength, requiredDex, strengthScaling, dexScaling);
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata)
	{
		return WieldStyle.ONE_HAND;
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.MAINHAND_ONLY;
	}
}