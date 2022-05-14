package com.skullmangames.darksouls.client.animation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.client.animation.AnimationLayer.Priority;
import com.skullmangames.darksouls.client.animation.JointMask.BindModifier;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.ServerAnimator;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAnimator extends Animator
{
	public static Animator getAnimator(LivingCap<?> entityCap)
	{
		return entityCap.isClientSide() ? new ClientAnimator(entityCap) : ServerAnimator.getAnimator(entityCap);
	}

	private final Map<LivingMotion, StaticAnimation> compositeLivingAnimations;
	private final Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
	private final Map<LivingMotion, StaticAnimation> defaultCompositeLivingAnimations;
	public final AnimationLayer.BaseLayer baseLayer;
	private LivingMotion currentMotion;
	private LivingMotion currentMixMotion;

	public ClientAnimator(LivingCap<?> entityCap)
	{
		this.entityCap = entityCap;
		this.currentMotion = LivingMotion.IDLE;
		this.currentMixMotion = LivingMotion.IDLE;
		this.compositeLivingAnimations = new HashMap<>();
		this.defaultLivingAnimations = new HashMap<>();
		this.defaultCompositeLivingAnimations = new HashMap<>();
		this.baseLayer = new AnimationLayer.BaseLayer(null);
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float convertTimeModifier)
	{
		AnimationLayer layer = nextAnimation.getLayerType() == AnimationLayer.LayerType.BASE_LAYER ? this.baseLayer
				: this.baseLayer.compositeLayers.get(nextAnimation.getPriority());
		layer.paused = false;
		layer.playAnimation(nextAnimation, this.entityCap, convertTimeModifier);
	}

	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation)
	{
		this.baseLayer.paused = false;
		this.baseLayer.playAnimation(nextAnimation, this.entityCap);
	}

	@Override
	public void reserveAnimation(StaticAnimation nextAnimation)
	{
		this.baseLayer.paused = false;
		this.baseLayer.nextAnimation = nextAnimation;
	}

	@Override
	public void addLivingAnimation(LivingMotion livingMotion, StaticAnimation animation)
	{
		AnimationLayer.LayerType layerType = animation.getLayerType();

		switch (layerType)
		{
		case BASE_LAYER:
			this.addBaseLivingAnimation(livingMotion, animation);
			break;
		case COMPOSITE_LAYER:
			this.addCompositeLivingAnimation(livingMotion, animation);
			break;
		}
	}

	protected void addBaseLivingAnimation(LivingMotion livingMotion, StaticAnimation animation)
	{
		this.livingAnimations.put(livingMotion, animation);

		if (livingMotion == this.currentMotion)
		{
			if (!this.entityCap.isInaction())
			{
				this.playAnimation(animation, 0.0F);
			}
		}
	}

	protected void addCompositeLivingAnimation(LivingMotion livingMotion, StaticAnimation animation)
	{
		if (animation != null)
		{
			this.compositeLivingAnimations.put(livingMotion, animation);

			if (this.currentMixMotion == livingMotion)
			{
				if (!this.entityCap.isInaction())
				{
					this.playAnimation(animation, 0.0F);
				}
			}
		}
	}

	public void setCurrentMotionsToDefault()
	{
		this.livingAnimations.forEach(this.defaultLivingAnimations::put);
		this.compositeLivingAnimations.forEach(this.defaultCompositeLivingAnimations::put);
	}

	@Override
	public void resetMotions()
	{
		super.resetMotions();
		this.compositeLivingAnimations.clear();
		this.defaultLivingAnimations.forEach(this.livingAnimations::put);
		this.defaultCompositeLivingAnimations.forEach(this.compositeLivingAnimations::put);
	}

	public StaticAnimation getLivingMotion(LivingMotion motion)
	{
		return this.livingAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION);
	}

	public StaticAnimation getCompositeLivingMotion(LivingMotion motion)
	{
		return this.compositeLivingAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION);
	}

	public void setPoseToModel(float partialTicks)
	{
		Joint rootJoint = this.entityCap.getEntityModel(ClientModels.CLIENT).getArmature().getJointHierarcy();
		this.applyPoseToJoint(rootJoint, new PublicMatrix4f(), this.getPose(partialTicks), partialTicks);
	}

	public void applyPoseToJoint(Joint joint, PublicMatrix4f parentTransform, Pose pose, float partialTicks)
	{
		PublicMatrix4f result = pose.getTransformByName(joint.getName()).getAnimationBindedMatrix(joint,
				parentTransform);
		joint.setAnimatedTransform(result);

		for (Joint joints : joint.getSubJoints())
		{
			this.applyPoseToJoint(joints, result, pose, partialTicks);
		}
	}

	@Override
	public void init()
	{
		this.entityCap.initAnimator(this);
		StaticAnimation idleMotion = this.livingAnimations.get(this.currentMotion);
		this.baseLayer.playAnimation(idleMotion, this.entityCap);
	}

	@Override
	public void updatePose()
	{
		this.prevPose = this.currentPose;
		this.currentPose = this.getComposedLayerPose(1.0F);
	}

	@Override
	public void update()
	{
		this.baseLayer.update(this.entityCap);
		this.updatePose();
		
		if (this.baseLayer.animationPlayer.isEnd() && this.baseLayer.nextAnimation == null
				&& this.currentMotion != LivingMotion.DEATH)
		{
			this.entityCap.updateMotion();
			this.baseLayer.playAnimation(this.getLivingMotion(this.entityCap.currentMotion), this.entityCap, 0.0F);
		}

		if (!this.compareCompositeMotion(this.entityCap.currentMixMotion))
		{
			if (this.compositeLivingAnimations.containsKey(this.entityCap.currentMixMotion))
			{
				this.playAnimation(this.getCompositeLivingMotion(this.entityCap.currentMixMotion), 0.0F);
			} else
			{
				this.getCompositeLayer(AnimationLayer.Priority.MIDDLE).off(this.entityCap);
			}
		}

		if (!this.compareMotion(this.entityCap.currentMotion))
		{
			if (this.livingAnimations.containsKey(this.entityCap.currentMotion))
			{
				this.baseLayer.playAnimation(this.getLivingMotion(this.entityCap.currentMotion), this.entityCap, 0.0F);
			}
		}

		this.currentMotion = this.entityCap.currentMotion;
		this.currentMixMotion = this.entityCap.currentMixMotion;
	}

	@Override
	public void playDeathAnimation()
	{
		this.playAnimation(this.livingAnimations.get(LivingMotion.DEATH), 0);
		this.currentMotion = LivingMotion.DEATH;
	}

	public AnimationLayer getCompositeLayer(AnimationLayer.Priority priority)
	{
		return this.baseLayer.compositeLayers.get(priority);
	}

	public Pose getComposedLayerPose(float partialTicks)
	{
		Pose composedPose = new Pose();
		Pose currentBasePose = this.baseLayer.animationPlayer.getCurrentPose(this.entityCap, partialTicks);
		;
		Map<AnimationLayer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		layerPoses.put(AnimationLayer.Priority.LOWEST,
				Pair.of(this.baseLayer.animationPlayer.getPlay(), currentBasePose));

		for (Map.Entry<String, JointTransform> transformEntry : currentBasePose.getJointTransformData().entrySet())
		{
			composedPose.putJointData(transformEntry.getKey(), transformEntry.getValue());
		}

		for (AnimationLayer.Priority priority : this.baseLayer.baserLayerPriority.uppers())
		{
			AnimationLayer compositeLayer = this.baseLayer.compositeLayers.get(priority);

			if (!compositeLayer.isDisabled())
			{
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entityCap,
						compositeLayer.paused ? 1.0F : partialTicks);
				layerPoses.put(priority, Pair.of(compositeLayer.animationPlayer.getPlay(), layerPose));

				for (Map.Entry<String, JointTransform> transformEntry : layerPose.getJointTransformData().entrySet())
				{
					composedPose.getJointTransformData().put(transformEntry.getKey(), transformEntry.getValue());
				}
			}
		}

		Joint rootJoint = this.entityCap.getEntityModel(ClientModels.CLIENT).getArmature().getJointHierarcy();
		this.applyBindModifier(composedPose, rootJoint, layerPoses);

		return composedPose;
	}

	public Pose getComposedLayerPoseBelow(AnimationLayer.Priority priorityLimit, float partialTicks)
	{
		Pose composedPose = this.baseLayer.animationPlayer.getCurrentPose(this.entityCap, partialTicks);
		Map<AnimationLayer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();

		for (AnimationLayer.Priority priority : priorityLimit.lowers())
		{
			AnimationLayer compositeLayer = this.baseLayer.compositeLayers.get(priority);

			if (!compositeLayer.isDisabled())
			{
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entityCap,
						compositeLayer.paused ? 1.0F : partialTicks);
				layerPoses.put(priority, Pair.of(compositeLayer.animationPlayer.getPlay(), layerPose));

				for (Map.Entry<String, JointTransform> transformEntry : layerPose.getJointTransformData().entrySet())
				{
					composedPose.getJointTransformData().put(transformEntry.getKey(), transformEntry.getValue());
				}
			}
		}

		Joint rootJoint = this.entityCap.getEntityModel(ClientModels.CLIENT).getArmature().getJointHierarcy();
		this.applyBindModifier(composedPose, rootJoint, layerPoses);

		return composedPose;
	}

	public void applyBindModifier(Pose result, Joint joint,
			Map<AnimationLayer.Priority, Pair<DynamicAnimation, Pose>> poses)
	{
		List<Priority> list = Lists.newArrayList(poses.keySet());
		Collections.reverse(list);

		for (AnimationLayer.Priority priority : list)
		{
			DynamicAnimation nowPlaying = poses.get(priority).getFirst();

			if (nowPlaying.isJointEnabled(this.entityCap, joint.getName()))
			{
				BindModifier bindModifier = nowPlaying.getBindModifier(this.entityCap, joint.getName());

				if (bindModifier != null)
				{
					bindModifier.modify(this, result, priority, joint, poses);
				}

				break;
			}
		}

		for (Joint subJoints : joint.getSubJoints())
		{
			this.applyBindModifier(result, subJoints, poses);
		}
	}

	public boolean compareMotion(LivingMotion motion)
	{
		boolean flag = this.currentMotion == motion
				|| (this.currentMotion == LivingMotion.INACTION && motion == LivingMotion.IDLE);

		if (flag)
			this.currentMotion = motion;

		return flag;
	}

	public boolean compareCompositeMotion(LivingMotion motion)
	{
		return this.currentMixMotion == motion;
	}

	public void startInaction()
	{
		this.currentMotion = LivingMotion.INACTION;
		this.entityCap.currentMotion = LivingMotion.INACTION;
	}

	public void resetCompositeMotion()
	{
		this.currentMixMotion = LivingMotion.NONE;
		this.entityCap.currentMixMotion = LivingMotion.NONE;
	}

	public boolean isAiming()
	{
		return this.currentMixMotion == LivingMotion.AIMING;
	}

	public void playReboundAnimation()
	{
		if (this.compositeLivingAnimations.containsKey(LivingMotion.SHOOTING))
		{
			this.playAnimation(this.compositeLivingAnimations.get(LivingMotion.SHOOTING), 0.0F);
			this.entityCap.currentMixMotion = LivingMotion.NONE;
			this.resetCompositeMotion();
		}
	}

	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation)
	{
		for (AnimationLayer layer : this.baseLayer.compositeLayers.values())
		{
			if (layer.animationPlayer.getPlay().equals(playingAnimation))
			{
				return layer.animationPlayer;
			}
		}

		return this.baseLayer.animationPlayer;
	}

	public LivingCap<?> getOwner()
	{
		return this.entityCap;
	}

	@Override
	public EntityState getEntityState()
	{
		return this.baseLayer.animationPlayer.getPlay().getState(this.baseLayer.animationPlayer.getElapsedTime());
	}
}