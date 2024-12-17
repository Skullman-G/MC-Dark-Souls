package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;

public abstract class FlyingMobCap<T extends Mob> extends MobCap<T>
{
	protected FlyingMoveControl flyingMoveControl;
	protected MoveControl moveControl;
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.flyingMoveControl = new FlyingMoveControl(this.orgEntity, 60, true);
		this.moveControl = this.orgEntity.getMoveControl();
	}
	
	public void setFlying(boolean value)
	{
		if (value)
		{
			if (this.orgEntity.isOnGround())
			{
				this.playAnimationSynchronized(this.getFlyingStartAnim(), 1.0F);
			}
			this.orgEntity.moveControl = this.flyingMoveControl;
		}
		else
		{
			this.playAnimationSynchronized(this.getFlyingStopAnim(), 1.0F);
			this.orgEntity.moveControl = this.moveControl;
		}
		this.orgEntity.setNoGravity(value);
	}
	
	protected abstract StaticAnimation getFlyingStartAnim();
	protected abstract StaticAnimation getFlyingStopAnim();
	
	public boolean isFlying()
	{
		return this.orgEntity.isNoGravity();
	}
}
