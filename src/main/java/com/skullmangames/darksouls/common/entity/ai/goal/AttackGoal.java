package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.item.Shield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class AttackGoal extends Goal
{
	protected final Mob mob;
	private final MobCap<?> mobCap;
	protected final int yDist;
	protected final boolean affectY;
	
	protected StaticAnimation dodge;
	private int dodgeTime;
	
	protected int combo = 0;
	protected int currentAttack = -1;
	
	protected final List<AttackInstance> attacks = new ArrayList<>();
	
	protected final boolean defensive;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	
	protected Phase phase = Phase.NONE;
	
	private long lastCanUseCheck;
	
	public AttackGoal(MobCap<?> mobCap, float minDist, boolean affectY, boolean defensive)
	{
		this(mobCap, minDist, 0, affectY, defensive);
	}
	
	public AttackGoal(MobCap<?> mobCap, float minDist, int yDist, boolean affectY, boolean defensive)
	{
		this.mobCap = mobCap;
		this.mob = mobCap.getOriginalEntity();
		this.yDist = yDist;
		this.affectY = affectY;
		this.defensive = defensive;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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
		float maxDist = 0.0F;
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
		
		long i = this.mob.level.getGameTime();
		if (i - this.lastCanUseCheck < 20L) return false;
		this.lastCanUseCheck = i;
		
		//Check if mob is holding the right weapon
		WeaponCap weaponCap = this.mobCap.getHeldWeaponCap(InteractionHand.MAIN_HAND);
		if (weaponCap == null || !weaponCap.getWeaponCategory().isMelee()) return false;
		
		LivingEntity target = this.mob.getTarget();
		if (!this.isValidTarget(target)) return false;
		this.updatePhase();
		return this.phase != Phase.NONE;
    }

    @Override
    public boolean canContinueToUse()
    {
    	LivingEntity target = this.mob.getTarget();
    	if (!this.isValidTarget(target)) return false;
    	this.updatePhase();
		return this.phase != Phase.NONE;
    }
    
    @Override
    public boolean isInterruptable()
    {
    	return false;
    }
    
    protected boolean canExecuteAttack()
    {
    	return !this.mobCap.isInaction() && this.mobCap.getEntityState().getContactLevel() != 3;
    }
    
    protected boolean canExecuteComboAttack()
    {
    	return this.combo > 0
    			&& this.currentAttack > -1
    			&& this.attacks.get(this.currentAttack).isValidRange(this.getTargetRange(this.mob.getTarget()))
    			&& this.mobCap.getEntityState().getContactLevel() == 3;
    }
    
    protected void updatePhase()
    {
    	LivingEntity target = this.mob.getTarget();
    	boolean inAttackRange = this.targetInAttackRange(target);
    	switch(this.phase)
    	{
    	default:
    	case NONE:
    		if (!this.mobCap.isInaction())
    		{
    			if (inAttackRange) this.setPhase(Phase.ATTACKING);
    			else this.setPhase(Phase.CHASING);
    		}
    		break;
    	case CHASING:
    		if (this.mobCap.isInaction()) this.setPhase(Phase.NONE);
    		else if (inAttackRange && !this.pathBlocked(target)) this.setPhase(Phase.ATTACKING);
    		break;
    	case ATTACKING:
    		if (!this.mobCap.isInaction()
    				&& this.mobCap.rndmPercentage(0.3F)
    				&& this.currentAttack > -1
    				&& !this.canExecuteComboAttack()) this.setPhase(Phase.NONE);
    		else if (!inAttackRange || this.pathBlocked(target)) this.setPhase(Phase.CHASING);
    		break;
    	}
    }
    
    private boolean pathBlocked(LivingEntity target)
    {
    	double distX = Math.abs(target.getX() - this.mob.getX());
    	double distZ = Math.abs(target.getZ() - this.mob.getZ());
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
	    	case ATTACKING:
	    		this.attack();
	    		break;
    	}
    }
    
    private void startChasing()
    {
    	this.mob.setAggressive(true);
		
		if (this.defensive && ModCapabilities.getItemCapability(this.mob.getOffhandItem()) instanceof Shield && this.mobCap.canBlock())
			this.mob.startUsingItem(InteractionHand.OFF_HAND);
    }
    
    private void stopChasing()
    {
    	LivingEntity livingentity = this.mob.getTarget();
		if(livingentity != null && !livingentity.isAttackable())
		{
			this.mob.setTarget(null);
		}
		
		this.mob.setSprinting(false);
		this.mob.stopUsingItem();
		this.mob.setAggressive(false);
		this.mob.getNavigation().stop();
    }
    
    private void setPhase(Phase value)
    {
    	switch(this.phase)
    	{
	    	default: break;
	    	case CHASING:
	    		this.stopChasing();
	    		break;
    	}
    	switch(value)
    	{
	    	default: break;
	    	case CHASING:
	    		this.startChasing();
	    		break;
    	}
    	this.phase = value;
    }
    
    private void chase()
    {
    	LivingEntity target = this.mob.getTarget();
		this.mob.getLookControl().setLookAt(target, 30F, 30F);
		
		if (target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1D)
		{
			if (!this.defensive && this.mob.distanceToSqr(target) > 50D && !this.mob.isSprinting())
			{
				this.mob.setSprinting(true);
			}
			this.mob.getNavigation().moveTo(target, 1.0F);
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
    		double targetRange = this.getTargetRange(this.mob.getTarget());
    		
    		// Dodge
    		if (this.dodge != null && this.dodgeTime <= 0 && targetRange <= 2.0D && this.currentAttack >= 0
    				&& this.mobCap.rndmPercentage(0.25F))
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
						|| this.mob.getRandom().nextInt(10) <= a.priority)) attack = a;
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
    	double x = target.getX() - this.mob.getX();
    	double z = target.getZ() - this.mob.getZ();
    	return Math.sqrt(x * x + z * z);
    }
    
    protected boolean targetInAttackRange(LivingEntity target)
    {
    	double targetRange = this.getTargetRange(target);
    	return targetRange <= this.getMaxDist(targetRange) && (this.isInSameHorizontalPosition(target) || (targetRange <= 2
    			&& target.getY() - mob.getY() <= 1));
    }
    
    protected boolean isValidTarget(LivingEntity target)
    {
    	return target != null && target.isAlive() &&
    			!((target instanceof Player) && (((Player)target).isSpectator() || ((Player)target).isCreative()));
    }
    
    protected boolean isInSameHorizontalPosition(LivingEntity target)
    {
    	if(affectY)
    		return target.getY() - mob.getY() <= this.yDist && target.getY() - mob.getY() >= -this.yDist;
    	
    	return true;
    }
    
    public void stop()
    {
    	this.currentAttack = -1;
    	this.mob.setSprinting(false);
    	this.mob.stopUsingItem();
    }
    
    protected enum Phase
    {
    	NONE, CHASING, ATTACKING
    }
}