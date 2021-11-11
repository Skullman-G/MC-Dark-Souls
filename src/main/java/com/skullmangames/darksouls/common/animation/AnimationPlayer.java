package com.skullmangames.darksouls.common.animation;

import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class AnimationPlayer
{
	private float elapsedTime;
	private float prevElapsedTime;
	private float exceedTime;
	private boolean isEnd;
	private boolean doNotResetNext;

	private DynamicAnimation play;

	public AnimationPlayer()
	{
		resetPlayer();
	}

	public AnimationPlayer(DynamicAnimation animation)
	{
		setPlayAnimation(animation);
	}
	
	public void update(float updateTime)
	{
		this.prevElapsedTime = this.elapsedTime;
		this.elapsedTime += updateTime;

		if (this.elapsedTime >= this.play.getTotalTime())
		{
			if (this.play.isRepeat())
			{
				this.prevElapsedTime = 0;
				this.elapsedTime = (this.elapsedTime % this.play.getTotalTime()) + this.play.getStartingTime();
			}
			else
			{
				this.exceedTime = this.elapsedTime % this.play.getTotalTime();
				this.elapsedTime = this.play.getTotalTime();
				this.isEnd = true;
			}
		}
		else if (this.elapsedTime < 0)
		{
			if (this.play.isRepeat())
			{
				this.prevElapsedTime = this.play.getTotalTime();
				this.elapsedTime = this.play.getTotalTime() + this.elapsedTime;
			}
			else
			{
				this.elapsedTime = 0;
				this.isEnd = true;
			}
		}
	}

	public void synchronize(AnimationPlayer animationPlayer)
	{
		this.play = animationPlayer.play;
		this.elapsedTime = animationPlayer.elapsedTime;
		this.prevElapsedTime = animationPlayer.prevElapsedTime;
		this.exceedTime = animationPlayer.exceedTime;
		this.isEnd = animationPlayer.isEnd;
	}

	public void resetPlayer()
	{
		this.elapsedTime = 0;
		this.prevElapsedTime = 0;
		this.exceedTime = 0;
		this.isEnd = false;
	}

	public void setPlayAnimation(DynamicAnimation animation)
	{
		if (doNotResetNext) doNotResetNext = false;
		else resetPlayer();

		this.play = animation;
	}

	public void setEmpty()
	{
		this.resetPlayer();
		this.play = null;
	}

	public Pose getCurrentPose(LivingData<?> entitydata, float partialTicks)
	{
		return play.getPoseByTime(entitydata, prevElapsedTime + (elapsedTime - prevElapsedTime) * partialTicks);
	}

	public float getElapsedTime()
	{
		return this.elapsedTime;
	}

	public float getPrevElapsedTime()
	{
		return this.prevElapsedTime;
	}

	public void setElapsedTime(float elapsedTime)
	{
		this.elapsedTime = elapsedTime;
		this.prevElapsedTime = elapsedTime;
		this.exceedTime = 0;
		this.isEnd = false;
	}

	public DynamicAnimation getPlay()
	{
		return play;
	}

	public float getExceedTime()
	{
		return exceedTime;
	}

	public void checkNoResetMark()
	{
		this.doNotResetNext = true;
	}

	public boolean isEnd()
	{
		return isEnd;
	}

	public boolean isEmpty()
	{
		return this.play == null ? true : false;
	}
}