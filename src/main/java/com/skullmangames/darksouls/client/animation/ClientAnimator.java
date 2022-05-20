package com.skullmangames.darksouls.client.animation;

import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.Joint;
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

	private final Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
	public final AnimationLayer.BaseLayer baseLayer;
	private LivingMotion currentMotion;
	private Map<AnimationLayer.LayerPart, LivingMotion> currentMixMotions = new HashMap<>();

	public ClientAnimator(LivingCap<?> entityCap)
	{
		this.entityCap = entityCap;
		this.currentMotion = LivingMotion.IDLE;
		for (LayerPart part : LayerPart.compositeLayers()) this.currentMixMotions.put(part, LivingMotion.NONE);
		this.defaultLivingAnimations = new HashMap<>();
		this.baseLayer = new AnimationLayer.BaseLayer();
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float convertTimeModifier)
	{
		AnimationLayer layer = this.baseLayer.compositeLayers.get(nextAnimation.getLayerPart());
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
		if (animation != null) this.livingAnimations.put(livingMotion, animation);
	}

	public void setCurrentMotionsToDefault()
	{
		this.livingAnimations.forEach(this.defaultLivingAnimations::put);
	}

	@Override
	public void resetMotions()
	{
		super.resetMotions();
		this.defaultLivingAnimations.forEach(this.livingAnimations::put);
	}

	public StaticAnimation getLivingMotion(LivingMotion motion)
	{
		return this.livingAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION).checkAndReturnAnimation(this.entityCap);
	}

	public void setPoseToModel(float partialTicks)
	{
		Joint rootJoint = this.entityCap.getEntityModel(ClientModels.CLIENT).getArmature().getJointHierarcy();
		this.applyPoseToJoint(rootJoint, new PublicMatrix4f(), this.getPose(partialTicks), partialTicks);
	}

	public void applyPoseToJoint(Joint joint, PublicMatrix4f parentTransform, Pose pose, float partialTicks)
	{
		PublicMatrix4f result = pose.getTransformByName(joint.getName()).getParentboundMatrix(joint, parentTransform);
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
		
		for (AnimationLayer.LayerPart layerPart : AnimationLayer.LayerPart.compositeLayers())
		{
			LivingMotion motion = this.entityCap.currentMixMotions.get(layerPart);
			if (!this.compareCompositeMotion(layerPart, motion))
			{
				if (this.livingAnimations.containsKey(motion))
				{
					this.playAnimation(this.getLivingMotion(motion), 0.0F);
				}
				else
				{
					this.getCompositeLayer(layerPart).off(this.entityCap);
				}
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
		this.currentMixMotions.putAll(this.entityCap.currentMixMotions);
	}

	@Override
	public void playDeathAnimation()
	{
		this.playAnimation(this.livingAnimations.get(LivingMotion.DEATH), 0);
		this.currentMotion = LivingMotion.DEATH;
	}

	public AnimationLayer getCompositeLayer(AnimationLayer.LayerPart layerPart)
	{
		return this.baseLayer.compositeLayers.get(layerPart);
	}

	public Pose getComposedLayerPose(float partialTicks)
	{
		Pose composedPose = new Pose();
		
		Pose currentBasePose = this.baseLayer.animationPlayer.getCurrentPose(this.entityCap, partialTicks);
		currentBasePose.getJointTransformData().forEach((joint, transform) ->
		{
			composedPose.putJointData(joint, transform);
		});
		
		for (AnimationLayer.LayerPart layerPart : AnimationLayer.LayerPart.compositeLayers())
		{
			AnimationLayer compositeLayer = this.baseLayer.compositeLayers.get(layerPart);

			if (!compositeLayer.isDisabled())
			{
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entityCap, compositeLayer.paused ? 1.0F : partialTicks);
				layerPose.getJointTransformData().forEach((joint, transform) ->
				{
					if (compositeLayer.isJointEnabled(joint)) composedPose.getJointTransformData().put(joint, transform);
				});
			}
		}
		return composedPose;
	}

	public Pose getComposedLayerPoseFromOthers(AnimationLayer.LayerPart layerPart, float partialTicks)
	{
		Pose composedPose = this.baseLayer.animationPlayer.getCurrentPose(this.entityCap, partialTicks);

		Pose currentBasePose = this.baseLayer.animationPlayer.getCurrentPose(this.entityCap, partialTicks);
		currentBasePose.getJointTransformData().forEach((joint, transform) ->
		{
			composedPose.putJointData(joint, transform);
		});
		
		for (AnimationLayer.LayerPart part : layerPart.otherCompositeLayers())
		{
			AnimationLayer compositeLayer = this.baseLayer.compositeLayers.get(part);

			if (!compositeLayer.isDisabled())
			{
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entityCap, compositeLayer.paused ? 1.0F : partialTicks);
				layerPose.getJointTransformData().forEach((joint, transform) ->
				{
					if (compositeLayer.isJointEnabled(joint)) composedPose.getJointTransformData().put(joint, transform);
				});
			}
		}
		return composedPose;
	}

	public boolean compareMotion(LivingMotion motion)
	{
		boolean flag = this.currentMotion == motion
				|| (this.currentMotion == LivingMotion.INACTION && motion == LivingMotion.IDLE);

		if (flag)
			this.currentMotion = motion;

		return flag;
	}

	public boolean compareCompositeMotion(AnimationLayer.LayerPart layerPart, LivingMotion motion)
	{
		return this.currentMixMotions.get(layerPart) == motion;
	}

	public void startInaction()
	{
		this.currentMotion = LivingMotion.INACTION;
		this.entityCap.currentMotion = LivingMotion.INACTION;
	}

	public void resetCompositeMotion()
	{
		for (LayerPart part : LayerPart.compositeLayers())
		{
			this.currentMixMotions.put(part, LivingMotion.NONE);
			this.entityCap.currentMixMotions.put(part, LivingMotion.NONE);
		}
	}

	public boolean isAiming()
	{
		return this.currentMixMotions.containsValue(LivingMotion.AIMING);
	}

	public void playReboundAnimation()
	{
		if (this.livingAnimations.containsKey(LivingMotion.SHOOTING))
		{
			this.playAnimation(this.livingAnimations.get(LivingMotion.SHOOTING), 0.0F);
			for (LayerPart part : LayerPart.compositeLayers()) this.currentMixMotions.put(part, LivingMotion.NONE);
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