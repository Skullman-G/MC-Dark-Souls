package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.MobCap;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class StandStillGoal extends Goal
{
	protected final Mob mob;
	protected final MobCap<?> mobCap;
	
	public StandStillGoal(MobCap<?> mobCap)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		EntityState state = this.mobCap.getEntityState();
		return state == EntityState.HIT && state == EntityState.INVINCIBLE;
	}
	
	@Override
	public boolean canContinueToUse()
	{
		EntityState state = this.mobCap.getEntityState();
		return state == EntityState.HIT && state == EntityState.INVINCIBLE;
	}
}
