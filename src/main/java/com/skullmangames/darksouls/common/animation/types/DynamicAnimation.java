package com.skullmangames.darksouls.common.animation.types;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.animation.AnimFrameData;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Keyframe;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.config.ClientConfig;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.util.math.ModMath;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DynamicAnimation
{
	private final AnimFrameData frameData;
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime;

	public DynamicAnimation()
	{
		this(ClientConfig.GENERAL_ANIMATION_CONVERT_TIME, false, new AnimFrameData());
	}

	public DynamicAnimation(float convertTime, boolean isRepeat, AnimFrameData frameData)
	{
		this.frameData = frameData;
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
		this.totalTime = frameData.getTotalTime();
	}
	
	public AnimFrameData getFrameData()
	{
		return this.frameData;
	}
	
	public TransformSheet getJointTransform(String jointName)
	{
		return this.getFrameData().get(jointName);
	}
	
	public boolean shouldSync()
	{
		return false;
	}

	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		return this.getFrameData().getPoseByTimeRaw(entityCap, time, partialTicks);
	}

	public LinkAnimation getLinkAnimation(Pose lastPose, float startAt, LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
		if (!entityCap.isClientSide())
		{
			lastPose = Animations.DUMMY_ANIMATION.getPoseByTime(entityCap, 0.0F, 1.0F);
		}

		float totalTime = this.convertTime;
		startAt = ModMath.clamp(startAt, 0.05F, this.getTotalTime());
		
		AnimFrameData.Builder frameDataBuilder = AnimFrameData.builder(null)
			.withTotalTime(totalTime);

		Map<String, JointTransform> data1 = lastPose.getJointTransformData();
		Map<String, JointTransform> data2 = this.getPoseByTime(entityCap, startAt, 1.0F).getJointTransformData();

		data2.forEach((jointName, transform) ->
		{
			Keyframe[] keyframes = new Keyframe[2];
			keyframes[0] = new Keyframe(0.0F, data1.getOrDefault(jointName, transform));
			keyframes[1] = new Keyframe(totalTime, transform);
			TransformSheet sheet = new TransformSheet(keyframes);
			frameDataBuilder.addSheet(jointName, sheet);
		});
		
		return new LinkAnimation(startAt, this, frameDataBuilder.build());
	}

	public void onStart(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
	}

	public void onUpdate(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
	}

	public void onFinish(LivingCap<?> entityCap, AnimationPlayer animPlayer)
	{
	}

	public void onUpdateLink(LivingCap<?> entityCap, LinkAnimation linkAnimation)
	{
	}

	public boolean isJointEnabled(String joint)
	{
		return this.getFrameData().isJointEnabled(joint);
	}

	public EntityState getState(float time)
	{
		return EntityState.FREE;
	}

	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		return 1.0F;
	}

	public DynamicAnimation getRealAnimation()
	{
		return this;
	}
	
	@Nullable
	public DeathAnimation getDeathAnimation()
	{
		return null;
	}

	public void setTotalTime(float totalTime)
	{
		this.totalTime = totalTime;
	}

	public float getTotalTime()
	{
		return this.totalTime - 0.001F;
	}

	public float getConvertTime()
	{
		return this.convertTime;
	}

	public boolean isRepeat()
	{
		return this.isRepeat;
	}

	@Nullable
	public ResourceLocation getId()
	{
		return null;
	}

	public <V> Optional<V> getProperty(Property<V> propertyType)
	{
		return Optional.empty();
	}
	
	public <V> Optional<V> getPropertyByTime(AttackProperty<V> propertyType, float elapsedTime)
	{
		return Optional.empty();
	}

	public boolean isMainFrameAnimation()
	{
		return false;
	}

	public boolean isReboundAnimation()
	{
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(AnimationPlayer animPlayer, PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap, float partialTicks) {}
}