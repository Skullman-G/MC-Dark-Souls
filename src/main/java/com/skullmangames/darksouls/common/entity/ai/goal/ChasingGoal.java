package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class ChasingGoal extends Goal
{
	protected MobData<?> mobdata;
	protected final Mob attacker;
	private final double speedTowardsTarget;
	private final boolean longMemory;
	private Path path;
	private Vec3 targetPos = new Vec3(0D, 0D, 0D);
	protected final int attackInterval = 20;

	public ChasingGoal(MobData<?> mobdata, double speedIn, boolean hasLongMemory)
	{
		this.mobdata = mobdata;
		this.attacker = mobdata.getOriginalEntity();
		this.speedTowardsTarget = speedIn;
		this.longMemory = hasLongMemory;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		LivingEntity target = this.attacker.getTarget();
		if (target == null || !target.isAlive() || this.mobdata.isInaction()) return false;
		else
		{
			this.path = this.attacker.getNavigation().createPath(target, 0);
			return this.path != null
					|| this.getAttackReachSqr(target) >= this.attacker.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
		}
	}

	@Override
	public boolean canContinueToUse()
	{
		LivingEntity target = this.attacker.getTarget();
		if(target == null || !target.isAlive() || !this.attacker.isWithinRestriction(new BlockPos(target.position()))) return false;
		else return !(target instanceof Player)
					|| !target.isSpectator() && !((Player) target).isCreative();
	}

	@Override
	public void start()
	{
		this.attacker.getNavigation().moveTo(this.path, this.speedTowardsTarget);
		this.attacker.setAggressive(true);
	}

	@Override
	public void stop()
	{
		LivingEntity livingentity = this.attacker.getTarget();
		if(livingentity != null && !livingentity.isAttackable())
		{
			this.attacker.setTarget((LivingEntity) null);
		}
		
		this.attacker.setAggressive(false);
		this.attacker.getNavigation().stop();
	}

	@Override
	public void tick()
	{
		if(this.mobdata.isInaction())
		{
			this.attacker.getNavigation().stop();
			return;
		}
		
		LivingEntity target = this.attacker.getTarget();
		this.attacker.getLookControl().setLookAt(target, 30F, 30F);
		
		if ((this.longMemory || this.attacker.getSensing().hasLineOfSight(target))
				&& (this.targetPos == new Vec3(0D, 0D, 0D) || target.distanceToSqr(this.targetPos) >= 1D))
		{
			this.targetPos = new Vec3(target.getX(), target.getBoundingBox().minY, target.getZ());
		}
	}

	protected double getAttackReachSqr(LivingEntity attackTarget)
	{
		return (double)(this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F + attackTarget.getBbWidth());
	}
}