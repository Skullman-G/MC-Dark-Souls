package com.skullmangames.darksouls.common.animation;

import java.util.List;

import com.skullmangames.darksouls.DarkSouls;

public class TransformSheet
{
	private final JointKeyFrame[] keyframes;

	public TransformSheet(List<JointKeyFrame> keyframeList)
	{
		JointKeyFrame[] keyframes = new JointKeyFrame[keyframeList.size()];
		for (int i = 0; i < keyframeList.size(); i++)
		{
			keyframes[i] = keyframeList.get(i);
		}
		
		this.keyframes = keyframes;
	}

	public TransformSheet(JointKeyFrame[] keyframes)
	{
		this.keyframes = keyframes;
	}
	
	public JointTransform getStartTransform()
	{
		return this.keyframes[0].getTransform();
	}
	
	public void setEndKeyFrame(JointTransform transform)
	{
		this.keyframes[this.keyframes.length - 1] = this.keyframes[this.keyframes.length - 1].setTransform(transform);
	}

	public JointTransform getInterpolatedTransform(float currentTime)
	{
		int prev = 0, next = 1;

		for (int i = 1; i < keyframes.length; i++)
		{
			if(currentTime <= keyframes[i].getTimeStamp())
			{
				break;
			}

			if (keyframes.length > next + 1)
			{
				prev++;
				next++;
			}
			else
			{
				DarkSouls.LOGGER.error("Time exceeded keyframe length");
			}
		}
		
		float progression = (currentTime - keyframes[prev].getTimeStamp()) / (keyframes[next].getTimeStamp() - keyframes[prev].getTimeStamp());
		JointTransform trasnform = JointTransform.interpolate(keyframes[prev].getTransform(), keyframes[next].getTransform(), progression);
		
		return trasnform;
	}
}
