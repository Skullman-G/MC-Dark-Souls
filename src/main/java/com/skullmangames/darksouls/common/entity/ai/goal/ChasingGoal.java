package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class ChasingGoal extends Goal
{
	protected final MobCap<?> mobCap;
	protected final Mob attacker;
	
	private final boolean defensive;
	private Path path;
	private double targetX;
	private double targetY;
	private double targetZ;

	public ChasingGoal(MobCap<?> mobdata, boolean defensive)
	{
		this.mobCap = mobdata;
		this.attacker = mobdata.getOriginalEntity();
		this.defensive = defensive;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		LivingEntity target = this.attacker.getTarget();
		if (target == null || !target.isAlive() || this.mobCap.isInaction()) return false;
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
		if (target == null || !target.isAlive() || this.mobCap.isInaction()) return false;
		else return (!(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative())
				&& this.attacker.distanceTo(target) > 2;
	}

	@Override
	public void start()
	{
		this.attacker.getNavigation().moveTo(this.path, 1D);
		this.attacker.setAggressive(true);
		
		if (this.defensive && ModCapabilities.getItemCapability(this.attacker.getOffhandItem()) instanceof IShield)
			this.attacker.startUsingItem(InteractionHand.OFF_HAND);
	}

	@Override
	public void stop()
	{
		LivingEntity livingentity = this.attacker.getTarget();
		if(livingentity != null && !livingentity.isAttackable())
		{
			this.attacker.setTarget((LivingEntity) null);
		}
		
		this.attacker.setSprinting(false);
		this.attacker.stopUsingItem();
		this.attacker.setAggressive(false);
		this.attacker.getNavigation().stop();
	}

	@Override
	public void tick()
	{
		LivingEntity target = this.attacker.getTarget();
		this.attacker.getLookControl().setLookAt(target, 30F, 30F);
		this.mobCap.rotateTo(target, 60, false);
		
		if (target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1D)
		{
			if (!this.defensive && this.attacker.distanceToSqr(target) > 50D) this.attacker.setSprinting(true);
			this.attacker.getNavigation().moveTo(target, 1.0F);
		}
	}

	protected double getAttackReachSqr(LivingEntity attackTarget)
	{
		return (double)(this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F + attackTarget.getBbWidth());
	}
}