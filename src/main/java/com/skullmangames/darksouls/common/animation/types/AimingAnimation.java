package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Models;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public class AimingAnimation extends StaticAnimation
{
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;

	public AimingAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, Function<Models<?>, Model> model)
	{
		super(convertTime, repeatPlay, path1, model);
		this.lookUp = new StaticAnimation(convertTime, repeatPlay, path2, model, true);
		this.lookDown = new StaticAnimation(convertTime, repeatPlay, path3, model, true);
		this.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP);
	}

	public AimingAnimation(boolean repeatPlay, String path1, String path2, String path3, Function<Models<?>, Model> model)
	{
		this(IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, model);
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		if (!this.isReboundAnimation())
		{
			ClientAnimator animator = entityCap.getClientAnimator();
			AnimationLayer layer = animator.getCompositeLayer(this.getLayerPart());
			AnimationPlayer player = layer.animationPlayer;

			if (player.getElapsedTime() >= this.totalTime - 0.06F)
				layer.pause();
		}
	}

	@Override
	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		if (!entityCap.isFirstPerson())
		{
			float pitch = entityCap.getOriginalEntity().getViewXRot(Minecraft.getInstance().getFrameTime());
			StaticAnimation interpolateAnimation;
			interpolateAnimation = (pitch > 0) ? this.lookDown : this.lookUp;
			Pose pose1 = super.getPoseByTime(entityCap, time, partialTicks);
			Pose pose2 = interpolateAnimation.getPoseByTime(entityCap, time, partialTicks);
			this.modifyPose(pose2, entityCap, time);
			Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, (Math.abs(pitch) / 90.0F));
			return interpolatedPose;
		}

		return super.getPoseByTime(entityCap, time, partialTicks);
	}

	@Override
	protected void modifyPose(Pose pose, LivingCap<?> entityCap, float time)
	{
		if (!entityCap.isFirstPerson())
		{
			JointTransform head = pose.getTransformByName("Head");
			float f = 90.0F;
			float ratio = (f - Math.abs(entityCap.getOriginalEntity().getXRot())) / f;
			float yawOffset = entityCap.getOriginalEntity().getVehicle() != null
					? entityCap.getOriginalEntity().getYRot()
					: entityCap.getOriginalEntity().yBodyRot;
			head.rotation().mulLeft(Vector3f.YP.rotationDegrees((yawOffset - entityCap.getOriginalEntity().getYRot()) * ratio));
		}
	}

	@Override
	public <V> StaticAnimation addProperty(Property<V> propertyType, V value)
	{
		super.addProperty(propertyType, value);
		this.lookDown.addProperty(propertyType, value);
		this.lookUp.addProperty(propertyType, value);
		return this;
	}

	@Override
	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this);
		load(resourceManager, models, this.lookUp);
		load(resourceManager, models, this.lookDown);
	}
}