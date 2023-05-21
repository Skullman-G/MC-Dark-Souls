package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.core.init.Animations;
import net.minecraft.world.item.Item;

public class TridentCap extends RangedWeaponCap
{
	public TridentCap(Item item, float damage, int reqStrength, int reqDex, int reqFaith, Scaling strengthScaling, Scaling dexScaling, Scaling faithScaling)
	{
		super(item, WeaponCategory.NONE_WEAON, null, Animations.BIPED_SPEER_AIM, Animations.BIPED_SPEER_REBOUND, damage, reqStrength, reqDex, reqFaith, strengthScaling, dexScaling, faithScaling);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.MAINHAND_ONLY;
	}

	@Override
	public int getStaminaDamage()
	{
		return 0;
	}

	@Override
	public int getStaminaUsage(AttackType type, boolean twohanded)
	{
		return 25;
	}
}