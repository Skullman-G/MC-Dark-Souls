package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.MobData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTarget;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class AttackPatternGoal extends Goal
{
	protected final MobEntity attacker;
	protected final MobData<?> mobdata;
	protected final double minDist;
	protected final double maxDist;
	protected final double maxDashDist;
	protected final List<AttackAnimation> lightAttack;
	protected final List<AttackAnimation> otherAttacks;
	protected final AttackAnimation dashAttack;
	protected final boolean affectHorizon;
	protected int combo;
	
	
	
	public AttackPatternGoal(MobData<?> mobdata, MobEntity attacker, double minDist, double maxDist, boolean affectHorizon, List<AttackAnimation> lightattack)
	{
		this(mobdata, attacker, minDist, maxDist, maxDist, affectHorizon, lightattack, null, null);
	}
	
	public AttackPatternGoal(MobData<?> mobdata, MobEntity attacker, double minDist, double maxDist, double maxDashDist, boolean affectHorizon, List<AttackAnimation> lightattack, @Nullable List<AttackAnimation> otherattacks, @Nullable AttackAnimation dashattack)
	{
		this.attacker = attacker;
		this.mobdata = mobdata;
		this.minDist = minDist * minDist;
		this.maxDist = maxDist * maxDist;
		this.maxDashDist = maxDashDist * maxDashDist;
		this.lightAttack = lightattack;
		this.otherAttacks = otherattacks;
		this.dashAttack = dashattack;
		this.combo = 0;
		this.affectHorizon = affectHorizon;
		this.setFlags(EnumSet.noneOf(Flag.class));
	}
	
	@Override
    public boolean canUse()
    {
		LivingEntity LivingEntity = this.attacker.getTarget();
		return isValidTarget(LivingEntity) && isTargetInRange(LivingEntity);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse()
    {
    	LivingEntity livingEntity = this.attacker.getTarget();
    	return this.isValidTarget(livingEntity) && (this.isTargetInRange(livingEntity) || this.isTargetInDashRange(livingEntity));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start()
    {
        
    }
    
    protected boolean canExecuteAttack()
    {
    	return !mobdata.isInaction();
    }
    
    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick()
    {
    	if(!this.canExecuteAttack())
    	{
    		if (this.combo > 0 && this.mobdata.getEntityState() == LivingData.EntityState.ROTATABLE_POST_DELAY || this.mobdata.getEntityState() == LivingData.EntityState.POST_DELAY);
    		else return;
    	}
    	else if (this.combo > 0) this.combo = 0;
    	
    	AttackAnimation animation;
        
    	if (this.dashAttack != null && !this.isTargetInRange(this.attacker.getTarget()) && this.isTargetInDashRange(this.attacker.getTarget()))
    	{
    		if (this.combo > 0) return;
    		animation = this.dashAttack;
    	}
    	else
    	{
    		if (this.combo > 0 || this.attacker.getRandom().nextBoolean())
        	{
        		animation = this.lightAttack.get(this.combo++);
            	this.combo %= this.lightAttack.size();
        	}
            else
            {
            	animation = this.otherAttacks.get(this.attacker.getRandom().nextInt(this.otherAttacks.size()));
            }
    	}
        
        if (animation == null) return;
        
        mobdata.getServerAnimator().playAnimation(animation, 0);
    	mobdata.updateInactionState();
    	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(animation.getId(), attacker.getId(), 0, attacker.getTarget().getId()), attacker);
    }
    
    protected boolean isTargetInRange(LivingEntity attackTarget)
    {
    	double targetRange = this.attacker.distanceToSqr(attackTarget.getX(), attackTarget.getBoundingBox().minY, attackTarget.getZ());
    	return targetRange <= this.maxDist && targetRange >= this.minDist && isInSameHorizontalPosition(attackTarget);
    }
    
    protected boolean isTargetInDashRange(LivingEntity target)
    {
    	double targetRange = this.attacker.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
    	return targetRange <= this.maxDashDist && targetRange >= this.minDist && isInSameHorizontalPosition(target);
    }
    
    protected boolean isValidTarget(LivingEntity attackTarget)
    {
    	return attackTarget != null && attackTarget.isAlive() && 
    			!((attackTarget instanceof PlayerEntity) && (((PlayerEntity)attackTarget).isSpectator() || ((PlayerEntity)attackTarget).isCreative()));
    }
    
    protected boolean isInSameHorizontalPosition(LivingEntity attackTarget)
    {
    	if(affectHorizon)
    		return Math.abs(attacker.getY() - attackTarget.getY()) <= attacker.getEyeHeight();
    	
    	return true;
    }
}