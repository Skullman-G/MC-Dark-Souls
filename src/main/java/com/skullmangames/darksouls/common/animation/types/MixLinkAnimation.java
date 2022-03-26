package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.client.animation.MixLayer;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.client.Minecraft;

public class MixLinkAnimation extends DynamicAnimation
{
	private Pose lastPose;
	private final MixLayer mixLayer;
	
	public MixLinkAnimation(MixLayer mixLayer)
	{
		super();
		this.mixLayer = mixLayer;
	}
	
	public void setLastPose(Pose pose)
	{
		this.lastPose = pose;
	}
	
	@Override
	public void onFinish(LivingCap<?> entitydata, boolean isEnd)
	{
		if(isEnd)
		{
			this.mixLayer.animationPlayer.setEmpty();
		}
		this.mixLayer.animationPlayer.resetPlayer();
	}
	
	@Override
	public Pose getPoseByTime(LivingCap<?> entitydata, float time)
	{
		Pose basePose = entitydata.getClientAnimator().getCurrentPose(entitydata.getClientAnimator().baseLayer, Minecraft.getInstance().getFrameTime());
		return Pose.interpolatePose(lastPose, basePose, time / totalTime);
	}
}