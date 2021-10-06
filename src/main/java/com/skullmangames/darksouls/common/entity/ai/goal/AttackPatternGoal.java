package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
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
	protected final AttackAnimation[][] attacks;
	protected final AttackAnimation dashAttack;
	private int dashCooldown;
	protected final boolean affectHorizon;
	protected int combo = 0;
	protected int currentAttack = -1;
	
	public AttackPatternGoal(MobData<?> mobdata, MobEntity attacker, double minDist, double maxDist, boolean affectHorizon, AttackAnimation attackAnimation)
	{
		this(mobdata, attacker, minDist, maxDist, affectHorizon, new AttackAnimation[][] { new AttackAnimation[] { attackAnimation } });
	}
	
	public AttackPatternGoal(MobData<?> mobdata, MobEntity attacker, double minDist, double maxDist, boolean affectHorizon, AttackAnimation[][] attackAnimations)
	{
		this(mobdata, attacker, minDist, maxDist, maxDist, affectHorizon, attackAnimations, null);
	}
	
	public AttackPatternGoal(MobData<?> mobdata, MobEntity attacker, double minDist, double maxDist, double maxDashDist, boolean affectHorizon, AttackAnimation[][] attackAnimations, @Nullable AttackAnimation dashattack)
	{
		this.attacker = attacker;
		this.mobdata = mobdata;
		this.minDist = minDist * minDist;
		this.maxDist = maxDist * maxDist;
		this.maxDashDist = maxDashDist * maxDashDist;
		this.attacks = attackAnimations;
		this.dashAttack = dashattack;
		this.affectHorizon = affectHorizon;
		this.setFlags(EnumSet.noneOf(Flag.class));
		this.dashCooldown = 0;
	}
	
	@Override
    public boolean canUse()
    {
		if (this.attacks.length <= 0) return false;
		LivingEntity LivingEntity = this.attacker.getTarget();
		return isValidTarget(LivingEntity) && isTargetInRange(LivingEntity);
    }

    @Override
    public boolean canContinueToUse()
    {
    	LivingEntity livingEntity = this.attacker.getTarget();
    	return this.isValidTarget(livingEntity) && (this.isTargetInRange(livingEntity) || this.isTargetInDashRange(livingEntity));
    }
    
    @Override
    public void start() {}
    
    protected boolean canExecuteAttack()
    {
    	return !mobdata.isInaction();
    }
    
    protected boolean canExecuteComboAttack()
    {
    	return this.combo > 0 && this.mobdata.getEntityState().getContactLevel() == 3;
    }
    
    @Override
    public void tick()
    {
    	boolean canExecuteAttack = this.canExecuteAttack();
    	if(!canExecuteAttack && !this.canExecuteComboAttack()) return;
    	else if (canExecuteAttack && this.combo > 0) this.combo = 0;
    	
    	AttackAnimation animation;
        
    	if (this.dashAttack != null && this.dashCooldown == 0 && !this.isTargetInRange(this.attacker.getTarget()) && this.isTargetInDashRange(this.attacker.getTarget()))
    	{
    		if (this.combo > 0) return;
    		animation = this.dashAttack;
    		this.dashCooldown = 5;
    	}
    	else
    	{
    		if (this.combo <= 0) this.currentAttack = this.attacker.getRandom().nextInt(this.attacks.length);
    		animation = this.attacks[this.currentAttack][this.combo];
    		if (this.attacks[this.currentAttack].length > 1) this.combo = this.combo >= this.attacks[this.currentAttack].length - 1 ? 0 : this.combo + 1;
    	}
        
        if (animation == null) return;
        
        mobdata.getServerAnimator().playAnimation(animation, 0);
    	mobdata.updateInactionState();
    	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(animation.getId(), attacker.getId(), 0, attacker.getTarget().getId()), attacker);
    	if (this.dashCooldown > 0) this.dashCooldown--;
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