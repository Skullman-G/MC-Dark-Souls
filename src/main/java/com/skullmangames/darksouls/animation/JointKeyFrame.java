package com.skullmangames.darksouls.animation;

public class JointKeyFrame
{
	private final float timeStamp;
	private final JointTransform transform;

	public JointKeyFrame(float timeStamp, JointTransform trasnform)
	{
		this.timeStamp = timeStamp;
		this.transform = trasnform;
	}

	public float getTimeStamp()
	{
		return timeStamp;
	}

	public JointTransform getTransform()
	{
		return transform;
	}
}