package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class StrafingGoal extends Goal
{
	protected final MobCap<?> mobdata;
	protected final Mob mob;
	
	public StrafingGoal(MobCap<?> mobdata)
	{
		this.mobdata = mobdata;
		this.mob = mobdata.getOriginalEntity();
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		return this.mob.getTarget() != null && !this.mobdata.isInaction() && this.mobdata.getStamina() <= 5F;
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return this.mob.getTarget() != null && this.mobdata.getStamina() < 10F;
	}
	
	@Override
	public void start()
	{
		if (ModCapabilities.getItemCapability(this.mob.getOffhandItem()) instanceof IShield)
			this.mob.startUsingItem(InteractionHand.OFF_HAND);
	}
	
	@Override
	public void stop()
	{
		this.mob.stopUsingItem();
	}
	
	@Override
	public void tick()
	{
		LivingEntity target = this.mob.getTarget();
		this.mobdata.rotateTo(target, 60, false);
		double targetDist = this.mob.distanceToSqr(target);
		if (targetDist <= 20F) this.mob.getMoveControl().strafe(-1, 0);
	}
}
