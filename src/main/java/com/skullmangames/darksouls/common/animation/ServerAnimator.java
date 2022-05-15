package com.skullmangames.darksouls.common.animation;

import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.LinkAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Animations;

public class ServerAnimator extends Animator
{
	public static Animator getAnimator(LivingCap<?> entityCap)
	{
		return new ServerAnimator(entityCap);
	}

	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	private LinkAnimation linkAnimation;
	public boolean pause = false;

	public ServerAnimator(LivingCap<?> entityCap)
	{
		this.entityCap = entityCap;
		this.linkAnimation = new LinkAnimation();
		this.animationPlayer = new AnimationPlayer();
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime)
	{
		this.pause = false;
		this.animationPlayer.getPlay().onFinish(this.entityCap, this.animationPlayer.isEnd());
		nextAnimation.onStart(this.entityCap);
		nextAnimation.setLinkAnimation(nextAnimation.getPoseByTime(this.entityCap, 0.0F, 0.0F), modifyTime,
				this.entityCap, this.linkAnimation);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = nextAnimation;
	}

	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation)
	{
		this.pause = false;
		this.animationPlayer.getPlay().onFinish(this.entityCap, this.animationPlayer.isEnd());
		nextAnimation.onStart(this.entityCap);
		nextAnimation.putOnPlayer(this.animationPlayer);
	}

	@Override
	public void reserveAnimation(StaticAnimation nextAnimation)
	{
		this.pause = false;
		this.nextPlaying = nextAnimation;
	}

	@Override
	public void init()
	{

	}

	@Override
	public void updatePose()
	{
		this.prevPose = this.currentPose;
		this.currentPose = this.animationPlayer.getCurrentPose(this.entityCap, 1.0F);
	}

	@Override
	public void update()
	{
		if (this.pause) return;

		this.animationPlayer.update(this.entityCap);
		this.updatePose();
		this.animationPlayer.getPlay().onUpdate(this.entityCap);

		if (this.animationPlayer.isEnd())
		{
			this.animationPlayer.getPlay().onFinish(this.entityCap, true);

			if (this.nextPlaying == null)
			{
				Animations.DUMMY_ANIMATION.putOnPlayer(this.animationPlayer);
				this.pause = true;
			} else
			{
				if (!(this.animationPlayer.getPlay() instanceof LinkAnimation)
						&& !(this.nextPlaying instanceof LinkAnimation))
				{
					this.nextPlaying.onStart(this.entityCap);
				}

				this.nextPlaying.putOnPlayer(this.animationPlayer);
				this.nextPlaying = null;
			}
		}
	}

	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation)
	{
		return this.animationPlayer;
	}

	@Override
	public EntityState getEntityState()
	{
		return this.animationPlayer.getPlay().getState(this.animationPlayer.getElapsedTime());
	}
}