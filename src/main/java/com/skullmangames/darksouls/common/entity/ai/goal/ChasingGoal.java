package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.MobData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class ChasingGoal extends Goal
{
	protected MobData<?> mobdata;
	protected final Mob attacker;
	private final double speedTowardsTarget;
	private final boolean longMemory;
	private Path path;
	private int delayCounter;
	private double targetX;
	private double targetY;
	private double targetZ;
	protected final int attackInterval = 20;
	private int failedPathFindingPenalty = 0;
	private boolean canPenalize = false;

	protected final StaticAnimation chasingAnimation;
	protected final StaticAnimation walkingAnimation;
	protected final boolean changeMotion;

	public ChasingGoal(MobData<?> mobdata, Mob host, double speedIn, boolean useLongMemory, StaticAnimation chasingId, StaticAnimation walkId, boolean changeMotion)
	{
		this.mobdata = mobdata;
		this.attacker = host;
		this.speedTowardsTarget = speedIn;
		this.longMemory = useLongMemory;
		this.chasingAnimation = chasingId;
		this.walkingAnimation = walkId;
		this.changeMotion = changeMotion;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	public ChasingGoal(MobData<?> mobdata, Mob host, double speedIn, boolean useLongMemory)
	{
		this(mobdata, host, speedIn, useLongMemory, null, null, false);
	}
	
	public ChasingGoal(MobData<?> mobdata, Mob host, double speedIn, boolean useLongMemory, StaticAnimation chasing, StaticAnimation walk)
	{
		this(mobdata, host, speedIn, useLongMemory, chasing, walk, true);
	}
	
	@Override
	public boolean canUse()
	{
		LivingEntity livingentity = this.attacker.getTarget();
		
		if (livingentity == null || !livingentity.isAlive())
		{
			return false;
		}
		else if (this.mobdata.isInaction())
		{
			return false;
		}
		else
		{
			if (canPenalize)
			{
				if (--this.delayCounter <= 0)
				{
					this.path = this.attacker.getNavigation().createPath(livingentity, 0);
					this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
					return this.path != null;
				}
				else
				{
					return true;
				}
			}
			
			this.path = this.attacker.getNavigation().createPath(livingentity, 0);
			if (this.path != null)
			{
				return true;
			}
			else
			{
				return this.getAttackReachSqr(livingentity) >= this.attacker.distanceToSqr(livingentity.getX(), livingentity.getBoundingBox().minY, livingentity.getZ());
			}
		}
	}

	@Override
	public boolean canContinueToUse()
	{
		LivingEntity livingentity = this.attacker.getTarget();
		
		if (livingentity == null)
		{
			return false;
		}
		else if(!livingentity.isAlive())
		{
			return false;
		}
		else if(!this.attacker.isWithinRestriction(new BlockPos(livingentity.position())))
		{
			return false;
		}
		else
		{
			return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative();
		}
	}

	@Override
	public void start()
	{
		this.attacker.getNavigation().moveTo(this.path, this.speedTowardsTarget);
		this.attacker.setAggressive(true);
		this.delayCounter = -1;
		
		if(changeMotion)
        {
			STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getId(), 1);
			msg.setMotions(LivingMotion.WALKING);
			msg.setAnimations(chasingAnimation);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
        }
	}

	@Override
	public void stop()
	{
		LivingEntity livingentity = this.attacker.getTarget();
		if(!livingentity.isAttackable())
		{
			this.attacker.setTarget((LivingEntity) null);
		}
		
		this.attacker.setAggressive(false);
		this.attacker.getNavigation().stop();
		
		if(changeMotion)
        {
			STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getId(), 1);
			msg.setMotions(LivingMotion.WALKING);
			msg.setAnimations(walkingAnimation);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
        }
	}

	@Override
	public void tick()
	{
		if(this.mobdata.isInaction())
		{
			this.attacker.getNavigation().stop();
			this.delayCounter = -1;
			return;
		}
		
		LivingEntity livingentity = this.attacker.getTarget();
		this.attacker.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
		double d0 = this.attacker.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
		
		if (this.longMemory || this.attacker.getSensing().hasLineOfSight(livingentity) && --this.delayCounter <= 0 && 
				(this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingentity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D
				|| this.attacker.getRandom().nextFloat() < 0.05F))
		{
			this.targetX = livingentity.getX();
			this.targetY = livingentity.getBoundingBox().minY;
			this.targetZ = livingentity.getZ();
			this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
			
			if(this.canPenalize)
			{
				this.delayCounter += failedPathFindingPenalty;
				if(this.attacker.getNavigation().getPath() != null)
				{
					Node finalPathPoint = this.attacker.getNavigation().getPath().getEndNode();
					if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
						failedPathFindingPenalty = 0;
					else
						failedPathFindingPenalty += 10;
				}
				else
				{
					failedPathFindingPenalty += 10;
				}
			}
			if (d0 > 1024.0D)
				this.delayCounter += 10;
			else if (d0 > 256.0D)
				this.delayCounter += 5;
			
			if (!this.attacker.getNavigation().moveTo(livingentity, this.speedTowardsTarget))
				this.delayCounter += 2;
		}
	}

	protected double getAttackReachSqr(LivingEntity attackTarget)
	{
		return (double)(this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F + attackTarget.getBbWidth());
	}
}