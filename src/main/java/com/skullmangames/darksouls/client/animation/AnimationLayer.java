package com.skullmangames.darksouls.client.animation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.LayerOffAnimation;
import com.skullmangames.darksouls.common.animation.types.LinkAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Animations;

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

	public AnimationLayer(Priority priority)
	{
		this.animationPlayer = new AnimationPlayer();
		this.linkAnimationStorage = new LinkAnimation();
		this.layerOffAnimation = new LayerOffAnimation(priority);
		this.disabled = true;
	}

	public void playAnimation(StaticAnimation nextAnimation, LivingCap<?> entityCap, float convertTimeModifier)
	{
		Pose lastPose = entityCap.getAnimator().getPose(1.0F);
		this.animationPlayer.getPlay().onFinish(entityCap, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.onStart(entityCap);

		if (!nextAnimation.isMetaAnimation())
		{
			this.setLinkAnimation(nextAnimation, entityCap, lastPose, convertTimeModifier);
			this.linkAnimationStorage.putOnPlayer(this.animationPlayer);
			this.nextAnimation = nextAnimation;
		}
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
		if (this.paused)
		{
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}

		if (this.animationPlayer.isEmpty())
		{
			return;
		}

		this.animationPlayer.update(entityCap);
		this.animationPlayer.getPlay().onUpdate(entityCap);

		if (this.animationPlayer.isEnd())
		{
			if (this.nextAnimation != null)
			{
				this.animationPlayer.getPlay().onFinish(entityCap, true);

				if (!(this.animationPlayer.getPlay() instanceof LinkAnimation)
						&& !(this.nextAnimation instanceof LinkAnimation))
				{
					this.nextAnimation.onStart(entityCap);
				}

				this.nextAnimation.putOnPlayer(this.animationPlayer);
				this.nextAnimation = null;
			} else
			{
				if (this.animationPlayer.getPlay() instanceof LayerOffAnimation)
				{
					this.animationPlayer.getPlay().onFinish(entityCap, true);
				} else
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
		protected Map<AnimationLayer.Priority, AnimationLayer> compositeLayers = new HashMap<>();
		protected AnimationLayer.Priority baserLayerPriority;

		public BaseLayer(Priority priority)
		{
			super(priority);
			this.compositeLayers.computeIfAbsent(Priority.HIGHEST, AnimationLayer::new);
			this.compositeLayers.computeIfAbsent(Priority.MIDDLE, AnimationLayer::new);
			this.compositeLayers.put(Priority.LOWEST, this);
			this.baserLayerPriority = Priority.LOWEST;
		}

		@Override
		public void playAnimation(StaticAnimation nextAnimation, LivingCap<?> entityCap, float convertTimeModifier)
		{
			Priority priority = nextAnimation.getPriority();
			this.baserLayerPriority = priority;
			this.offCompositeLayerLowerThan(entityCap, priority);
			super.playAnimation(nextAnimation, entityCap, convertTimeModifier);
		}

		@Override
		public void update(LivingCap<?> entityCap)
		{
			super.update(entityCap);

			for (AnimationLayer layer : this.compositeLayers.values())
			{
				if (layer != this)
				{
					layer.update(entityCap);
				}
			}
		}

		public void offCompositeLayerLowerThan(LivingCap<?> entityCap, Priority priority)
		{
			for (Priority p : priority.notUpperThan())
			{
				this.compositeLayers.get(p).off(entityCap);
			}
		}

		public void disableLayer(Priority priority)
		{
			AnimationLayer layer = this.compositeLayers.get(priority);
			layer.disabled = true;
			Animations.DUMMY_ANIMATION.putOnPlayer(layer.animationPlayer);
		}

		@Override
		public void off(LivingCap<?> entityCap)
		{

		}

		@Override
		protected boolean isDisabled()
		{
			return false;
		}
	}

	public static enum LayerType
	{
		BASE_LAYER, COMPOSITE_LAYER;
	}

	public static enum Priority
	{
		LOWEST, MIDDLE, HIGHEST;

		public Priority[] lowers()
		{
			return Arrays.copyOfRange(Priority.values(), 0, this.ordinal());
		}

		public Priority[] uppers()
		{
			return Arrays.copyOfRange(Priority.values(), this.ordinal() + 1, 3);
		}

		public Priority[] notUpperThan()
		{
			return Arrays.copyOfRange(Priority.values(), 0, this.ordinal() + 1);
		}
	}
}
