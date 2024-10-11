package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.capability.entity.FlyingMobCap;

public class FlyingAttackGoal extends AttackGoal
{
	private final FlyingMobCap<?> mobCap;
	
	public FlyingAttackGoal(FlyingMobCap<?> mobCap, float minDist, int yDist, boolean affectY, boolean defensive,
			boolean shouldStrafe)
	{
		super(mobCap, minDist, yDist, affectY, defensive, shouldStrafe);
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
		    	case STRAFING:
		    		this.strafe();
		    		break;
		    	case ATTACKING:
		    		this.attack();
		    		break;
	    	}
		}
		else
		{
			if (this.getTargetRange(this.attacker.getTarget()) > 50)
			{
				this.mobCap.setFlying(true);
			}
			else super.tick();
		}
    }
	
	private void chase() {}
	private void strafe() {}
	private void attack() {}
}
