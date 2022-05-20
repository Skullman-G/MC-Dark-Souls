package com.skullmangames.darksouls.common.animation;

import com.skullmangames.darksouls.common.animation.Property.MovementAnimationSet;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Animations;

public class AnimationPlayer
{
	private float elapsedTime;
	private float prevElapsedTime;
	private float exceedTime;
	private boolean isEnd;
	private boolean doNotResetNext;
	private boolean reversed;
	private DynamicAnimation play;
	private TransformSheet movementAnimation = new TransformSheet();

	public AnimationPlayer()
	{
		this.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}

	public void update(LivingCap<?> entityCap)
	{
		this.prevElapsedTime = this.elapsedTime;
		this.elapsedTime += IngameConfig.A_TICK * this.getPlay().getPlaySpeed(entityCap);

		if (this.elapsedTime >= this.play.getTotalTime())
		{
			if (this.play.isRepeat())
			{
				this.prevElapsedTime = 0;
				this.elapsedTime %= this.play.getTotalTime();
			} else
			{
				this.exceedTime = this.elapsedTime % this.play.getTotalTime();
				this.elapsedTime = this.play.getTotalTime();
				this.isEnd = true;
			}
		} else if (this.elapsedTime < 0)
		{
			if (this.play.isRepeat())
			{
				this.prevElapsedTime = this.play.getTotalTime();
				this.elapsedTime += this.play.getTotalTime();
			} else
			{
				this.elapsedTime = 0;
				this.isEnd = true;
			}
		}
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
		if (this.doNotResetNext) this.doNotResetNext = false;
		else this.resetPlayer();

		this.play = animation;
	}

	public void setMovementAnimation(DynamicAnimation animation, LivingCap<?> entityCap,
			MovementAnimationSet movementAnimationSetter)
	{
		movementAnimationSetter.set(animation, entityCap, this.movementAnimation);
	}

	public TransformSheet getMovementAnimation()
	{
		return this.movementAnimation;
	}

	public void setEmpty()
	{
		this.resetPlayer();
		this.play = Animations.DUMMY_ANIMATION;
	}

	public Pose getCurrentPose(LivingCap<?> entityCap, float partialTicks)
	{
		return this.play.getPoseByTime(entityCap,
				this.prevElapsedTime + (this.elapsedTime - this.prevElapsedTime) * partialTicks, partialTicks);
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
	
	public float getExceedTime()
	{
		return this.exceedTime;
	}

	public DynamicAnimation getPlay()
	{
		return this.play;
	}

	public void markToDoNotReset()
	{
		this.doNotResetNext = true;
	}

	public boolean isEnd()
	{
		return this.isEnd;
	}

	public boolean isReversed()
	{
		return this.reversed;
	}

	public void setReversed(boolean reversed)
	{
		if (reversed != this.reversed)
		{
			this.setElapsedTime(this.getPlay().getTotalTime() - this.getElapsedTime());
			this.reversed = reversed;
		}
	}

	public boolean isEmpty()
	{
		return this.play == Animations.DUMMY_ANIMATION ? true : false;
	}
}