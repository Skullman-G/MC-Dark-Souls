package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;
import com.skullmangames.darksouls.common.item.WeaponItem;
import com.skullmangames.darksouls.common.skill.Skill;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Skills;
import com.skullmangames.darksouls.core.init.SoundEvents;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class GreatHammerCapability extends WeaponCapability
{
	public GreatHammerCapability(Item item)
	{
		super(item, WeaponCategory.GREAT_HAMMER);
	}
	
	@Override
	public HoldingWeaponAnimation getHoldingAnimation()
	{
		return Animations.BIPED_HOLDING_GREAT_HAMMER;
	}
	
	@Override
	public Skill getWeakAttack()
	{
		return Skills.GREAT_HAMMER_WEAK_ATTACK;
	}
	
	@Override
	public Skill getHeavyAttack(LivingEntity entity)
	{
		if (!(this.orgItem instanceof WeaponItem)) return null;
		if (!((WeaponItem)this.orgItem).meetRequirements(entity)) return this.getWeakAttack();
		return Skills.GREAT_HAMMER_HEAVY_ATTACK;
	}
	
	@Override
	public SoundEvent getSmashSound()
	{
		return SoundEvents.GREAT_HAMMER_SMASH;
	}
}
