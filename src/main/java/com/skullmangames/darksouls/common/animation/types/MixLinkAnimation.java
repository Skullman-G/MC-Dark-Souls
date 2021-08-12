package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

import net.minecraft.client.Minecraft;

public class MixLinkAnimation extends DynamicAnimation
{
	private Pose lastPose;
	
	public void setLastPose(Pose pose)
	{
		this.lastPose = pose;
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		if(isEnd)
			entitydata.getClientAnimator().mixLayerActivated = false;
		entitydata.getClientAnimator().mixLayer.animationPlayer.resetPlayer();
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		Pose basePose = entitydata.getClientAnimator().getCurrentPose(entitydata.getClientAnimator().baseLayer, Minecraft.getInstance().getFrameTime());
		return Pose.interpolatePose(lastPose, basePose, time / totalTime);
	}
}