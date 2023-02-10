package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.math.vector.Vector3d;

public abstract class RangeAttackGoal<T extends MobEntity & IRangedAttackMob, D extends HumanoidCap<T>> extends Goal
{
	protected final T mob;
	protected final D entityCap;
	protected LivingEntity chasingTarget;
	protected int attackCooldown;
	protected final float maxAttackDistance;
	protected int attackTime = -1;
	protected int seeTime;

    public RangeAttackGoal(D entityCap, int attackCooldown, float maxAttackDist)
    {
        this.entityCap = entityCap;
        this.mob = entityCap.getOriginalEntity();
        this.attackCooldown = attackCooldown;
        this.maxAttackDistance = maxAttackDist * maxAttackDist;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    
    public void setAttackCooldown(int value)
    {
        this.attackCooldown = value;
    }
    
    @Override
    public boolean canUse()
    {
        return (this.mob.getTarget() == null && this.chasingTarget == null) ? false : this.isHoldingRightWeapon() && !this.entityCap.isInaction();
    }
    
    protected abstract boolean isHoldingRightWeapon();
    
    @Override
    public boolean canContinueToUse()
    {
        return (this.canUse() || (!this.mob.getNavigation().isStuck())) && this.isHoldingRightWeapon() && !entityCap.isInaction();
    }

    @Override
    public void start()
    {
        super.start();
        this.mob.setAggressive(true);
    }

    @Override
    public void stop()
    {
        super.stop();
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.stopUsingItem();
        this.mob.getMoveControl().strafe(0, 0);
    	this.mob.getNavigation().stop();
    	this.mob.setAggressive(false);
        if(!entityCap.isInaction())
        {
        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.DUMMY_ANIMATION, mob.getId(), 0.0F), mob);
        }
    }
    
	@Override
    public void tick()
    {
        LivingEntity target = this.mob.getTarget();
        
        if (target != null)
        {
            double targetDistance = this.mob.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
            boolean canSee = this.mob.getSensing().canSee(target);
            boolean saw = this.seeTime > 0;
            this.chasingTarget = target;
            
            if (canSee != saw)
                this.seeTime = 0;

            if (canSee)
                ++this.seeTime;
            else
                --this.seeTime;

            if (this.mob.isUsingItem() || this.entityCap.isInaction())
            {
                this.mob.getNavigation().stop();
            }
            else if (this.seeTime >= 20)
            {
            	if (targetDistance <= (double)((this.maxAttackDistance * 1.5F) / 2))
            	{
            		Vector3d tpos = target.position();
                	Vector3d apos = this.mob.position();
                	double x = apos.x + (apos.x - tpos.x);
                	double z = apos.z + (apos.z - tpos.z);
                	this.mob.getNavigation().moveTo(x, apos.y, z, 1.0D);
            	}
            	else if (targetDistance <= (double)this.maxAttackDistance * 1.5F)
            	{
            		this.mob.getNavigation().stop();
            	}
            }
            else
            {
                this.mob.getNavigation().moveTo(target, 1.0D);
            }

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.mob.lookAt(target, 30.0F, 30.0F);

            if (this.mob.isUsingItem())
            {
                if (!canSee && this.seeTime < -60)
                    this.mob.stopUsingItem();
                else if(canSee)
                {
                    this.performAttack();
                }
            }
            else if (--this.attackTime <= 0 && this.seeTime >= -60)
            {
            	this.aim();
            }
        }
        else if(this.chasingTarget != null)
        {
        	double targetDistance = this.mob.distanceToSqr(chasingTarget.getX(), chasingTarget.getBoundingBox().minY, chasingTarget.getZ());
        	
        	if(targetDistance <= (double)this.maxAttackDistance * 2.0F && this.seeTime >= 20)
        	{
        		if(targetDistance <= (double)this.maxAttackDistance)
        			this.chasingTarget = null;
        		else
        		{
        			this.mob.stopUsingItem();
            		this.mob.getNavigation().moveTo(chasingTarget, 1.0D);
        		}
        		return;
        	}
        	
        	this.chasingTarget = null;
        	this.mob.stopUsingItem();
        }
    }
	
	protected abstract void performAttack();
	
	protected abstract void aim();
}