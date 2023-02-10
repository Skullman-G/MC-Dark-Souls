package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.animation.types.attack.AttackAnimation;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import net.minecraft.util.math.MathHelper;

public class AttackInstance
{
	protected final AttackAnimation[] animation;
	protected final float offset;
	protected final float range;
	protected final int priority;
	
	public AttackInstance(int priority, float range, AttackAnimation... animation)
	{
		this(priority, 0.0F, range, animation);
	}
	
	public AttackInstance(int priority, float offset, float range, AttackAnimation... animation)
	{
		this.animation = animation;
		this.offset = offset;
		this.range = range;
		this.priority = MathHelper.clamp(priority, 0, 9);
	}
	
	public boolean isValidRange(double targetRange)
	{
		return offset <= targetRange && targetRange <= range;
	}
	
	public void performAttack(MobCap<?> attackerCap, int combo)
	{
    	attackerCap.playAnimationSynchronized(this.animation[combo], 0);
	}
}
