package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

public class SpellAttackGoal extends Goal
{
	private final MobCap<?> mobCap;
	private final Mob mob;
	private final SpellAttackInstance[] spells;
	private final ItemStack casterItem;
	private ItemStack prevItem;
	
	public SpellAttackGoal(MobCap<?> mobCap, ItemStack casterItem, SpellAttackInstance... spells)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.casterItem = casterItem;
		this.prevItem = ItemStack.EMPTY;
		this.spells = spells;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		boolean flag = false;
		for (SpellAttackInstance spell : this.spells)
		{
			if (spell.cooldown > 0) --spell.cooldown;
			if (spell.canUse(this.mobCap)) flag = true;
		}
		return flag;
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return this.mobCap.isInaction();
	}
	
	@Override
	public void start()
	{
		this.prevItem = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
		this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.casterItem);
		for (SpellAttackInstance s : this.spells)
		{
			if (s.canUse(this.mobCap))
			{
				s.performSpell(this.mobCap);
				break;
			}
		}
	}
	
	@Override
	public void stop()
	{
		if (!this.prevItem.isEmpty())
		{
			this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.prevItem);
			this.prevItem = ItemStack.EMPTY;
		}
	}
}
