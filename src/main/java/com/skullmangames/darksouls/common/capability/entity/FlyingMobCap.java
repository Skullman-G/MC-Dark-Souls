package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;

import net.minecraft.world.entity.Mob;

public abstract class FlyingMobCap<T extends Mob> extends MobCap<T>
{
	private boolean flying;
	
	public boolean isFlying()
	{
		return this.flying;
	}
	
	public void setFlying(boolean value)
	{
		if (value)
			this.playAnimationSynchronized(this.getFlyingStartAnim(), 1.0F);
		else
			this.playAnimationSynchronized(this.getFlyingStopAnim(), 1.0F);
		this.flying = value;
	}
	
	protected abstract StaticAnimation getFlyingStartAnim();
	protected abstract StaticAnimation getFlyingStopAnim();
}
