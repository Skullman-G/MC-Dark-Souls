package com.skullmangames.darksouls.animation.types;

import com.skullmangames.darksouls.animation.Pose;
import com.skullmangames.darksouls.common.entities.LivingData;

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