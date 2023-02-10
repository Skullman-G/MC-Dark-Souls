package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;

import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Keyframe;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

public class LinkAnimation extends DynamicAnimation
{
	protected DynamicAnimation nextAnimation;
	protected float startsAt;

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		this.nextAnimation.onUpdateLink(entityCap, this);
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		if (!isEnd)
		{
			this.nextAnimation.onFinish(entityCap, isEnd);
		}
		else if (this.startsAt > 0.0F)
		{
			entityCap.getAnimator().getPlayerFor(this).setElapsedTime(this.startsAt);
			entityCap.getAnimator().getPlayerFor(this).markToDoNotReset();
			this.startsAt = 0.0F;
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
		Pose nextStartingPose = this.nextAnimation.getPoseByTime(entityCap, this.startsAt, 1.0F);

		for (Map.Entry<String, JointTransform> entry : nextStartingPose.getJointTransformData().entrySet())
		{
			if (!this.jointTransforms.containsKey(entry.getKey())) continue;
			
			Keyframe[] keyframes = this.jointTransforms.get(entry.getKey()).getKeyframes();
			JointTransform endTransform = keyframes[keyframes.length - 1].transform();
			JointTransform newEndTransform = nextStartingPose.getJointTransformData().get(entry.getKey());
			
			newEndTransform.translation().set(endTransform.translation().x(), endTransform.translation().y(), endTransform.translation().z());
			endTransform.copyFrom(newEndTransform);
		}

		return super.getPoseByTime(entityCap, time, partialTicks);
	}

	@Override
	protected void modifyPose(Pose pose, LivingCap<?> entityCap, float time)
	{
		this.nextAnimation.modifyPose(pose, entityCap, time);
	}

	@Override
	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		return this.nextAnimation.getPlaySpeed(entityCap);
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
	public boolean isJointEnabled(LivingCap<?> entityCap, String joint)
	{
		return this.nextAnimation.isJointEnabled(entityCap, joint);
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