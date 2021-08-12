package com.skullmangames.darksouls.client.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.animation.AnimationPlayer;
import com.skullmangames.darksouls.animation.Animator;
import com.skullmangames.darksouls.animation.Joint;
import com.skullmangames.darksouls.animation.JointTransform;
import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.animation.Pose;
import com.skullmangames.darksouls.animation.types.MirrorAnimation;
import com.skullmangames.darksouls.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.common.entities.LivingData.EntityState;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ClientModelInit;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

public class AnimatorClient extends Animator
{
	private final Map<LivingMotion, StaticAnimation> livingAnimations = new HashMap<LivingMotion, StaticAnimation>();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
	private List<LivingMotion> modifiedLivingMotions;
	public final BaseLayer baseLayer;
	public final MixLayer mixLayer;
	private LivingMotion currentMotion;
	private LivingMotion currentMixMotion;
	private boolean reversePlay = false;
	public boolean mixLayerActivated = false;
	
	public AnimatorClient(LivingData<?> entitydata)
	{
		this.entitydata = entitydata;
		this.baseLayer = new BaseLayer(Animations.DUMMY_ANIMATION);
		this.mixLayer = new MixLayer(Animations.DUMMY_ANIMATION);
		this.currentMotion = LivingMotion.IDLE;
		this.currentMixMotion = LivingMotion.NONE;
		this.defaultLivingAnimations = new HashMap<LivingMotion, StaticAnimation>();
		this.modifiedLivingMotions = new ArrayList<LivingMotion>();
	}

	@Override
	public void playAnimation(int id, float modifyTime) {
		this.playAnimation(Animations.findAnimationDataById(id), modifyTime);
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime)
	{
		this.baseLayer.pause = false;
		this.mixLayer.pause = false;
		this.reversePlay = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitydata, modifyTime);
	}
	
	private void playAnimationLiving(StaticAnimation nextAnimation, float modifyTime)
	{
		this.baseLayer.pause = false;
		this.mixLayer.pause = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitydata, modifyTime);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation)
	{
		
	}
	
	@Override
	public void vacateCurrentPlay()
	{
		this.baseLayer.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	public void addLivingAnimation(LivingMotion motion, StaticAnimation animation)
	{
		this.livingAnimations.put(motion, animation);
		
		if (motion == this.currentMotion)
		{
			if (!this.entitydata.isInaction())
			{
				playAnimationLiving(animation, 0);
			}
		}
	}
	
	public void addLivingMixAnimation(LivingMotion motion, StaticAnimation animation)
	{
		this.livingAnimations.put(motion, animation);

		if (motion == this.currentMotion)
		{
			if (!this.entitydata.isInaction())
			{
				if (animation instanceof MirrorAnimation)
				{
					playMixLayerAnimation(((MirrorAnimation)animation).checkHandAndReturnAnimation(this.entitydata.getOriginalEntity().getUsedItemHand()));
				}
				else
				{
					playMixLayerAnimation(animation);
				}
			}
		}
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
			this.playAnimationLiving(this.livingAnimations.get(this.entitydata.currentMotion), 0.0F);
		}
	}
	
	public void playMixLoopMotion()
	{
		if(this.entitydata.currentMixMotion == LivingMotion.NONE)
		{
			this.offMixLayer(false);
		}
		else
		{
			StaticAnimation animation = this.livingAnimations.get(this.entitydata.currentMixMotion);
			
			if(animation instanceof MirrorAnimation)
			{
				this.playMixLayerAnimation(((MirrorAnimation)animation).checkHandAndReturnAnimation(this.entitydata.getOriginalEntity().getUsedItemHand()));
			}
			else
			{
				this.playMixLayerAnimation(animation);
			}
		}
		this.mixLayer.pause = false;
		this.currentMixMotion = this.entitydata.currentMixMotion;
	}

	public void playMixLayerAnimation(int id)
	{
		playMixLayerAnimation(Animations.findAnimationDataById(id));
	}
	
	public void playMixLayerAnimation(StaticAnimation nextAnimation)
	{
		if (!this.mixLayerActivated)
		{
			this.mixLayerActivated = true;
			this.mixLayer.animationPlayer.synchronize(this.baseLayer.animationPlayer);
		}
		this.mixLayer.linkEndPhase = false;
		this.mixLayer.playAnimation(nextAnimation, this.entitydata, 0);
	}
	
	public void offMixLayer(boolean byForce)
	{
		if (this.mixLayerActivated && (byForce || this.mixLayer.animationPlayer.getPlay().getState(this.mixLayer.animationPlayer.getElapsedTime()) != EntityState.POST_DELAY))
		{
			this.mixLayer.linkEndPhase = true;
			this.mixLayer.setMixLinkAnimation(entitydata, 0);
			this.mixLayer.playAnimation(this.mixLayer.mixLinkAnimation, this.entitydata);
			this.mixLayer.nextPlaying = null;
			this.mixLayer.pause = false;
		}
	}
	
	public void disableMixLayer()
	{
		this.mixLayerActivated = false;

		if (this.mixLayer.animationPlayer.getPlay() != null)
		{
			this.mixLayer.animationPlayer.getPlay().onFinish(this.entitydata, true);
			this.mixLayer.animationPlayer.setEmpty();
		}

		this.mixLayer.animationPlayer.resetPlayer();
	}
	
	public void setPoseToModel(float partialTicks)
	{
		Joint rootJoint = this.entitydata.getEntityModel(ClientModelInit.CLIENT).getArmature().getJointHierarcy();
		if(this.mixLayerActivated)
		{
			applyPoseToJoint(getCurrentPose(this.baseLayer, partialTicks), getCurrentPose(this.mixLayer, partialTicks), rootJoint, new PublicMatrix4f());
		}
		else
		{
			applyPoseToJoint(getCurrentPose(this.baseLayer, partialTicks), rootJoint, new PublicMatrix4f());
		}
	}
	
	private void applyPoseToJoint(Pose base, Pose mix, Joint joint, PublicMatrix4f parentTransform)
	{
		if (this.mixLayer.jointMasked(joint.getName()))
		{
			PublicMatrix4f currentLocalTransformBase = base.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformBase, currentLocalTransformBase);
			PublicMatrix4f bindTransformBase = PublicMatrix4f.mul(parentTransform, currentLocalTransformBase, null);
			
			PublicMatrix4f currentLocalTransformMix = mix.getTransformByName(joint.getName()).toTransformMatrix();
			PublicMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformMix, currentLocalTransformMix);
			PublicMatrix4f bindTransformMix = PublicMatrix4f.mul(parentTransform, currentLocalTransformMix, null);
			
			bindTransformMix.m31 = bindTransformBase.m31;
			joint.setAnimatedTransform(bindTransformMix);
			
			for (Joint joints : joint.getSubJoints())
			{
				if(this.mixLayer.jointMasked(joints.getName()) || this.currentMotion == LivingMotion.IDLE)
				{
					applyPoseToJoint(mix, joints, bindTransformMix);
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
				applyPoseToJoint(base, mix, joints, bindTransform);
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
		
		if (this.mixLayerActivated)
		{
			this.mixLayer.update(this.entitydata);
			if (this.mixLayer.animationPlayer.isEnd())
			{
				if (this.mixLayer.linkEndPhase) {
					if (this.mixLayer.nextPlaying == null)
					{
						disableMixLayer();
						this.mixLayer.linkEndPhase = false;
					}
				}
				else
				{
					this.mixLayer.animationPlayer.getPlay().onFinish(this.entitydata, this.mixLayer.animationPlayer.isEnd());
					if (!this.mixLayer.pause)
					{
						this.mixLayer.setMixLinkAnimation(this.entitydata, 0);
						this.mixLayer.playAnimation(this.mixLayer.mixLinkAnimation, this.entitydata);
						this.mixLayer.linkEndPhase = true;
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
		this.mixLayer.clear(this.entitydata);
	}

	public Pose getCurrentPose(BaseLayer layer, float partialTicks)
	{
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
			player = this.mixLayer.animationPlayer;
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
	
	public AnimationPlayer getMixLayerPlayer()
	{
		return this.mixLayer.animationPlayer;
	}
}