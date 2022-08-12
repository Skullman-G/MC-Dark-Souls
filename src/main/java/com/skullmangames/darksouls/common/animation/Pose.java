package com.skullmangames.darksouls.common.animation;

import java.util.HashMap;
import java.util.Map;

public class Pose
{
	private final Map<String, JointTransform> jointTransformData = new HashMap<>();

	public void putJointData(String name, JointTransform transform)
	{
		this.jointTransformData.put(name, transform);
	}

	public Map<String, JointTransform> getJointTransformData()
	{
		return this.jointTransformData;
	}

	public JointTransform getTransformByName(String jointName)
	{
		JointTransform jt = this.jointTransformData.get(jointName);
		if (jt == null)
		{
			return JointTransform.empty();
		}

		return jt;
	}

	public static Pose interpolatePose(Pose pose1, Pose pose2, float progression)
	{
		Pose pose = new Pose();

		for (String jointName : pose1.jointTransformData.keySet())
		{
			if (pose2.jointTransformData.containsKey(jointName))
			{
				pose.putJointData(jointName, JointTransform.interpolate(pose1.jointTransformData.get(jointName),
						pose2.jointTransformData.get(jointName), progression));
			}
		}

		return pose;
	}

	public String toString()
	{
		String str = "[";

		for (Map.Entry<String, JointTransform> entry : this.jointTransformData.entrySet())
		{
			str += String.format("%s{ %s, %s }, ", entry.getKey(), entry.getValue().translation().toString(),
					entry.getValue().rotation().toString());
		}

		str += "]";

		return str;
	}
}