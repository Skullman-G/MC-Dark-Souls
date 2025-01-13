package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.entity.Mob;

public abstract class FlyingMobCap<T extends Mob> extends MobCap<T>
{
	public void setFlying(boolean value)
	{
		if (value == this.isFlying()) return;
		
		this.orgEntity.setNoGravity(value);
		if (value)
		{
			if (this.orgEntity.isOnGround())
			{
				this.playAnimationSynchronized(this.getFlyingStartAnim(), 1.0F);
			}
		}
		else
		{
			this.playAnimationSynchronized(this.getFlyingStopAnim(), 1.0F);
		}
	}
	
	protected abstract StaticAnimation getFlyingStartAnim();
	protected abstract StaticAnimation getFlyingStopAnim();
	
	public boolean isFlying()
	{
		return this.orgEntity.isNoGravity();
	}
}
