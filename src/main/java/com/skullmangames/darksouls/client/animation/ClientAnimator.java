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
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAnimator extends Animator
{
	public final AnimationLayer.BaseLayer baseLayer;
	private LivingMotion baseMotion;
	
	private final Map<LivingMotion, StaticAnimation> defaultLivingAnimations = new HashMap<>();
	private final Map<LayerPart, Map<LivingMotion, StaticAnimation>> animOverrides = new HashMap<>();
	private final Map<LayerPart, LivingMotion> mixMotions = new HashMap<>();

	public ClientAnimator(LivingCap<?> entityCap)
	{
		this.entityCap = entityCap;
		this.baseMotion = LivingMotion.IDLE;
		for (LayerPart part : LayerPart.mixLayers())
		{
			this.mixMotions.put(part, LivingMotion.NONE);
			this.animOverrides.put(part, new HashMap<>());
		}
		this.baseLayer = new AnimationLayer.BaseLayer();
	}
	
	public static Animator getAnimator(LivingCap<?> entityCap)
	{
		return entityCap.isClientSide() ? new ClientAnimator(entityCap) : ServerAnimator.getAnimator(entityCap);
	}
	
	public void putAnimOverride(LayerPart layerPart, LivingMotion motion, StaticAnimation anim)
	{
		this.animOverrides.get(layerPart).put(motion, anim);
	}
	
	public boolean isMotionActive(LivingMotion motion)
	{
		return this.baseMotion == motion || this.mixMotions.containsValue(motion);
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float startAt)
	{
		AnimationLayer layer = this.baseLayer.mixLayers.get(nextAnimation.getLayerPart());
		layer.paused = false;
		layer.playAnimation(nextAnimation, this.entityCap, startAt);
	}

	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation)
	{
		this.baseLayer.paused = false;
		this.baseLayer.playAnimation(nextAnimation, this.entityCap);
	}

	@Override
	public void putLivingAnimation(LivingMotion livingMotion, StaticAnimation animation)
	{
		if (animation != null) this.livingAnimations.put(livingMotion, animation);
	}

	public void setCurrentMotionsToDefault()
	{
		this.defaultLivingAnimations.putAll(this.livingAnimations);
	}

	@Override
	public void resetLivingAnimations()
	{
		super.resetLivingAnimations();
		this.animOverrides.forEach((layerPart, map) -> map.clear());
		this.defaultLivingAnimations.forEach((motion, animation) ->
		{
			this.livingAnimations.put(motion, animation);
			if (this.isMotionActive(motion))
			{
				StaticAnimation anim = this.getForLivingMotion(motion, LayerPart.FULL);
				this.playAnimation(anim, 0.0F);
			}
		});
	}

	public StaticAnimation getForLivingMotion(LivingMotion motion, LayerPart part)
	{
		return this.livingAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION).get(this.entityCap, part);
	}
	
	public StaticAnimation getOverrideAnim(LivingMotion motion, LayerPart part)
	{
		return this.animOverrides.get(part).getOrDefault(motion, Animations.DUMMY_ANIMATION);
	}

	public void setPoseToModel(float partialTicks)
	{
		Joint rootJoint = this.entityCap.getEntityModel(ClientModels.CLIENT).getArmature().getJointHierarcy();
		this.applyPoseToJoint(rootJoint, new ModMatrix4f(), this.getPose(partialTicks), partialTicks);
	}

	public void applyPoseToJoint(Joint joint, ModMatrix4f parentTransform, Pose pose, float partialTicks)
	{
		ModMatrix4f result = pose.getTransformByName(joint.getName()).getParentboundMatrix(joint, parentTransform);
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
		StaticAnimation idleMotion = this.getForLivingMotion(this.baseMotion, LayerPart.FULL);
		this.baseLayer.playAnimation(idleMotion, this.entityCap);
	}

	@Override
	public void updatePose()
	{
		this.prevPose = this.currentPose;
		this.currentPose = this.getDefinetivePose(1.0F);
	}

	@Override
	public void update()
	{
		this.baseLayer.update(this.entityCap);
		this.updatePose();
		
		StaticAnimation baseAnim = Animations.DUMMY_ANIMATION;
		
		if (this.baseLayer.animationPlayer.isEnd() && this.baseLayer.nextAnimation == null)
		{
			this.entityCap.updateMotion();
			baseAnim = this.getForLivingMotion(this.entityCap.baseMotion, LayerPart.FULL);
			this.baseLayer.playAnimation(baseAnim, this.entityCap, 0.0F);
		}
		
		boolean baseMotionChanged = this.baseMotion != this.entityCap.baseMotion;
		
		if (baseMotionChanged)
		{
			if (this.livingAnimations.containsKey(this.entityCap.baseMotion))
			{
				baseAnim = this.getForLivingMotion(this.entityCap.baseMotion, LayerPart.FULL);
				this.baseLayer.playAnimation(baseAnim, this.entityCap, 0.0F);
			}
		}
		
		for (LayerPart layerPart : LayerPart.mixLayers())
		{
			LivingMotion newMixMotion = this.entityCap.mixMotions.get(layerPart);
			if (this.mixMotions.get(layerPart) != newMixMotion || baseMotionChanged)
			{
				if (this.livingAnimations.containsKey(newMixMotion))
				{
					this.playAnimation(this.getForLivingMotion(newMixMotion, layerPart), 0.0F);
				}
				else if (this.animOverrides.get(layerPart).containsKey(this.entityCap.baseMotion))
				{
					this.playAnimation(this.getOverrideAnim(this.entityCap.baseMotion, layerPart), 0.0F);
				}
				else
				{
					this.getMixLayer(layerPart).off(this.entityCap);
				}
			}
		}

		this.baseMotion = this.entityCap.baseMotion;
		this.mixMotions.putAll(this.entityCap.mixMotions);
	}

	public AnimationLayer getMixLayer(AnimationLayer.LayerPart layerPart)
	{
		return this.baseLayer.mixLayers.get(layerPart);
	}

	public Pose getDefinetivePose(float partialTicks)
	{
		Pose composedPose = new Pose();
		
		Pose currentBasePose = this.baseLayer.animationPlayer.getCurrentPose(this.entityCap, partialTicks);
		currentBasePose.getJointTransformData().forEach((joint, transform) ->
		{
			composedPose.putJointData(joint, transform);
		});
		
		for (AnimationLayer.LayerPart layerPart : AnimationLayer.LayerPart.mixLayers())
		{
			AnimationLayer compositeLayer = this.baseLayer.mixLayers.get(layerPart);

			if (!compositeLayer.isDisabled())
			{
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entityCap, compositeLayer.paused ? 1.0F : partialTicks);
				layerPose.getJointTransformData().forEach((joint, transform) ->
				{
					if (compositeLayer.isJointEnabled(joint)) composedPose.putJointData(joint, transform);
				});
			}
		}
		return composedPose;
	}

	public Pose getHigherPose(AnimationLayer.LayerPart layerPart, float partialTicks)
	{
		Pose composedPose = new Pose();
		AnimationLayer layer = this.baseLayer.getHigherActiveLayer(layerPart);

		Pose layerPose = layer.animationPlayer.getCurrentPose(this.entityCap, layer.paused ? 1.0F : partialTicks);
		layerPose.getJointTransformData().forEach((joint, transform) ->
		{
			if (layer.isJointEnabled(joint)) composedPose.putJointData(joint, transform);
		});
		return composedPose;
	}

	public void startInaction()
	{
		this.baseMotion = LivingMotion.INACTION;
		this.entityCap.baseMotion = LivingMotion.INACTION;
	}

	public void resetMixMotions()
	{
		for (LayerPart part : LayerPart.mixLayers())
		{
			this.resetMixMotionFor(part);
		}
	}
	
	public void resetMixMotionFor(LayerPart part)
	{
		this.mixMotions.put(part, LivingMotion.NONE);
		this.entityCap.mixMotions.put(part, LivingMotion.NONE);
		this.getMixLayer(part).off(this.entityCap);
	}

	public boolean isAiming()
	{
		return this.mixMotions.containsValue(LivingMotion.AIMING);
	}

	public void playReboundAnimation()
	{
		if (this.livingAnimations.containsKey(LivingMotion.SHOOTING))
		{
			this.playAnimation(this.getForLivingMotion(LivingMotion.SHOOTING, LayerPart.FULL), 0.0F);
			for (LayerPart part : LayerPart.mixLayers()) this.mixMotions.put(part, LivingMotion.NONE);
			this.resetMixMotions();
		}
	}

	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation)
	{
		for (AnimationLayer layer : this.baseLayer.mixLayers.values())
		{
			if (layer.animationPlayer.getPlay().equals(playingAnimation))
			{
				return layer.animationPlayer;
			}
		}

		return this.getMainPlayer();
	}
	
	@Override
	public AnimationPlayer getMainPlayer()
	{
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