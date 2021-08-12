package com.skullmangames.darksouls.common.entity.ai.goal;

import java.util.EnumSet;

import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.LinkAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.BipedMobData;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimationTarget;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

public class RangeAttackMobGoal extends Goal
{
    protected final MobEntity entityHost;
    protected final BipedMobData<?> entitydata;
    protected final StaticAnimation shotAnimation;
    protected final IRangedAttackMob rangedAttackEntityHost;
    protected LivingEntity attackTarget;
    protected int rangedAttackTime;
    protected final double entityMoveSpeed;
    protected int seeTime;
    protected final int attackIntervalMin;
    protected final int maxRangedAttackTime;
    protected final float attackRadius;
    protected final float maxAttackDistance;
    protected final int animationFrame;

    public RangeAttackMobGoal(IRangedAttackMob attacker, BipedMobData<?> entitydata, StaticAnimation shotanimation, 
    		double movespeed, int maxAttackTime, float maxAttackDistanceIn, int animationFrame)
    {
        this(attacker, entitydata, shotanimation, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn, animationFrame);
    }

    public RangeAttackMobGoal(IRangedAttackMob attacker, BipedMobData<?> entitydata, StaticAnimation shotanimation, 
    		double movespeed, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn, int animationFrame)
    {
        this.rangedAttackTime = -1;
        
        if (!(attacker instanceof LivingEntity))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        else
        {
        	this.entitydata = entitydata;
        	this.shotAnimation = shotanimation;
            this.rangedAttackEntityHost = attacker;
            this.entityHost = (MobEntity)attacker;
            this.entityMoveSpeed = movespeed;
            this.attackIntervalMin = p_i1650_4_;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.animationFrame = animationFrame;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }
    }

    @Override
    public boolean canUse()
    {
        LivingEntity LivingEntity = this.entityHost.getTarget();

        if (LivingEntity == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = LivingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse()
    {
    	if(this.entitydata.isInaction())
    	{
    		DynamicAnimation animation = this.entitydata.getAnimator().getPlayer().getPlay();
    		if(animation != this.shotAnimation && (animation instanceof LinkAnimation && ((LinkAnimation)animation).getNextAnimation() != this.shotAnimation))
    			return false;
    	}
    	
        return this.canUse() || !this.entityHost.getNavigation().isStuck();
    }

    @Override
    public void stop()
    {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    @Override
    public void tick()
    {
        double d0 = this.entityHost.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getZ());
        boolean flag = this.entityHost.getSensing().canSee(this.attackTarget);

        if(flag)
            ++this.seeTime;
        else
            this.seeTime = 0;

        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20)
            this.entityHost.getNavigation().stop();
        else
            this.entityHost.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
        
        this.entityHost.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
        
        if(--this.rangedAttackTime == this.animationFrame && !this.entitydata.isInaction() && flag)
        {
        	entitydata.getServerAnimator().playAnimation(shotAnimation, 0);
        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(shotAnimation.getId(), entityHost.getId(), 0, attackTarget.getId()), entityHost);
        }
        else if(this.rangedAttackTime == 0)
        {
            if(!flag) return;
            float f = MathHelper.sqrt(d0) / this.attackRadius;
            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
            this.rangedAttackEntityHost.performRangedAttack(this.attackTarget, lvt_5_1_);
            this.rangedAttackTime = MathHelper.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
        else if(this.rangedAttackTime < 0)
        {
        	float f2 = MathHelper.sqrt(d0) / this.attackRadius;
            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
    }
}