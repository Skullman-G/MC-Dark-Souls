package com.skullmangames.darksouls.common.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.client.animation.JointMask.BindModifier;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Keyframe;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DynamicAnimation
{
	protected Map<String, TransformSheet> jointTransforms;
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime = 0.0F;

	public DynamicAnimation()
	{
		this(IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, false);
	}

	public DynamicAnimation(float convertTime, boolean isRepeat)
	{
		this.jointTransforms = new HashMap<String, TransformSheet>();
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}

	public void addSheet(String jointName, TransformSheet sheet)
	{
		this.jointTransforms.put(jointName, sheet);
	}

	public final Pose getPoseByTimeRaw(LivingCap<?> entityCap, float time, float partialTicks)
	{
		Pose pose = new Pose();
		for (String jointName : this.jointTransforms.keySet())
		{
			if (!entityCap.isClientSide() || this.isJointEnabled(entityCap, jointName))
			{
				pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
			}
		}
		return pose;
	}

	public Pose getPoseByTime(LivingCap<?> entityCap, float time, float partialTicks)
	{
		Pose pose = new Pose();

		for (String jointName : this.jointTransforms.keySet())
		{
			if (!entityCap.isClientSide() || this.isJointEnabled(entityCap, jointName))
			{
				pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
			}
		}

		this.modifyPose(pose, entityCap, time);

		return pose;
	}
	
	protected void modifyPose(Pose pose, LivingCap<?> entityCap, float time) {}

	public void setLinkAnimation(Pose pose1, float convertTimeModifier, LivingCap<?> entityCap,
			LinkAnimation dest)
	{
		if (!entityCap.isClientSide())
		{
			pose1 = Animations.DUMMY_ANIMATION.getPoseByTime(entityCap, 0.0F, 1.0F);
		}

		float totalTime = convertTimeModifier >= 0.0F ? convertTimeModifier + this.convertTime : this.convertTime;
		boolean isNeg = convertTimeModifier < 0.0F;
		float nextStart = isNeg ? -convertTimeModifier : 0.0F;

		if (isNeg) dest.startsAt = nextStart;

		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);

		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = this.getPoseByTime(entityCap, nextStart, 1.0F).getJointTransformData();

		for (String jointName : data1.keySet())
		{
			if (data1.containsKey(jointName) && data2.containsKey(jointName))
			{
				Keyframe[] keyframes = new Keyframe[2];
				keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
				keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.addSheet(jointName, sheet);
			}
		}
	}

	public void putOnPlayer(AnimationPlayer player)
	{
		player.setPlayAnimation(this);
	}

	public void onStart(LivingCap<?> entityCap)
	{
	}

	public void onUpdate(LivingCap<?> entityCap)
	{
	}

	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
	}

	public void linkTick(LivingCap<?> entityCap, LinkAnimation linkAnimation) {}

	public boolean isJointEnabled(LivingCap<?> entityCap, String joint)
	{
		return this.jointTransforms.containsKey(joint);
	}

	public BindModifier getBindModifier(LivingCap<?> entityCap, String joint)
	{
		return null;
	}

	public EntityState getState(float time)
	{
		return EntityState.FREE;
	}

	public Map<String, TransformSheet> getTransfroms()
	{
		return this.jointTransforms;
	}

	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		return 1.0F;
	}

	public DynamicAnimation getRealAnimation()
	{
		return this;
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

	public int getId()
	{
		return -1;
	}

	public <V> Optional<V> getProperty(Property<V> propertyType)
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

	public boolean isMetaAnimation()
	{
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap,
			float playTime, float partialTicks) {}
}