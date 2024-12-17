package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.capability.entity.FlyingMobCap;

import net.minecraft.world.entity.LivingEntity;

public class FlyingAttackGoal extends AttackGoal
{
	private final FlyingMobCap<?> mobCap;
	
	public FlyingAttackGoal(FlyingMobCap<?> mobCap, float minDist, int yDist, boolean affectY, boolean defensive)
	{
		super(mobCap, minDist, yDist, affectY, defensive);
		this.mobCap = mobCap;
	}
	
	@Override
    public void tick()
    {
		if (this.mobCap.isFlying())
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
		else
		{
			if (this.getTargetRange(this.mob.getTarget()) > 5)
			{
				this.mobCap.setFlying(true);
			}
			else super.tick();
		}
    }
	
	private void chase()
	{
		LivingEntity target = this.mob.getTarget();
		this.mob.getLookControl().setLookAt(target, 30F, 30F);
		
		if (target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1D)
		{
			this.mob.getNavigation().moveTo(target, 1.0F);
		}
	}
	
	private void attack() {}
}
