package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
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
import net.minecraft.world.level.pathfinder.Path;

public class AttackGoal extends Goal
{
	protected final Mob attacker;
	protected final MobCap<?> mobCap;
	protected final float minDist;
	protected final int yDist;
	protected final boolean affectY;
	
	protected StaticAnimation dodge;
	private int dodgeTime;
	
	private boolean parryMode;
	protected AdaptableAnimation parryStance;
	protected ParryAnimation parry;
	
	protected int combo = 0;
	protected int currentAttack = -1;
	
	protected final List<AttackInstance> attacks = new ArrayList<>();
	
	private final boolean defensive;
	private Path path;
	private double targetX;
	private double targetY;
	private double targetZ;
	
	private final boolean shouldStrafe;
	private int strafingTime;
	private Vector2f strafingDir;
	private int strafeLength;
	private int strafeMinLength;
	
	private Phase phase = Phase.NONE;
	
	private static Vector2f BACK = new Vector2f(-0.5F, 0);
	private static Vector2f LEFT = new Vector2f(0, -0.5F);
	private static Vector2f RIGHT = new Vector2f(0, 0.5F);
	
	public AttackGoal(MobCap<?> mobCap, float minDist, boolean affectY, boolean defensive, boolean shouldStrafe)
	{
		this(mobCap, minDist, 0, affectY, defensive, shouldStrafe);
	}
	
	public AttackGoal(MobCap<?> mobCap, float minDist, int yDist, boolean affectY, boolean defensive, boolean shouldStrafe)
	{
		this.mobCap = mobCap;
		this.attacker = mobCap.getOriginalEntity();
		this.minDist = minDist * minDist;
		this.yDist = yDist;
		this.affectY = affectY;
		this.defensive = defensive;
		this.shouldStrafe = shouldStrafe;
		this.strafeMinLength = defensive ? 4 : 1;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	public AttackGoal addParry(AdaptableAnimation stance, ParryAnimation parry)
	{
		this.parryStance = stance;
		this.parry = parry;
		return this;
	}
	
	public AttackGoal addDodge(StaticAnimation dodge)
	{
		this.dodge = dodge;
		return this;
	}
	
	public AttackGoal addAttack(AttackInstance attack)
	{
		this.attacks.add(attack);
		return this;
	}
	
	private float getMaxDist(double range)
	{
		float maxDist = this.minDist;
		for (int i = 0; i < this.attacks.size(); i++)
		{
			AttackInstance a = this.attacks.get(i);
			if (a.range > maxDist && (i == 0 || i != this.currentAttack) && a.offset < range) maxDist = a.range;
		}
		return maxDist;
	}
	
	@Override
    public boolean canUse()
    {
		if (this.attacks.isEmpty()) return false;
		LivingEntity target = this.attacker.getTarget();
		if (!this.isValidTarget(target)) return false;
		this.updatePhase();
		return this.phase != Phase.NONE;
    }

    @Override
    public boolean canContinueToUse()
    {
    	LivingEntity target = this.attacker.getTarget();
    	if (!this.isValidTarget(target)) return false;
    	this.updatePhase();
		return this.phase != Phase.NONE;
    }
    
    protected boolean canExecuteAttack()
    {
    	return !this.mobCap.isInaction() && this.mobCap.getEntityState().getContactLevel() != 3;
    }
    
    protected boolean canExecuteComboAttack()
    {
    	return this.combo > 0
    			&& this.currentAttack > -1
    			&& this.attacks.get(this.currentAttack).isValidRange(this.getTargetRange(this.attacker.getTarget()))
    			&& this.mobCap.getEntityState().getContactLevel() == 3;
    }
    
    private void updatePhase()
    {
    	LivingEntity target = this.attacker.getTarget();
    	boolean inAttackRange = this.targetInAttackRange(target);
    	switch(this.phase)
    	{
    	default:
    	case NONE:
    		if (!this.mobCap.isInaction())
    		{
    			if (inAttackRange)
    			{
    				if (this.shouldStrafe && this.defensive) this.setPhase(Phase.STRAFING);
    				else this.setPhase(Phase.ATTACKING);
    			}
    			else this.setPhase(Phase.CHASING);
    		}
    		break;
    	case CHASING:
    		if (this.mobCap.isInaction()) this.setPhase(Phase.NONE);
    		else if (inAttackRange && !this.pathBlocked(target))
        	{
        		if (this.shouldStrafe && this.defensive && this.strafeLength == 0) this.setPhase(Phase.STRAFING);
        		else this.setPhase(Phase.ATTACKING);
        	}
    		break;
    	case STRAFING:
    		if (this.mobCap.isInaction()) this.setPhase(Phase.NONE);
    		else if (this.strafingTime <= 0 || this.strafingBlocked(inAttackRange))
        	{
    			if (this.strafeLength < this.strafeMinLength || this.rndmPercentage(0.3F)) this.setPhase(Phase.STRAFING);
    			else if (inAttackRange) this.setPhase(Phase.ATTACKING);
        		else this.setPhase(Phase.CHASING);
        	}
    		break;
    	case ATTACKING:
    		if (!inAttackRange || this.pathBlocked(target)) this.setPhase(Phase.CHASING);
    		else if (this.shouldStrafe && !this.mobCap.isInaction() && this.rndmPercentage(0.25F))
    		{
    			this.currentAttack = -1;
    			this.strafeLength = 0;
    			this.setPhase(Phase.STRAFING);
    		}
    		break;
    	}
    }
    
    private boolean rndmPercentage(float percentage)
    {
    	return this.attacker.getRandom().nextFloat() <= percentage;
    }
    
    private boolean strafingBlocked(boolean inAttackRange)
    {
    	if (this.strafingDir == BACK) return !inAttackRange;
    	
        float x = (float)this.attacker.getX() + this.attacker.xxa;
        float z = (float)this.attacker.getZ() + this.attacker.zza;
        
        BlockPos pos = new BlockPos(x, this.attacker.getY(), z);
        return !this.attacker.level.getBlockState(pos).isAir()
        		|| !this.attacker.level.getBlockState(pos.above()).isAir()
        		|| this.attacker.level.getBlockState(pos.below()).isAir();
    }
    
    private boolean pathBlocked(LivingEntity target)
    {
    	double distX = Math.abs(target.getX() - this.attacker.getX());
    	double distZ = Math.abs(target.getZ() - this.attacker.getZ());
    	return distX == distZ || distX == 0 || distZ == 0;
    }
    
    @Override
    public void tick()
    {
    	switch(this.phase)
    	{
    		default:
	    	case NONE:
	    		break;
    		case CHASING:
	    		this.chase();
	    		break;
	    	case STRAFING:
	    		this.strafe();
	    		break;
	    	case ATTACKING:
	    		this.attack();
	    		break;
    	}
    }
    
    private void startChasing()
    {
    	this.attacker.getNavigation().moveTo(this.path, 1D);
		this.attacker.setAggressive(true);
		
		if (this.defensive && ModCapabilities.getItemCapability(this.attacker.getOffhandItem()) instanceof Shield && this.mobCap.canBlock())
			this.attacker.startUsingItem(InteractionHand.OFF_HAND);
    }
    
    private void stopChasing()
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
    
    private void startStrafing()
    {
    	if (this.parryStance != null && this.parry != null && this.rndmPercentage(1.0F))
    	{
    		LivingCap<?> targetCap = (LivingCap<?>)this.attacker.getTarget().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
    		if (targetCap != null && targetCap.canBeParried())
    		{
    			this.parryMode = true;
        		STCLivingMotionChange msg = new STCLivingMotionChange(this.attacker.getId(), false);
        		for (LivingMotion motion : this.parryStance.getAvailableMotions())
        		{
        			msg.put(motion, this.parryStance.getForMotion(motion));
        		}
        		ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
    		}
    	}
    	else if (ModCapabilities.getItemCapability(this.attacker.getOffhandItem()) instanceof Shield && this.mobCap.canBlock())
			this.attacker.startUsingItem(InteractionHand.OFF_HAND);
    	
    	this.strafingDir = this.strafeLength == 0 && this.getTargetRange(this.attacker.getTarget()) < this.minDist ? BACK : this.strafingDir == RIGHT ? LEFT : RIGHT;
    	this.strafingTime = 50;
    }
	
	private void strafe()
    {
		LivingEntity target = this.attacker.getTarget();
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
		else
		{
			this.attacker.getMoveControl().strafe(this.strafingDir.x, this.strafingDir.y);
			--this.strafingTime;
		}
    }
	
	private void stopStrafing()
    {
    	if (this.parryMode)
    	{
    		this.parryMode = false;
    		STCLivingMotionChange msg = new STCLivingMotionChange(this.attacker.getId(), false);
    		ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
    	}
		
		this.attacker.getMoveControl().strafe(0, 0);
		this.attacker.stopUsingItem();
		this.attacker.zza = 0;
		this.attacker.xxa = 0;
    	++this.strafeLength;
    }
    
    private void setPhase(Phase value)
    {
    	switch(this.phase)
    	{
	    	default: break;
	    	case CHASING:
	    		this.stopChasing();
	    		break;
	    	case STRAFING:
	    		this.stopStrafing();
	    		break;
    	}
    	switch(value)
    	{
	    	default: break;
	    	case CHASING:
	    		this.startChasing();
	    		break;
	    	case STRAFING:
	    		this.startStrafing();
	    		break;
    	}
    	this.phase = value;
    }
    
    private void chase()
    {
    	LivingEntity target = this.attacker.getTarget();
		this.attacker.getLookControl().setLookAt(target, 30F, 30F);
		
		if (target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1D)
		{
			if (!this.defensive && this.attacker.distanceToSqr(target) > 50D) this.attacker.setSprinting(true);
			this.attacker.getNavigation().moveTo(target, 1.0F);
		}
    }
    
    private void attack()
    {
    	boolean canExecuteAttack = this.canExecuteAttack();
    	if(!canExecuteAttack && !this.canExecuteComboAttack()) return;
    	else if (canExecuteAttack && this.combo > 0) this.combo = 0;
    	
    	AttackInstance attack = null;
    	
    	if (this.combo > 0)
    	{
    		attack = this.attacks.get(this.currentAttack);
    	}
    	else
    	{
    		double targetRange = this.getTargetRange(this.attacker.getTarget());
    		
    		// Dodge
    		if (this.dodge != null && this.dodgeTime <= 0 && targetRange <= 2.0D && this.currentAttack >= 0
    				&& this.rndmPercentage(0.25F))
    		{
    			this.mobCap.playAnimationSynchronized(this.dodge, 0);
    	    	this.dodgeTime = 3;
    	    	return;
    		}
    		// Attacks
    		for (AttackInstance a : this.attacks)
        	{
    			if (a.isValidRange(targetRange) && (attack == null
    					|| !attack.isValidRange(targetRange)
						|| this.attacker.getRandom().nextInt(10) <= a.priority)) attack = a;
        	}
    	}
    	
    	if (attack == null) return;
    	
        attack.performAttack(this.mobCap, this.combo);
        this.currentAttack = this.attacks.indexOf(attack);
        if (attack.animation.length > 1) this.combo = this.combo + 1 >= attack.animation.length ? 0 : this.combo + 1;
        this.dodgeTime--;
    }
    
    protected double getTargetRange(LivingEntity target)
    {
    	double x = target.getX() - this.attacker.getX();
    	double z = target.getZ() - this.attacker.getZ();
    	return Math.sqrt(x * x + z * z);
    }
    
    protected boolean targetInAttackRange(LivingEntity target)
    {
    	double targetRange = this.getTargetRange(target);
    	return targetRange <= this.getMaxDist(targetRange) && targetRange >= this.minDist && (this.isInSameHorizontalPosition(target) || (targetRange <= 2
    			&& target.getY() - attacker.getY() <= 1));
    }
    
    protected boolean isValidTarget(LivingEntity target)
    {
    	return target != null && target.isAlive() &&
    			!((target instanceof Player) && (((Player)target).isSpectator() || ((Player)target).isCreative()));
    }
    
    protected boolean isInSameHorizontalPosition(LivingEntity target)
    {
    	if(affectY)
    		return target.getY() - attacker.getY() <= this.yDist && target.getY() - attacker.getY() >= -this.yDist;
    	
    	return true;
    }
    
    public void stop()
    {
    	this.attacker.setSprinting(false);
    	this.attacker.stopUsingItem();
    }
    
    protected enum Phase
    {
    	NONE, CHASING, STRAFING, ATTACKING
    }
}