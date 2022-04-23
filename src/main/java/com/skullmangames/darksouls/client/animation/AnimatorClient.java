package com.skullmangames.darksouls.client.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.Joint;
import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.types.AdaptableAnimation;
import com.skullmangames.darksouls.common.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap.EntityState;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

public class AnimatorClient extends Animator
{
	private final Map<LivingMotion, StaticAnimation> livingAnimations = new HashMap<LivingMotion, StaticAnimation>();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
	private List<LivingMotion> modifiedLivingMotions;
	public final BaseLayer baseLayer;
	public final MixLayer mixLayerLeft;
	public final MixLayer mixLayerRight;
	private LivingMotion currentMotion;
	private LivingMotion currentMixMotion;
	private boolean reversePlay = false;
	
	public AnimatorClient(LivingCap<?> entitydata)
	{
		this.entitydata = entitydata;
		this.baseLayer = new BaseLayer(Animations.DUMMY_ANIMATION);
		this.mixLayerLeft = new MixLayer(Animations.DUMMY_ANIMATION);
		this.mixLayerRight = new MixLayer(Animations.DUMMY_ANIMATION);
		this.currentMotion = LivingMotion.IDLE;
		this.currentMixMotion = LivingMotion.NONE;
		this.defaultLivingAnimations = new HashMap<LivingMotion, StaticAnimation>();
		this.modifiedLivingMotions = new ArrayList<LivingMotion>();
	}

	@Override
	public void playAnimation(int id, float modifyTime)
	{
		this.playAnimation(Animations.getById(id), modifyTime);
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime)
	{
		this.baseLayer.pause = false;
		this.mixLayerLeft.pause = false;
		this.mixLayerRight.pause = false;
		this.reversePlay = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitydata, modifyTime);
	}
	
	private void playAnimationLiving(StaticAnimation nextAnimation, float modifyTime)
	{
		this.baseLayer.pause = false;
		this.mixLayerLeft.pause = false;
		this.mixLayerRight.pause = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitydata, modifyTime);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation) {}
	
	@Override
	public void vacateCurrentPlay()
	{
		this.baseLayer.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	public void addLivingAnimation(LivingMotion motion, StaticAnimation animation)
	{
		this.livingAnimations.put(motion, animation);
	}
	
	public void addModifiedLivingMotion(LivingMotion motion, StaticAnimation animation)
	{
		if (!this.modifiedLivingMotions.contains(motion))
		{
			this.modifiedLivingMotions.add(motion);
		}
		
		this.addLivingAnimation(motion, animation);
	}
	
	public void resetModifiedLivingMotions()
	{
		if (this.modifiedLivingMotions != null)
		{
			for (LivingMotion livingMotion : this.modifiedLivingMotions)
			{
				this.addLivingAnimation(livingMotion, this.defaultLivingAnimations.get(livingMotion));
			}
			
			this.modifiedLivingMotions.clear();
		}
	}
	
	public void setCurrentLivingMotionsToDefault()
	{
		this.defaultLivingAnimations.clear();
		this.defaultLivingAnimations.putAll(this.livingAnimations);
	}
	
	public void playLoopMotion()
	{
		this.currentMotion = this.entitydata.currentMotion;
		if(this.livingAnimations.containsKey(this.entitydata.currentMotion))
		{
			StaticAnimation animation = this.livingAnimations.get(this.entitydata.currentMotion);
			if (animation instanceof AdaptableAnimation)
			{
				animation = ((AdaptableAnimation)animation).getAnimation(this.currentMotion, this.entitydata.getOriginalEntity().getUsedItemHand());
			}
			else if(animation instanceof MirrorAnimation)
			{
				animation = ((MirrorAnimation)animation).getAnimation(this.entitydata.getOriginalEntity().getUsedItemHand());
			}
			if (animation != null) this.playAnimationLiving(animation, 0.0F);
		}
	}
	
	public void playMixLoopMotion()
	{
		if(this.entitydata.currentMixMotion == LivingMotion.NONE)
		{
			this.offMixLayer(this.mixLayerLeft, false);
			this.offMixLayer(this.mixLayerRight, false);
		}
		else
		{
			StaticAnimation animation = this.livingAnimations.get(this.entitydata.currentMixMotion);
			if (this.entitydata.currentMixMotion == LivingMotion.HOLDING_WEAPON)
			{
				animation = this.entitydata.getHoldingWeaponAnimation();
			}
			else if (animation instanceof AdaptableAnimation)
			{
				animation = ((AdaptableAnimation)animation).getAnimation(this.currentMotion, this.entitydata.getOriginalEntity().getUsedItemHand());
			}
			else if(animation instanceof MirrorAnimation && this.entitydata.getOriginalEntity().isUsingItem())
			{
				animation = ((MirrorAnimation)animation).getAnimation(this.entitydata.getOriginalEntity().getUsedItemHand());
			}
			if (animation != null) this.playMixLayerAnimation(animation);
		}
		this.mixLayerLeft.pause = false;
		this.mixLayerRight.pause = false;
		this.currentMixMotion = this.entitydata.currentMixMotion;
	}

	public void playMixLayerAnimation(int id)
	{
		playMixLayerAnimation(Animations.getById(id));
	}
	
	public void playMixLayerAnimation(StaticAnimation nextAnimation)
	{
		if (!this.mixLayerLeft.isActive()) this.mixLayerLeft.animationPlayer.synchronize(this.baseLayer.animationPlayer);
		if (!this.mixLayerRight.isActive()) this.mixLayerRight.animationPlayer.synchronize(this.baseLayer.animationPlayer);
		
		switch (nextAnimation.getMixPart())
		{
			case LEFT:
				this.mixLayerLeft.linkEndPhase = false;
				this.mixLayerLeft.playAnimation(nextAnimation, this.entitydata, 0);
				break;
				
			case RIGHT:
				this.mixLayerRight.linkEndPhase = false;
				this.mixLayerRight.playAnimation(nextAnimation, this.entitydata, 0);
				break;
				
			case FULL:
				this.mixLayerLeft.linkEndPhase = false;
				this.mixLayerRight.linkEndPhase = false;
				this.mixLayerLeft.playAnimation(nextAnimation, this.entitydata, 0);
				this.mixLayerRight.playAnimation(nextAnimation, this.entitydata, 0);
				break;
		}
	}
	
	public void offMixLayer(MixLayer mixLayer, boolean byForce)
	{
		if (mixLayer.isActive() && (byForce || (mixLayer.animationPlayer.getPlay().getState(mixLayer.animationPlayer.getElapsedTime()) != EntityState.POST_DELAY)))
		{
			mixLayer.linkEndPhase = true;
			mixLayer.setMixLinkAnimation(entitydata, 0);
			mixLayer.playAnimation(mixLayer.mixLinkAnimation, this.entitydata);
			mixLayer.nextPlaying = null;
			mixLayer.pause = false;
		}
	}
	
	public void disableMixLayer(MixLayer mixLayer)
	{
		if (mixLayer.animationPlayer.getPlay() != null)
		{
			mixLayer.animationPlayer.getPlay().onFinish(this.entitydata, true);
			mixLayer.animationPlayer.setEmpty();
		}

		mixLayer.animationPlayer.resetPlayer();
	}
	
	public boolean mixLayerActivated()
	{
		return this.mixLayerLeft.isActive()|| this.mixLayerRight.isActive();
	}
	
	public void setPoseToModel(float partialTicks)
	{
		Joint rootJoint = this.entitydata.getEntityModel(ClientModels.CLIENT).getArmature().getJointHierarcy();
		if(this.mixLayerActivated())
		{
			this.applyPoseToJoint(this.getCurrentPose(this.baseLayer, partialTicks), this.getCurrentPose(this.mixLayerLeft, partialTicks), this.getCurrentPose(this.mixLayerRight, partialTicks), rootJoint, new PublicMatrix4f());
		}
		else
		{
			this.applyPoseToJoint(this.getCurrentPose(this.baseLayer, partialTicks), rootJoint, new PublicMatrix4f());
		}
	}
	
	private void applyPoseToJoint(Pose base, Pose mixLeft, Pose mixRight, Joint joint, PublicMatrix4f parentTransform)
	{
		if (this.mixLayerLeft.isActive() && this.mixLayerLeft.jointMasked(joint.getName()))
		{
			PublicMatrix4f currentLocalTransformBase = base.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformBase, currentLocalTransformBase);
			PublicMatrix4f bindTransformBase = PublicMatrix4f.mul(parentTransform, currentLocalTransformBase, null);
			
			PublicMatrix4f currentLocalTransformMixLeft = mixLeft.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformMixLeft, currentLocalTransformMixLeft);
			PublicMatrix4f bindTransformMixLeft = PublicMatrix4f.mul(parentTransform, currentLocalTransformMixLeft, null);
			
			bindTransformMixLeft.m31 = bindTransformBase.m31;
			joint.setAnimatedTransform(bindTransformMixLeft);
			
			for (Joint joints : joint.getSubJoints())
			{
				if(this.mixLayerLeft.jointMasked(joints.getName()))
				{
					applyPoseToJoint(mixLeft, joints, bindTransformMixLeft);
				}
				else
				{
					applyPoseToJoint(base, joints, bindTransformBase);
				}
			}
		}
		else if (this.mixLayerRight.isActive() && this.mixLayerRight.jointMasked(joint.getName()))
		{
			PublicMatrix4f currentLocalTransformBase = base.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformBase, currentLocalTransformBase);
			PublicMatrix4f bindTransformBase = PublicMatrix4f.mul(parentTransform, currentLocalTransformBase, null);
			
			PublicMatrix4f currentLocalTransformMixRight = mixRight.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformMixRight, currentLocalTransformMixRight);
			PublicMatrix4f bindTransformMixRight = PublicMatrix4f.mul(parentTransform, currentLocalTransformMixRight, null);
			
			bindTransformMixRight.m31 = bindTransformBase.m31;
			joint.setAnimatedTransform(bindTransformMixRight);
			
			for (Joint joints : joint.getSubJoints())
			{
				if(this.mixLayerRight.jointMasked(joints.getName()))
				{
					applyPoseToJoint(mixRight, joints, bindTransformMixRight);
				}
				else
				{
					applyPoseToJoint(base, joints, bindTransformBase);
				}
			}
		}
		else
		{
			PublicMatrix4f currentLocalTransform = base.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
			PublicMatrix4f bindTransform = PublicMatrix4f.mul(parentTransform, currentLocalTransform, null);
			joint.setAnimatedTransform(bindTransform);
			
			for(Joint joints : joint.getSubJoints())
			{
				applyPoseToJoint(base, mixLeft, mixRight, joints, bindTransform);
			}
		}
	}
	
	private void applyPoseToJoint(Pose pose, Joint joint, PublicMatrix4f parentTransform)
	{
		JointTransform jt = pose.getTransformByName(joint.getName());
		PublicMatrix4f currentLocalTransform = jt.toTransformMatrix();
		PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
		PublicMatrix4f bindTransform = PublicMatrix4f.mul(parentTransform, currentLocalTransform, null);
		PublicMatrix4f.mul(bindTransform, joint.getAnimatedTransform(), bindTransform);
		
		if (jt.getCustomRotation() != null)
		{
			float x = bindTransform.m30;
			float y = bindTransform.m31;
			float z = bindTransform.m32;
			bindTransform.m30 = 0;
			bindTransform.m31 = 0;
			bindTransform.m32 = 0;
			PublicMatrix4f.mul(jt.getCustomRotation().toRotationMatrix(), bindTransform, bindTransform);
			bindTransform.m30 = x;
			bindTransform.m31 = y;
			bindTransform.m32 = z;
		}
		
		joint.setAnimatedTransform(bindTransform);
		
		for (Joint joints : joint.getSubJoints())
		{
			applyPoseToJoint(pose, joints, bindTransform);
		}
	}
	
	@Override
	public void update()
	{
		this.baseLayer.update(this.entitydata);
		if (this.baseLayer.animationPlayer.isEnd())
		{
			if (this.baseLayer.nextPlaying == null && this.currentMotion != LivingMotion.DEATH)
			{
				this.entitydata.updateMotion();
				playLoopMotion();
			}
		}
		
		if (this.mixLayerActivated())
		{
			MixLayer[] mixLayers = new MixLayer[] { this.mixLayerLeft, this.mixLayerRight };
			for (MixLayer mixLayer : mixLayers)
			{
				mixLayer.update(this.entitydata);
				if (mixLayer.animationPlayer.isEnd())
				{
					if (mixLayer.linkEndPhase)
					{
						if (mixLayer.nextPlaying == null)
						{
							disableMixLayer(mixLayer);
							mixLayer.linkEndPhase = false;
						}
					}
					else
					{
						mixLayer.animationPlayer.getPlay().onFinish(this.entitydata, mixLayer.animationPlayer.isEnd());
						if (!mixLayer.pause)
						{
							mixLayer.setMixLinkAnimation(this.entitydata, 0);
							mixLayer.playAnimation(mixLayer.mixLinkAnimation, this.entitydata);
							mixLayer.linkEndPhase = true;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void playDeathAnimation()
	{
		this.playAnimation(livingAnimations.get(LivingMotion.DEATH), 0);
		this.currentMotion = LivingMotion.DEATH;
	}
	
	public StaticAnimation getJumpAnimation()
	{
		return this.livingAnimations.get(LivingMotion.JUMPING);
	}
	
	@Override
	public void onEntityDeath()
	{
		this.baseLayer.clear(this.entitydata);
		this.mixLayerLeft.clear(this.entitydata);
		this.mixLayerRight.clear(this.entitydata);
	}

	@Nullable
	public Pose getCurrentPose(BaseLayer layer, float partialTicks)
	{
		if (layer instanceof MixLayer && !((MixLayer)layer).isActive()) return null;
		return layer.animationPlayer.getCurrentPose(this.entitydata, this.baseLayer.pause ? 1 : partialTicks);
	}

	public boolean compareMotion(LivingMotion motion)
	{
		return this.currentMotion == motion;
	}

	public boolean compareMixMotion(LivingMotion motion)
	{
		return this.currentMixMotion == motion;
	}

	public void resetMotion()
	{
		this.currentMotion = LivingMotion.IDLE;
	}

	public void resetMixMotion()
	{
		this.currentMixMotion = LivingMotion.NONE;
	}

	public boolean prevAiming()
	{
		return this.currentMixMotion == LivingMotion.AIMING;
	}

	public void playReboundAnimation()
	{
		this.playMixLayerAnimation(this.livingAnimations.get(LivingMotion.SHOTING));
		this.entitydata.resetLivingMixLoop();
	}
	
	@Override
	public boolean isReverse()
	{
		return this.reversePlay;
	}
	
	public void setReverse(boolean flag, LivingMotion motion)
	{
		if(this.reversePlay != flag && this.currentMotion == motion)
		{
			AnimationPlayer player = this.baseLayer.animationPlayer;
			player.setElapsedTime(player.getPlay().getTotalTime() - player.getElapsedTime());
		}
		this.reversePlay = flag;
	}
	
	@Override
	public AnimationPlayer getPlayer()
	{
		return this.baseLayer.animationPlayer;
	}
	
	@Override
	public AnimationPlayer getPlayerFor(StaticAnimation animation)
	{
		AnimationPlayer player = this.baseLayer.animationPlayer;
		
		if(player.getPlay().equals(animation))
		{
			return player;
		}
		else
		{
			player = this.mixLayerLeft.animationPlayer;
		}
		
		if(player.getPlay().equals(animation))
		{
			return player;
		}
		else
		{
			player = this.mixLayerRight.animationPlayer;
		}
		
		if(player.getPlay().equals(animation))
		{
			return player;
		}
		else
		{
			return null;
		}
	}
	
	public AnimationPlayer getLeftMixLayerPlayer()
	{
		return this.mixLayerLeft.animationPlayer;
	}
	
	public AnimationPlayer getRightMixLayerPlayer()
	{
		return this.mixLayerRight.animationPlayer;
	}
}