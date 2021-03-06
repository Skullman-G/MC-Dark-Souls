package com.skullmangames.darksouls.common.animation.types;

import java.util.Optional;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.client.Minecraft;

public class LayerOffAnimation extends DynamicAnimation
{
	private DynamicAnimation lastAnimation;
	private Pose lastPose;
	private LayerPart layerPart;

	public LayerOffAnimation(LayerPart layerPart)
	{
		this.layerPart = layerPart;
	}

	public void setLastPose(Pose pose)
	{
		this.lastPose = pose;
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		if (entityCap.isClientSide())
			entityCap.getClientAnimator().baseLayer.disableLayer(this.layerPart);
	}

	@Override
	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		Pose lowerLayerPose = entityCap.getClientAnimator().getComposedLayerPoseFromOthers(this.layerPart,
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
	public DynamicAnimation getRealAnimation()
	{
		return Animations.DUMMY_ANIMATION;
	}
}
