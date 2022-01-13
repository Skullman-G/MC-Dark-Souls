package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.Random;

import com.skullmangames.darksouls.common.entity.FireKeeperEntity;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class WalkAroundBonfireGoal extends WaterAvoidingRandomStrollGoal
{
	private final FireKeeperEntity fireKeeper;
	
	public WalkAroundBonfireGoal(FireKeeperEntity entity, double speedmodifier)
	{
		super(entity, speedmodifier);
		this.fireKeeper = entity;
	}
	
	@Override
	public boolean canUse()
	{
		if (this.mob.level.getNearestPlayer(this.mob, 20.0D) != null) return false;
		
		Random random = this.mob.getRandom();
		this.wantedX = this.fireKeeper.getLinkedBonfirePos().getX() + 5 - random.nextInt(10);
		this.wantedY = this.fireKeeper.getLinkedBonfirePos().getY();
		this.wantedZ = this.fireKeeper.getLinkedBonfirePos().getZ() + 5 - random.nextInt(10);
		
		return super.canUse();
	}
	
	@Override
	public void start()
	{
		this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
	}
	
	@Override
	public void tick()
	{
		if (this.mob.level.getNearestPlayer(this.mob, 20.0D) != null)
		{
			this.stop();
			return;
		}
	}
}
