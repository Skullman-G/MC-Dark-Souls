package com.skullmangames.darksouls.common.entity.ai.goal;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.MobCap;

public class SpellAttackInstance
{
	protected final StaticAnimation animation;
	protected int cooldown;
	private final SpellTest canUse;
	
	public SpellAttackInstance(StaticAnimation animation, SpellTest canUse)
	{
		this.animation = animation;
		this.canUse = canUse;
	}
	
	public boolean canUse(MobCap<?> mobCap)
	{
		if (this.cooldown > 0) return false;
		return this.canUse.test(mobCap);
	}
	
	public void performSpell(MobCap<?> attackerCap)
	{
    	attackerCap.playAnimationSynchronized(this.animation, 0);
    	this.cooldown = 100;
	}
	
	@FunctionalInterface
	public interface SpellTest
	{
		boolean test(MobCap<?> mobCap);
	}
}
