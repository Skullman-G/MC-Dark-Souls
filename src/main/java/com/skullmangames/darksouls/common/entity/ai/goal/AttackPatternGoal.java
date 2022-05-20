package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimationAndSetTarget;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class AttackPatternGoal extends Goal
{
	protected final Mob attacker;
	protected final MobCap<?> mobdata;
	protected final float minDist;
	protected final int yDist;
	protected final boolean affectHorizon;
	
	protected final StaticAnimation dodge;
	private int dodgeTime;
	
	protected int combo = 0;
	protected int currentAttack = -1;
	
	protected final List<AttackInstance> attacks = new ArrayList<>();
	
	public AttackPatternGoal(MobCap<?> mobdata, float minDist, boolean affectHorizon)
	{
		this(mobdata, minDist, 0, affectHorizon, null);
	}
	
	public AttackPatternGoal(MobCap<?> mobdata, float minDist, int yDist, boolean affectHorizon)
	{
		this(mobdata, minDist, yDist, affectHorizon, null);
	}
	
	public AttackPatternGoal(MobCap<?> mobdata, float minDist, boolean affectHorizon, StaticAnimation dodge)
	{
		this(mobdata, minDist, 0, affectHorizon, dodge);
	}
	
	public AttackPatternGoal(MobCap<?> mobdata, float minDist, int yDist, boolean affectHorizon, StaticAnimation dodge)
	{
		this.mobdata = mobdata;
		this.attacker = this.mobdata.getOriginalEntity();
		this.minDist = minDist * minDist;
		this.yDist = yDist;
		this.affectHorizon = affectHorizon;
		this.dodge = dodge;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}
	
	public AttackPatternGoal addAttack(AttackInstance attack)
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
		return this.isValidTarget(target) && this.isTargetInRange(target);
    }

    @Override
    public boolean canContinueToUse()
    {
    	LivingEntity target = this.attacker.getTarget();
    	return this.isValidTarget(target) && this.isTargetInRange(target);
    }
    
    protected boolean canExecuteAttack()
    {
    	return !this.mobdata.isInaction() && this.mobdata.getEntityState().getContactLevel() != 3;
    }
    
    protected boolean canExecuteComboAttack()
    {
    	return this.combo > 0
    			&& this.currentAttack > -1
    			&& this.attacks.get(this.currentAttack).isValidRange(this.getTargetRange(this.attacker.getTarget()))
    			&& this.mobdata.getEntityState().getContactLevel() == 3;
    }
    
    @Override
    public void tick()
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
    		if (this.dodge != null && this.dodgeTime <= 0 && targetRange <= 2.0D && this.attacker.getRandom().nextBoolean())
    		{
    			mobdata.getServerAnimator().playAnimation(this.dodge, 0);
    	    	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationAndSetTarget(this.dodge, 0, mobdata), mobdata.getOriginalEntity());
    	    	this.dodgeTime = 3;
    	    	return;
    		}
    		for (AttackInstance a : this.attacks)
        	{
    			if (a.isValidRange(targetRange) && (attack == null
    					|| !attack.isValidRange(targetRange)
						|| this.attacker.getRandom().nextInt(10) <= a.priority)) attack = a;
        	}
    	}
    	
    	if (attack == null) return;
    	
        attack.performAttack(this.mobdata, this.combo);
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
    
    protected boolean isTargetInRange(LivingEntity target)
    {
    	double targetRange = this.getTargetRange(target);
    	return targetRange <= this.getMaxDist(targetRange) && targetRange >= this.minDist && this.isInSameHorizontalPosition(target);
    }
    
    protected boolean isValidTarget(LivingEntity attackTarget)
    {
    	return attackTarget != null && attackTarget.isAlive() &&
    			!((attackTarget instanceof Player) && (((Player)attackTarget).isSpectator() || ((Player)attackTarget).isCreative()));
    }
    
    protected boolean isInSameHorizontalPosition(LivingEntity attackTarget)
    {
    	if(affectHorizon)
    		return attackTarget.getY() - attacker.getY() <= this.yDist && attackTarget.getY() - attacker.getY() >= -this.yDist;
    	
    	return true;
    }
}