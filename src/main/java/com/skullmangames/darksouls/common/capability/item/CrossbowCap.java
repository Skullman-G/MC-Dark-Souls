package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.item.Item;

public class CrossbowCap extends RangedWeaponCap
{
	public CrossbowCap(Item item, float damage, int requiredStrength, int requiredDex, Scaling strengthScaling, Scaling dexScaling)
	{
		super(item, WeaponCategory.CROSSBOW, Animations.BIPED_CROSSBOW_RELOAD, Animations.BIPED_CROSSBOW_AIM, Animations.BIPED_CROSSBOW_SHOT, damage, requiredStrength, requiredDex, strengthScaling, dexScaling);
		this.animationSet.put(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		this.animationSet.put(LivingMotion.WALKING, Animations.BIPED_WALK_CROSSBOW);
		this.animationSet.put(LivingMotion.RUNNING, Animations.BIPED_WALK_CROSSBOW);
	}
	
	@Override
	public final HandProperty getHandProperty()
	{
		return HandProperty.TWO_HANDED;
	}

	@Override
	public float getStaminaDamage()
	{
		return 4.0F;
	}
}