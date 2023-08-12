package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class StrafingGoal extends Goal
{
	protected final MobCap<?> mobdata;
	protected final Mob mob;
	private int strafingTime;
	private float dir;
	
	private int time;
	private int defaultTime;
	
	public StrafingGoal(MobCap<?> mobdata, int defaultTime)
	{
		this.mobdata = mobdata;
		this.mob = mobdata.getOriginalEntity();
		this.defaultTime = defaultTime;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		return this.mob.getTarget() != null && !this.mobdata.isInaction() && this.mob.distanceTo(this.mob.getTarget()) <= 5 && this.mob.getRandom().nextBoolean();
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return this.mob.getTarget() != null && !this.mobdata.isInaction() && this.mob.distanceTo(this.mob.getTarget()) <= 5 && --this.time > 0;
	}
	
	@Override
	public void start()
	{
		if (ModCapabilities.getItemCapability(this.mob.getOffhandItem()) instanceof Shield)
			this.mob.startUsingItem(InteractionHand.OFF_HAND);
		
		this.time = this.defaultTime;
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
		double targetDist = this.mob.distanceTo(target);
		this.mobdata.rotateTo(target, 60, false);
		
		if (this.time < 10)
		{
			if (targetDist > 2F) this.mob.getMoveControl().strafe(1, 0);
		}
		else
		{
			if (targetDist <= 3F) this.mob.getMoveControl().strafe(-1, 0);
			else
			{
				if (--this.strafingTime <= 0)
				{
					this.strafingTime = 40;
					this.dir = this.dir > 0 ? -0.4F : 0.4F;
				}
				this.mob.getMoveControl().strafe(0, this.dir);
			}
		}
	}
}
