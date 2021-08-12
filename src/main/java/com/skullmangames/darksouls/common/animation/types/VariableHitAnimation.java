package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;

import com.skullmangames.darksouls.common.animation.JointKeyFrame;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class VariableHitAnimation extends HitAnimation
{
	public VariableHitAnimation(int id, float convertTime, String path)
	{
		super(id, convertTime, path);
	}
	
	@Override
	public void getLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest)
	{
		dest.getTransfroms().clear();
		dest.setTotalTime(timeModifier + convertTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = super.getPoseByTime(entitydata, 0.0F).getJointTransformData();
		Map<String, JointTransform> data3 = super.getPoseByTime(entitydata, this.totalTime - 0.00001F).getJointTransformData();
		
		for(String jointName : data1.keySet())
		{
			JointKeyFrame[] keyframes = new JointKeyFrame[4];
			keyframes[0] = new JointKeyFrame(0, data1.get(jointName));
			keyframes[1] = new JointKeyFrame(convertTime, data2.get(jointName));
			keyframes[2] = new JointKeyFrame(convertTime + 0.033F, data3.get(jointName));
			keyframes[3] = new JointKeyFrame(timeModifier + convertTime, data3.get(jointName));
			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		return super.getPoseByTime(entitydata, this.getTotalTime() - 0.000001F);
	}
}