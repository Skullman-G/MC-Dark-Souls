package com.skullmangames.darksouls.common.entities.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.entities.BipedMobData;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.play.server.STCPlayAnimation;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

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
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

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
        net.minecraft.item.ItemStack main = this.entity.getMainHandItem();
        net.minecraft.item.ItemStack off  = this.entity.getMainHandItem();
        return main.getItem() instanceof BowItem || off.getItem() instanceof BowItem;
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
        LivingEntity LivingEntity = this.entity.getTarget();
        
        if (LivingEntity != null)
        {
            double d0 = this.entity.distanceToSqr(LivingEntity.getX(), LivingEntity.getBoundingBox().minY, LivingEntity.getZ());
            boolean flag = this.entity.getSensing().canSee(LivingEntity);
            boolean flag1 = this.seeTime > 0;
            this.chasingTarget = LivingEntity;
            
            if (flag != flag1)
                this.seeTime = 0;

            if (flag)
                ++this.seeTime;
            else
                --this.seeTime;

            if (d0 <= (double)this.maxAttackDistance * 1.5F && this.seeTime >= 20)
            {
                this.entity.getNavigation().stop();
                ++this.strafingTime;
            }
            else
            {
                this.entity.getNavigation().moveTo(LivingEntity, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double)this.entity.getRandom().nextFloat() < 0.3D)
                    this.strafingClockwise = !this.strafingClockwise;

                if ((double)this.entity.getRandom().nextFloat() < 0.3D)
                    this.strafingBackwards = !this.strafingBackwards;

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1)
            {
                if (d0 > (double)(this.maxAttackDistance * 0.75F))
                    this.strafingBackwards = false;
                else if (d0 < (double)(this.maxAttackDistance * 0.25F))
                    this.strafingBackwards = true;
                
                if(this.entity.getTicksUsingItem() < 10)
                	this.entity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                else
                	this.entity.getMoveControl().strafe(0, 0);
                
                /*this.entity.getLookControl().setLookAt(LivingEntity, entity.getHorizontalFaceSpeed(), entity.getVerticalFaceSpeed());
                this.entity.faceEntity(LivingEntity, 30.0F, 30.0F);*/
            }
            else
                this.entity.getLookControl().setLookAt(LivingEntity, 30.0F, 30.0F);

            if (this.entity.isUsingItem())
            {
                if (!flag && this.seeTime < -60)
                    this.entity.stopUsingItem();
                else if(flag)
                {
                    int i = this.entity.getTicksUsingItem();
                    if (i >= 20)
                    {
                        this.entity.stopUsingItem();
                        //((IRangedAttackMob)this.entity).attackEntityWithRangedAttack(LivingEntity, BowItem.getArrowVelocity(i));
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
        else
        {
        	if(chasingTarget != null)
        	{
        		double d0 = this.entity.distanceToSqr(chasingTarget.getX(), chasingTarget.getBoundingBox().minY, chasingTarget.getZ());
            	
            	if(d0 <= (double)this.maxAttackDistance * 2.0F && this.seeTime >= 20)
            	{
            		if(d0 <= (double)this.maxAttackDistance)
            			this.chasingTarget = null;
            		else
            		{
            			this.entity.stopUsingItem();
                		this.entity.getNavigation().moveTo(chasingTarget, this.moveSpeedAmp);
            		}
            		return;
            	}
        	}
        	
        	this.chasingTarget = null;
        }
    }
}