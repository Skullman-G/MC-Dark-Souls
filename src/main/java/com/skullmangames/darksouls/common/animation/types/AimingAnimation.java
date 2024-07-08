package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.AimingAnimationProperty;
import com.skullmangames.darksouls.common.animation.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.ClientConfig;
import com.skullmangames.darksouls.core.init.Models;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AimingAnimation extends StaticAnimation
{
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;

	public AimingAnimation(ResourceLocation id, float convertTime, boolean repeatPlay, ResourceLocation path1, ResourceLocation path2, ResourceLocation path3,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		super(id, convertTime, repeatPlay, path1, model, properties);
		this.lookUp = new StaticAnimation(null, convertTime, repeatPlay, path2, model, properties);
		this.lookDown = new StaticAnimation(null, convertTime, repeatPlay, path3, model, properties);
	}

	public AimingAnimation(ResourceLocation id, boolean repeatPlay, ResourceLocation path1, ResourceLocation path2, ResourceLocation path3,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, ClientConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, model, properties);
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		if (!this.isReboundAnimation())
		{
			ClientAnimator animator = entityCap.getClientAnimator();
			AnimationLayer layer = animator.getMixLayer(this.getLayerPart());
			AnimationPlayer player = layer.animationPlayer;

			if (player.getElapsedTime() >= this.totalTime - 0.06F)
				layer.pause();
		}
	}
	
	@Override
	public EntityState getState(float time)
	{
		if (this.isReboundAnimation()) return EntityState.POST_CONTACT;
		return super.getState(time);
	}
	
	@Override
	public boolean isReboundAnimation()
	{
		return this.getProperty(AimingAnimationProperty.IS_REBOUND).orElse(false);
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
	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this);
		load(resourceManager, models, this.lookUp);
		load(resourceManager, models, this.lookDown);
	}
	
	public static class Builder extends StaticAnimation.Builder
	{
		protected final ResourceLocation lookUpLocation;
		protected final ResourceLocation lookDownLocation;

		public Builder(ResourceLocation id, float convertTime, boolean repeat, ResourceLocation path1, ResourceLocation path2, ResourceLocation path3,
				Function<Models<?>, Model> model)
		{
			super(id, convertTime, repeat, path1, model);
			this.lookUpLocation = path2;
			this.lookDownLocation = path3;
			this.addProperty(StaticAnimationProperty.LAYER_PART, LayerPart.UP);
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			this.lookUpLocation = new ResourceLocation(json.get("look_up_location").getAsString());
			this.lookDownLocation = new ResourceLocation(json.get("look_down_location").getAsString());
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			json.addProperty("look_up_location", this.lookUpLocation.toString());
			json.addProperty("look_down_location", this.lookDownLocation.toString());
			return json;
		}

		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.AIMING;
		}
		
		@Override
		public AimingAnimation build()
		{
			return new AimingAnimation(this.id, this.convertTime, this.repeat, this.location,
					this.lookUpLocation, this.lookDownLocation, this.model, this.properties.build());
		}
	}
}