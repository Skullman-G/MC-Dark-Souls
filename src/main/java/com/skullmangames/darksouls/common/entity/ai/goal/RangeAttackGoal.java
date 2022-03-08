package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.HumanoidData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class RangeAttackGoal<T extends Mob & RangedAttackMob, D extends HumanoidData<T>> extends Goal
{
	private final T entity;
	private final D entitydata;
	private LivingEntity chasingTarget;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;

    public RangeAttackGoal(D entitydata, int attackCooldown, float maxAttackDist)
    {
        this.entitydata = entitydata;
        this.entity = entitydata.getOriginalEntity();
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
        return (this.entity.getTarget() == null && this.chasingTarget == null) ? false : this.isBowInMainhand() && !this.entitydata.isInaction();
    }
    
    protected boolean isBowInMainhand()
    {
        ItemStack main = this.entity.getMainHandItem();
        return main.getItem() instanceof BowItem;
    }
    
    @Override
    public boolean canContinueToUse()
    {
        return (this.canUse() || (!this.entity.getNavigation().isStuck())) && this.isBowInMainhand() && !entitydata.isInaction();
    }

    @Override
    public void start()
    {
        super.start();
        this.entity.setAggressive(true);
    }

    @Override
    public void stop()
    {
        super.stop();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.stopUsingItem();
        this.entity.getMoveControl().strafe(0, 0);
    	this.entity.getNavigation().stop();
    	this.entity.setAggressive(false);
        if(!entitydata.isInaction())
        {
        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(-1, entity.getId(), 0.0F), entity);
        }
    }
    
	@Override
    public void tick()
    {
        LivingEntity target = this.entity.getTarget();
        
        if (target != null)
        {
            double targetDistance = this.entity.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
            boolean canSee = this.entity.getSensing().hasLineOfSight(target);
            boolean saw = this.seeTime > 0;
            this.chasingTarget = target;
            
            if (canSee != saw)
                this.seeTime = 0;

            if (canSee)
                ++this.seeTime;
            else
                --this.seeTime;

            if (this.entity.isUsingItem() || this.entitydata.isInaction())
            {
                this.entity.getNavigation().stop();
            }
            else if (this.seeTime >= 20)
            {
            	if (targetDistance <= (double)((this.maxAttackDistance * 1.5F) / 2))
            	{
            		Vec3 tpos = target.position();
                	Vec3 apos = this.entity.position();
                	double x = apos.x + (apos.x - tpos.x);
                	double z = apos.z + (apos.z - tpos.z);
                	this.entity.getNavigation().moveTo(x, apos.y, z, 1.0D);
            	}
            	else if (targetDistance <= (double)this.maxAttackDistance * 1.5F)
            	{
            		this.entity.getNavigation().stop();
            	}
            }
            else
            {
                this.entity.getNavigation().moveTo(target, 1.0D);
            }

            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);

            if (this.entity.isUsingItem())
            {
                if (!canSee && this.seeTime < -60)
                    this.entity.stopUsingItem();
                else if(canSee)
                {
                    int i = this.entity.getTicksUsingItem();
                    if (i >= 20)
                    {
                        this.entity.stopUsingItem();
                        ((RangedAttackMob)this.entity).performRangedAttack(target, BowItem.getPowerForTime(i));
                        ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_REBOUND.getId(), entity.getId(), 0.0F, true), entity);
                        this.attackTime = this.attackCooldown;
                    }
                }
            }
            else if (--this.attackTime <= 0 && this.seeTime >= -60)
            {
            	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_AIM.getId(), entity.getId(), 0.0F, true), entity);
                this.entity.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.entity, item -> item instanceof BowItem));
            }
        }
        else if(this.chasingTarget != null)
        {
        	double targetDistance = this.entity.distanceToSqr(chasingTarget.getX(), chasingTarget.getBoundingBox().minY, chasingTarget.getZ());
        	
        	if(targetDistance <= (double)this.maxAttackDistance * 2.0F && this.seeTime >= 20)
        	{
        		if(targetDistance <= (double)this.maxAttackDistance)
        			this.chasingTarget = null;
        		else
        		{
        			this.entity.stopUsingItem();
            		this.entity.getNavigation().moveTo(chasingTarget, 1.0D);
        		}
        		return;
        	}
        	
        	this.chasingTarget = null;
        	this.entity.stopUsingItem();
        }
    }
}