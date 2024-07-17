package com.skullmangames.darksouls.common.animation;

import java.util.List;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.core.util.math.ModMath;
import com.skullmangames.darksouls.core.util.math.vector.ModQuaternion;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;
import com.skullmangames.darksouls.core.util.math.vector.Vector3fHelper;

import net.minecraft.util.Mth;

public class TransformSheet
{
	private Keyframe[] keyframes;

	public TransformSheet(List<Keyframe> keyframeList)
	{
		this(keyframeList.toArray(new Keyframe[0]));
	}

	public TransformSheet(Keyframe[] keyframes)
	{
		this.keyframes = keyframes;
	}

	public TransformSheet()
	{
		this(new Keyframe[0]);
	}

	public JointTransform getStartTransform()
	{
		return this.keyframes[0].transform();
	}

	public Keyframe[] getKeyframes()
	{
		return this.keyframes;
	}

	public TransformSheet copyAll()
	{
		return this.copy(0, this.keyframes.length);
	}

	public TransformSheet copy(int start, int end)
	{
		int len = end - start;
		Keyframe[] newKeyframes = new Keyframe[len];

		for (int i = 0; i < len; i++)
		{
			Keyframe kf = this.keyframes[i + start];
			newKeyframes[i] = new Keyframe(kf);
		}

		return new TransformSheet(newKeyframes);
	}

	public TransformSheet readFrom(TransformSheet opponent)
	{
		if (opponent == null) return this;
		if (opponent.keyframes.length != this.keyframes.length)
		{
			this.keyframes = new Keyframe[opponent.keyframes.length];

			for (int i = 0; i < this.keyframes.length; i++)
			{
				this.keyframes[i] = new Keyframe(0.0F, JointTransform.empty());
			}
		}

		for (int i = 0; i < this.keyframes.length; i++)
		{
			this.keyframes[i].copyFrom(opponent.keyframes[i]);
		}

		return this;
	}

	public Vector3f getInterpolatedTranslation(float currentTime)
	{
		InterpolationInfo interpolInfo = this.getInterpolationInfo(currentTime);
		Vector3f vec3f = ModMath.lerpVector(this.keyframes[interpolInfo.prev].transform().translation(),
				this.keyframes[interpolInfo.next].transform().translation(), interpolInfo.zero2One);
		return vec3f;
	}

	public ModQuaternion getInterpolatedRotation(float currentTime)
	{
		InterpolationInfo interpolInfo = this.getInterpolationInfo(currentTime);
		return ModQuaternion.lerp(this.keyframes[interpolInfo.prev].transform().rotation(),
				this.keyframes[interpolInfo.next].transform().rotation(), interpolInfo.zero2One);
	}

	public JointTransform getInterpolatedTransform(float currentTime)
	{
		InterpolationInfo interpolInfo = this.getInterpolationInfo(currentTime);
		JointTransform trasnform = JointTransform.interpolate(this.keyframes[interpolInfo.prev].transform(),
				this.keyframes[interpolInfo.next].transform(), interpolInfo.zero2One);
		return trasnform;
	}

	public void correctAnimationByNewPosition(Vector3f startpos, Vector3f startToEnd, Vector3f modifiedStart,
			Vector3f modifiedStartToEnd)
	{
		Keyframe[] keyframes = this.getKeyframes();
		Keyframe startKeyframe = keyframes[0];
		Keyframe endKeyframe = keyframes[keyframes.length - 1];
		float pitchDeg = (float) Math.toDegrees(
				Mth.atan2(modifiedStartToEnd.y() - startToEnd.y(), Vector3fHelper.length(modifiedStartToEnd)));
		float yawDeg = (float) Math.toDegrees(ModMath.getAngleBetween(
				Vector3fHelper.normalize(Vector3fHelper.mul(modifiedStartToEnd, 1.0F, 0.0F, 1.0F)),
				Vector3fHelper.normalize(Vector3fHelper.mul(startToEnd, 1.0F, 0.0F, 1.0F))));

		for (Keyframe kf : keyframes)
		{
			float lerp = (kf.time() - startKeyframe.time()) / (endKeyframe.time() - startKeyframe.time());
			Vector3f line = ModMath.lerpVector(new Vector3f(0F, 0F, 0F), startToEnd, lerp);
			Vector3f modifiedLine = ModMath.lerpVector(new Vector3f(0F, 0F, 0F), modifiedStartToEnd, lerp);
			Vector3f keyTransform = kf.transform().translation();
			Vector3f startToKeyTransform = Vector3fHelper.mul(Vector3fHelper.sub(keyTransform, startpos), -1.0F, 1.0F,
					-1.0F);
			Vector3f animOnLine = Vector3fHelper.sub(startToKeyTransform, line);
			ModMatrix4f rotator = ModMatrix4f.createRotatorDeg(pitchDeg, new Vector3f(1.0F, 0.0F, 0.0F))
					.mulFront(ModMatrix4f.createRotatorDeg(yawDeg, new Vector3f(0.0F, 1.0F, 0.0F)));
			Vector3f toNewKeyTransform = Vector3fHelper.add(modifiedLine,
					ModMatrix4f.transform3v(rotator, animOnLine));
			keyTransform = Vector3fHelper.add(modifiedStart, toNewKeyTransform);
		}
	}

	private InterpolationInfo getInterpolationInfo(float currentTime)
	{
		int prev = 0, next = 1;

		for (int i = 1; i < this.keyframes.length; i++)
		{
			if (currentTime <= this.keyframes[i].time())
			{
				break;
			}

			if (this.keyframes.length > next + 1)
			{
				prev++;
				next++;
			}
		}

		float progression = bezierCurve((currentTime - this.keyframes[prev].time())
				/ (this.keyframes[next].time() - this.keyframes[prev].time()));

		return new InterpolationInfo(prev, next, progression);
	}

	private static float bezierCurve(float t)
	{
		float start = 0.0F;
		float end = 1.0F;
		float p0 = -t * t * t + 3.0F * t * t - 3.0F * t + 1.0F;
		float p1 = 3.0F * t * t * t - 6.0F * t * t + 3.0F * t;
		float p2 = -3.0F * t * t * t + 3.0F * t * t;
		float p3 = t * t * t;
		return (start * p0) + (start * p1) + (end * p2) + (end * p3);
	}

	private static class InterpolationInfo
	{
		private int prev;
		private int next;
		private float zero2One;

		private InterpolationInfo(int prev, int next, float zero2One)
		{
			this.prev = prev;
			this.next = next;
			this.zero2One = zero2One;
		}
	}
}
