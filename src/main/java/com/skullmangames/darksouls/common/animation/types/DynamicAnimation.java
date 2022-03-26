package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.JointKeyFrame;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;

public class DynamicAnimation
{
	protected final Map<String, TransformSheet> jointTransforms;
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime;
	protected float startingTime = 0.0F;

	public DynamicAnimation()
	{
		jointTransforms = new HashMap<String, TransformSheet>();
		this.totalTime = 0;
		this.isRepeat = false;
		this.convertTime = IngameConfig.GENERAL_ANIMATION_CONVERT_TIME;
	}

	public DynamicAnimation(float convertTime, boolean isRepeat)
	{
		this(0, convertTime, isRepeat);
	}

	public DynamicAnimation(float totalTime, float convertTime, boolean isRepeat)
	{
		jointTransforms = new HashMap<String, TransformSheet>();
		this.totalTime = totalTime;
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}

	public void addSheet(String jointName, TransformSheet sheet)
	{
		jointTransforms.put(jointName, sheet);
	}
	
	public Pose getPoseByTime(LivingCap<?> entitydata, float time)
	{
		Pose pose = new Pose();

		for (String jointName : jointTransforms.keySet())
		{
			pose.putJointData(jointName, jointTransforms.get(jointName).getInterpolatedTransform(time));
		}

		return pose;
	}

	public void getLinkAnimation(Pose pose1, float timeModifier, LivingCap<?> entitydata, LinkAnimation dest)
	{
		float totalTime = timeModifier >= 0 ? timeModifier + convertTime : convertTime;
		boolean isNeg = timeModifier < 0;
		float nextStart = isNeg ? -timeModifier : 0;
		
		if(isNeg) dest.startsAt = nextStart;
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = getPoseByTime(entitydata, nextStart).getJointTransformData();

		for (String jointName : data1.keySet())
		{
			JointKeyFrame[] keyframes = new JointKeyFrame[2];
			keyframes[0] = new JointKeyFrame(0, data1.get(jointName));
			keyframes[1] = new JointKeyFrame(totalTime, data2.get(jointName));

			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}

	public void putOnPlayer(AnimationPlayer player)
	{
		player.setPlayAnimation(this);
	}
	
	public void onActivate(LivingCap<?> entitydata) {}
	public void onUpdate(LivingCap<?> entitydata) {}
	public void onFinish(LivingCap<?> entitydata, boolean isEnd) {}
	
	public LivingCap.EntityState getState(float time)
	{
		return LivingCap.EntityState.FREE;
	}

	public Map<String, TransformSheet> getTransfroms()
	{
		return jointTransforms;
	}

	public float getPlaySpeed(LivingCap<?> entitydata)
	{
		return 1.0F;
	}

	public void setTotalTime(float value)
	{
		this.totalTime = value;
	}

	public float getTotalTime()
	{
		return this.totalTime - 0.001F;
	}

	public float getConvertTime()
	{
		return this.convertTime;
	}

	public boolean isRepeat()
	{
		return this.isRepeat;
	}
	
	public void setStartingTime(float value)
	{
		this.startingTime = value;
	}
	
	public float getStartingTime()
	{
		return this.startingTime;
	}
}