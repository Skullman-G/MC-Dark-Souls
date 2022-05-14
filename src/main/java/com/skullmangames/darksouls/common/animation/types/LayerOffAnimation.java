package com.skullmangames.darksouls.common.animation.types;

import java.util.Optional;

import com.skullmangames.darksouls.client.animation.AnimationLayer.Priority;
import com.skullmangames.darksouls.client.animation.JointMask.BindModifier;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.client.Minecraft;

public class LayerOffAnimation extends DynamicAnimation
{
	private DynamicAnimation lastAnimation;
	private Pose lastPose;
	private Priority layerPriority;

	public LayerOffAnimation(Priority layerPriority)
	{
		this.layerPriority = layerPriority;
	}

	public void setLastPose(Pose pose)
	{
		this.lastPose = pose;
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		if (entityCap.isClientSide())
			entityCap.getClientAnimator().baseLayer.disableLayer(this.layerPriority);
	}

	@Override
	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		Pose lowerLayerPose = entityCap.getClientAnimator().getComposedLayerPoseBelow(this.layerPriority,
				Minecraft.getInstance().getFrameTime());
		return Pose.interpolatePose(this.lastPose, lowerLayerPose, time / this.totalTime);
	}

	@Override
	public boolean isJointEnabled(LivingCap<?> entityCap, String joint)
	{
		return this.lastPose.getJointTransformData().containsKey(joint);
	}

	@Override
	public <V> Optional<V> getProperty(Property<V> propertyType)
	{
		return this.lastAnimation.getProperty(propertyType);
	}

	public void setLastAnimation(DynamicAnimation animation)
	{
		this.lastAnimation = animation;
	}

	@Override
	public BindModifier getBindModifier(LivingCap<?> entityCap, String joint)
	{
		return this.lastAnimation.getBindModifier(entityCap, joint);
	}

	@Override
	public DynamicAnimation getRealAnimation()
	{
		return Animations.DUMMY_ANIMATION;
	}
}
