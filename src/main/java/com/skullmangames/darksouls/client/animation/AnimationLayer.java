package com.skullmangames.darksouls.client.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.LayerOffAnimation;
import com.skullmangames.darksouls.common.animation.types.LinkAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationLayer
{
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextAnimation;
	protected LinkAnimation linkAnimationStorage;
	protected LayerOffAnimation layerOffAnimation;
	protected boolean disabled;
	protected boolean paused;
	private List<String> jointMask;

	public AnimationLayer(LayerPart part)
	{
		this.animationPlayer = new AnimationPlayer();
		this.linkAnimationStorage = new LinkAnimation();
		this.layerOffAnimation = new LayerOffAnimation(part);
		this.disabled = true;
		if (part == LayerPart.RIGHT) this.jointMask = new ArrayList<>(Arrays.asList("Shoulder_R", "Arm_R", "Ellbow_R", "Tool_R", "Hand_R"));
		else if (part == LayerPart.LEFT) this.jointMask = new ArrayList<>(Arrays.asList("Shoulder_L", "Arm_L", "Ellbow_L", "Tool_L", "Hand_L"));
		else if (part == LayerPart.UP) this.jointMask = new ArrayList<>(Arrays.asList("Shoulder_R", "Arm_R", "Ellbow_R", "Tool_R", "Hand_R",
				"Shoulder_L", "Arm_L", "Ellbow_L", "Tool_L", "Hand_L", "Head", "Chest"));
	}
	
	public boolean isJointEnabled(String joint)
	{
		return this.jointMask.contains(joint);
	}

	public void playAnimation(StaticAnimation nextAnimation, LivingCap<?> entityCap, float convertTimeModifier)
	{
		Pose lastPose = entityCap.getAnimator().getPose(Minecraft.getInstance().getFrameTime());
		this.animationPlayer.getPlay().onFinish(entityCap, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.onStart(entityCap);

		this.setLinkAnimation(nextAnimation, entityCap, lastPose, convertTimeModifier);
		this.linkAnimationStorage.putOnPlayer(this.animationPlayer);
		this.nextAnimation = nextAnimation;
	}

	public void playAnimation(DynamicAnimation nextAnimation, LivingCap<?> entityCap)
	{
		this.animationPlayer.getPlay().onFinish(entityCap, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.onStart(entityCap);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextAnimation = null;
	}

	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingCap<?> entityCap, Pose lastPose,
			float convertTimeModifier)
	{
		nextAnimation.setLinkAnimation(lastPose, convertTimeModifier, entityCap, this.linkAnimationStorage);
	}

	public void update(LivingCap<?> entityCap)
	{
		if (this.paused || this.animationPlayer.isEmpty())
		{
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}

		this.animationPlayer.update(entityCap);
		this.animationPlayer.getPlay().onUpdate(entityCap);

		if (this.animationPlayer.isEnd())
		{
			if (this.nextAnimation != null)
			{
				float exceedTime = this.animationPlayer.getExceedTime();
				this.animationPlayer.getPlay().onFinish(entityCap, true);

				if (!(this.animationPlayer.getPlay() instanceof LinkAnimation)
						&& !(this.nextAnimation instanceof LinkAnimation))
				{
					this.nextAnimation.onStart(entityCap);
				}
				
				this.nextAnimation.putOnPlayer(this.animationPlayer);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime * 2); // Probably unfinished
				
				this.nextAnimation = null;
			}
			else
			{
				if (this.animationPlayer.getPlay() instanceof LayerOffAnimation)
				{
					this.animationPlayer.getPlay().onFinish(entityCap, true);
				}
				else
				{
					this.off(entityCap);
				}
			}
		}
	}

	public void pause()
	{
		this.paused = true;
	}

	public void resume()
	{
		this.paused = false;
		this.disabled = false;
	}

	protected boolean isDisabled()
	{
		return this.disabled;
	}

	public void off(LivingCap<?> entityCap)
	{
		if (!this.isDisabled() && !(this.animationPlayer.getPlay() instanceof LayerOffAnimation))
		{
			float convertTime = entityCap.getClientAnimator().baseLayer.animationPlayer.getPlay().getConvertTime();
			setLayerOffAnimation(this.animationPlayer.getPlay(), this.animationPlayer.getCurrentPose(entityCap, 1.0F),
					this.layerOffAnimation, convertTime);
			this.playAnimation(this.layerOffAnimation, entityCap);
		}
	}

	public static void setLayerOffAnimation(DynamicAnimation currentAnimation, Pose currentPose,
			LayerOffAnimation offAnimation, float convertTime)
	{
		offAnimation.setLastAnimation(currentAnimation.getRealAnimation());
		offAnimation.setLastPose(currentPose);
		offAnimation.setTotalTime(convertTime);
	}

	public static class BaseLayer extends AnimationLayer
	{
		protected Map<LayerPart, AnimationLayer> mixLayers = new HashMap<>();

		public BaseLayer()
		{
			super(LayerPart.FULL);
			for (LayerPart part : LayerPart.mixLayers()) this.mixLayers.computeIfAbsent(part, AnimationLayer::new);
			this.mixLayers.put(LayerPart.FULL, this);
		}
		
		@Override
		public boolean isJointEnabled(String joint)
		{
			return true;
		}

		@Override
		public void update(LivingCap<?> entityCap)
		{
			super.update(entityCap);

			for (AnimationLayer layer : this.mixLayers.values())
			{
				if (layer != this)
				{
					layer.update(entityCap);
				}
			}
		}

		public void disableLayer(LayerPart part)
		{
			AnimationLayer layer = this.mixLayers.get(part);
			layer.disabled = true;
			Animations.DUMMY_ANIMATION.putOnPlayer(layer.animationPlayer);
		}

		@Override
		public void off(LivingCap<?> entityCap) {}

		@Override
		protected boolean isDisabled()
		{
			return false;
		}
	}
	
	public static enum LayerPart
	{
		FULL, UP, LEFT, RIGHT;
		
		public static LayerPart[] mixLayers()
		{
			return new LayerPart[] { UP, LEFT, RIGHT };
		}
		
		public LayerPart[] otherMixLayers()
		{
			if (this == UP) return new LayerPart[] { LEFT, RIGHT };
			if (this == LEFT || this == RIGHT) return new LayerPart[] { UP };
			return mixLayers();
		}
		
		public static LayerPart fromHand(InteractionHand hand)
		{
			return hand == InteractionHand.MAIN_HAND ? LayerPart.RIGHT
					: LayerPart.LEFT;
		}
	}
}
