package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.AnimFrameData;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Keyframe;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.ClientConfig;

public class LinkAnimation extends DynamicAnimation
{
	private final DynamicAnimation nextAnimation;
	private final float startsAt;
	
	public LinkAnimation(float startsAt, DynamicAnimation nextAnimation, AnimFrameData frameData)
	{
		super(ClientConfig.GENERAL_ANIMATION_CONVERT_TIME, false, frameData);
		this.startsAt = startsAt;
		this.nextAnimation = nextAnimation;
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		this.nextAnimation.onUpdateLink(entityCap, this);
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		float startsAt = this.getStartsAt(entityCap);
		
		if (!animPlayer.isEnd())
		{
			this.nextAnimation.onFinish(entityCap, animPlayer);
		}
		else if (startsAt > 0.0F)
		{
			animPlayer.setElapsedTime(startsAt);
			animPlayer.markToDoNotReset();
		}
	}

	@Override
	public EntityState getState(float time)
	{
		return this.nextAnimation.getState(0.0F);
	}

	@Override
	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		Pose nextStartingPose = this.nextAnimation.getPoseByTime(entityCap, this.getStartsAt(entityCap), 1.0F);

		for (String jointName : nextStartingPose.getJointTransformData().keySet())
		{
			if (!this.isJointEnabled(jointName)) continue;
			
			Keyframe[] keyframes = this.getJointTransform(jointName).getKeyframes();
			JointTransform endTransform = keyframes[keyframes.length - 1].transform();
			JointTransform newEndTransform = nextStartingPose.getJointTransformData().get(jointName);
			
			newEndTransform.translation().set(endTransform.translation().x(), endTransform.translation().y(), endTransform.translation().z());
			endTransform.copyFrom(newEndTransform);
		}

		return super.getPoseByTime(entityCap, time, partialTicks);
	}
	
	public float getStartsAt(LivingCap<?> entityCap)
	{
		return this.nextAnimation.shouldSync() ? entityCap.getAnimator().getMainPlayer().getElapsedTime()
				: this.startsAt;
	}

	@Override
	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		return this.nextAnimation.getPlaySpeed(entityCap);
	}

	public DynamicAnimation getNextAnimation()
	{
		return this.nextAnimation;
	}

	@Override
	public boolean isJointEnabled(String joint)
	{
		return this.nextAnimation.isJointEnabled(joint);
	}

	@Override
	public boolean isMainFrameAnimation()
	{
		return this.nextAnimation.isMainFrameAnimation();
	}

	@Override
	public boolean isReboundAnimation()
	{
		return this.nextAnimation.isReboundAnimation();
	}

	@Override
	public DynamicAnimation getRealAnimation()
	{
		return this.nextAnimation;
	}

	@Override
	public String toString()
	{
		return "LinkAnimation " + this.nextAnimation;
	}
}