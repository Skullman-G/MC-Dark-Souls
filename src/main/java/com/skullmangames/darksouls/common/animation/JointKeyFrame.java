package com.skullmangames.darksouls.common.animation;

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
	
	public JointKeyFrame setTransform(JointTransform transform)
	{
		return new JointKeyFrame(this.timeStamp, transform);
	}
}