package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import com.skullmangames.darksouls.common.capability.entity.MobData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class AttackPatternGoal extends Goal
{
	protected final MobEntity attacker;
	protected final MobData<?> mobdata;
	protected final float minDist;
	protected float maxDist;
	protected final boolean affectHorizon;
	
	protected int combo = 0;
	protected int currentAttack = -1;
	
	protected final List<AttackInstance> attacks = new ArrayList<>();
	
	public AttackPatternGoal(MobData<?> mobdata, float minDist, boolean affectHorizon)
	{
		this.mobdata = mobdata;
		this.attacker = this.mobdata.getOriginalEntity();
		this.minDist = minDist * minDist;
		this.maxDist = this.minDist;
		this.affectHorizon = affectHorizon;
		this.setFlags(EnumSet.noneOf(Flag.class));
	}
	
	public AttackPatternGoal addAttack(AttackInstance attack)
	{
		this.attacks.add(attack);
		if (attack.range > this.maxDist) this.maxDist = attack.range;
		return this;
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
    	return !mobdata.isInaction() && this.mobdata.getEntityState().getContactLevel() != 3;
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
    	return targetRange <= this.maxDist && targetRange >= this.minDist && this.isInSameHorizontalPosition(target);
    }
    
    protected boolean isValidTarget(LivingEntity attackTarget)
    {
    	return attackTarget != null && attackTarget.isAlive() &&
    			!((attackTarget instanceof PlayerEntity) && (((PlayerEntity)attackTarget).isSpectator() || ((PlayerEntity)attackTarget).isCreative()));
    }
    
    protected boolean isInSameHorizontalPosition(LivingEntity attackTarget)
    {
    	if(affectHorizon)
    		return attackTarget.getY() - attacker.getY() <= 1.0F && attackTarget.getY() - attacker.getY() >= -1.0F;
    	
    	return true;
    }
}