package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class LinkAnimation extends DynamicAnimation
{
	protected DynamicAnimation nextAnimation;
	protected float startsAt;

	@Override
	public void onFinish(LivingData<?> entity, boolean isEnd)
	{
		if(!isEnd)
		{
			this.nextAnimation.onFinish(entity, isEnd);
		}
		else
		{
			if (this.startsAt > 0)
			{
				entity.getAnimator().getPlayer().setElapsedTime(startsAt);
				entity.getAnimator().getPlayer().checkNoResetMark();
				this.startsAt = 0;
			}
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		return this.nextAnimation.getState(0.0F);
	}

	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return this.nextAnimation.getPlaySpeed(entitydata);
	}

	public void setNextAnimation(DynamicAnimation animation)
	{
		this.nextAnimation = animation;
	}

	public DynamicAnimation getNextAnimation()
	{
		return this.nextAnimation;
	}

	@Override
	public String toString()
	{
		return "NextAnimation " + this.nextAnimation;
	}
}