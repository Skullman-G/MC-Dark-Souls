package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.capability.entity.BipedMobData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;

public class ArcherGoal<T extends MobEntity & IRangedAttackMob> extends Goal
{
	private final T entity;
	private final BipedMobData<?> entitydata;
	private LivingEntity chasingTarget;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;

    public ArcherGoal(BipedMobData<?> entitydata, T p_i47515_1_, double p_i47515_2_, int p_i47515_4_, float p_i47515_5_)
    {
        this.entity = p_i47515_1_;
        this.entitydata = entitydata;
        this.moveSpeedAmp = p_i47515_2_;
        this.attackCooldown = p_i47515_4_;
        this.maxAttackDistance = p_i47515_5_ * p_i47515_5_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }
    
    public void setAttackCooldown(int p_189428_1_)
    {
        this.attackCooldown = p_189428_1_;
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
            boolean canSee = this.entity.getSensing().canSee(target);
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
            		Vector3d tpos = target.position();
                	Vector3d apos = this.entity.position();
                	double x = apos.x + (apos.x - tpos.x);
                	double z = apos.z + (apos.z - tpos.z);
                	this.entity.getNavigation().moveTo(x, apos.y, z, this.moveSpeedAmp);
            	}
            	else if (targetDistance <= (double)this.maxAttackDistance * 1.5F)
            	{
            		this.entity.getNavigation().stop();
            	}
            }
            else
            {
                this.entity.getNavigation().moveTo(target, this.moveSpeedAmp);
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
                        ((IRangedAttackMob)this.entity).performRangedAttack(target, BowItem.getPowerForTime(i));
                        ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_REBOUND.getId(), entity.getId(), 0.0F, true), entity);
                        this.attackTime = this.attackCooldown;
                    }
                }
            }
            else if (--this.attackTime <= 0 && this.seeTime >= -60)
            {
            	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_BOW_AIM.getId(), entity.getId(), 0.0F, true), entity);
                this.entity.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.entity, Items.BOW));
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
            		this.entity.getNavigation().moveTo(chasingTarget, this.moveSpeedAmp);
        		}
        		return;
        	}
        	
        	this.chasingTarget = null;
        	this.entity.stopUsingItem();
        }
    }
}