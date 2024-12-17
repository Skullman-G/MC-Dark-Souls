package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.ParryAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.vector.Vector2f;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class StrafingGoal extends Goal
{
	protected final Mob mob;
	protected final MobCap<?> mobCap;
	
	protected boolean parryMode;
	protected AdaptableAnimation parryStance;
	protected ParryAnimation parry;
	
	protected Vector2f strafingDir;
	protected int strafeLength;
	protected int strafingTime;
	protected final int minStrafeLength;
	protected int maxStrafeLength;
	protected final int maxMaxStrafeLength;
	
	protected final float minDist;
	
	private long lastCanUseCheck;
	
	private static final double maxDist = 5.0D;
	
	protected static Vector2f BACK = new Vector2f(-0.5F, 0);
	protected static Vector2f LEFT = new Vector2f(0, -0.25F);
	protected static Vector2f RIGHT = new Vector2f(0, 0.25F);
	
	public StrafingGoal(MobCap<?> mobCap, float minDist, int minStrafeLength, int maxStrafeLength)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.minDist = minDist;
		this.minStrafeLength = minStrafeLength;
		this.maxMaxStrafeLength = maxStrafeLength;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	@Override
	public boolean canUse()
	{
		long i = this.mob.level.getGameTime();
		if (i - this.lastCanUseCheck < 20L) return false;
		this.lastCanUseCheck = i;
		
		return !this.mobCap.isInaction()
				&& this.isValidTarget(this.mob.getTarget())
				&& this.mobCap.rndmPercentage(0.4F);
	}
	
	@Override
	public boolean canContinueToUse()
	{
		return !this.mobCap.isInaction()
				&& this.isValidTarget(this.mob.getTarget())
				&& this.strafeLength <= this.maxStrafeLength
				&& this.strafeLength < this.minStrafeLength;
	}
	
	@Override
	public boolean isInterruptable()
	{
		return false;
	}
	
	private boolean isValidTarget(LivingEntity target)
    {
    	return target != null
    			&& target.isAlive()
    			&& this.mob.distanceTo(target) <= maxDist
    			&& !(target instanceof Player p && (p.isSpectator() || p.isCreative()));
    }
	
	private boolean strafingBlocked()
    {
        double x = this.mob.getX() + this.mob.xxa;
        double z = this.mob.getZ() + this.mob.zza;
        
        BlockPos pos = new BlockPos(x, this.mob.getY(), z);
        return !this.mob.level.getBlockState(pos).isAir()
        		|| !this.mob.level.getBlockState(pos.above()).isAir()
        		|| this.mob.level.getBlockState(pos.below()).isAir();
    }
	
	public StrafingGoal withParry(AdaptableAnimation stance, ParryAnimation parry)
	{
		this.parryStance = stance;
		this.parry = parry;
		return this;
	}
	
	@Override
	public void start()
	{
		LivingEntity target = this.mob.getTarget();
		if (this.parryStance != null && this.parry != null && this.mobCap.rndmPercentage(0.4F))
    	{
    		LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
    		if (targetCap != null && targetCap.canBeParried())
    		{
    			this.parryMode = true;
        		STCLivingMotionChange msg = new STCLivingMotionChange(this.mob.getId(), false);
        		for (LivingMotion motion : this.parryStance.getAvailableMotions())
        		{
        			msg.put(motion, this.parryStance.getForMotion(motion));
        		}
        		ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.mob);
    		}
    	}
    	else if (ModCapabilities.getItemCapability(this.mob.getOffhandItem()) instanceof Shield && this.mobCap.canBlock())
			this.mob.startUsingItem(InteractionHand.OFF_HAND);
		
		this.initStrafe();
		this.strafeLength = 0;
		this.maxStrafeLength = this.mob.getRandom().nextInt(this.minStrafeLength, this.maxMaxStrafeLength + 1);
	}
	
	private void initStrafe()
	{
		this.strafingDir = this.strafeLength == 0 && this.mob.distanceTo(this.mob.getTarget()) < this.minDist ? BACK
				: this.strafingDir == BACK ? (this.mobCap.rndmPercentage(0.5F) ? LEFT : RIGHT)
				: this.strafingDir == RIGHT ? LEFT : RIGHT;
    	this.strafingTime = this.mob.getRandom().nextInt(10, 21);
	}
	
	@Override
	public void tick()
	{
		LivingEntity target = this.mob.getTarget();
		this.mobCap.rotateTo(target, 60, false);
		
		if (this.parryMode)
		{
			LivingCap<?> targetCap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
			if (targetCap != null)
			{
				AnimationPlayer animPlayer = targetCap.getAnimator().getMainPlayer();
				float elapsedTime = animPlayer.getElapsedTime();
				if (animPlayer.getPlay() instanceof AttackAnimation anim)
				{
					float parryTimeDist = anim.getPhaseByTime(elapsedTime).contactStart - elapsedTime;
					if (parryTimeDist < 2.0F)
					{
						this.mobCap.playAnimationSynchronized(this.parry, 0.0F);
					}
				}
			}
		}

		this.mob.getMoveControl().strafe(this.strafingDir.x, this.strafingDir.y);
		--this.strafingTime;
		
		if (this.strafingTime <= 0 || this.strafingBlocked()
				|| (this.mob.distanceTo(target) >= this.minDist && this.strafingDir == BACK))
		{
			this.initStrafe();
	    	++this.strafeLength;
		}
	}
	
	@Override
	public void stop()
	{
		if (this.parryMode)
    	{
    		this.parryMode = false;
    		STCLivingMotionChange msg = new STCLivingMotionChange(this.mob.getId(), false);
    		ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.mob);
    	}
		
		this.mob.getMoveControl().strafe(0, 0);
		this.mob.stopUsingItem();
		this.mob.zza = 0;
		this.mob.xxa = 0;
		
		this.strafeLength = 0;
		this.strafingTime = 0;
	}
}
