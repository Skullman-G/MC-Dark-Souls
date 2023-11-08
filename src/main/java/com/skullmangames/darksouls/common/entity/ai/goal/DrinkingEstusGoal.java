package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.item.EstusFlaskItem;
import com.skullmangames.darksouls.core.init.ModItems;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class DrinkingEstusGoal extends Goal
{
	private final MobCap<?> mobCap;
	private final Mob mob;
	private int recovery;
	private boolean isEmpty;

	public DrinkingEstusGoal(MobCap<?> mobCap)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	public boolean canUse()
	{
		return !this.isEmpty && this.mob.getMainHandItem().is(ModItems.ESTUS_FLASK.get());
	}

	public boolean canContinueToUse()
	{
		return this.mob.isUsingItem() || --this.recovery > 0;
	}

	public void start()
	{
		this.recovery = 10;
	}
	
	@Override
	public void tick()
	{
		if (recovery == 6) this.mob.startUsingItem(InteractionHand.MAIN_HAND);
		if (this.mob.getUseItemRemainingTicks() < 10 && this.mob.getUseItemRemainingTicks() > 0) this.recovery = 5;
	}

	public void stop()
	{
		this.mobCap.cancelUsingItem();
		if (EstusFlaskItem.getUses(this.mob.getMainHandItem()) <= 0) this.isEmpty = true;
	}
}
