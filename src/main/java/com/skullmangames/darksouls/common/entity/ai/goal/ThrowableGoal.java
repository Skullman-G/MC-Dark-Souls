package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.ThrowableCap;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;

public class ThrowableGoal extends Goal
{
	private final MobCap<?> mobCap;
	private int timeout;
	
	public ThrowableGoal(MobCap<?> mobCap)
	{
		this.mobCap = mobCap;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		return !this.mobCap.isInaction()
				&& this.timeout-- <= 0
				&& this.mobCap.getHeldItemCapability(InteractionHand.MAIN_HAND) instanceof ThrowableCap
				&& this.mobCap.getTarget() != null
				&& this.mobCap.getTarget().distanceTo(this.mobCap.getOriginalEntity()) < 20;
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return this.mobCap.isInaction();
	}
	
	@Override
	public void start()
	{
		ThrowableCap throwable = (ThrowableCap)this.mobCap.getHeldItemCapability(InteractionHand.MAIN_HAND);
		this.mobCap.rotateTo(this.mobCap.getTarget(), 60F, true);
		throwable.use(this.mobCap);
		this.timeout = 20;
	}
}
