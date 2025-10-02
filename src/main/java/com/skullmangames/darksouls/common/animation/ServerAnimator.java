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
	public boolean pause = false;

	public ServerAnimator(LivingCap<?> entityCap)
	{
		this.entityCap = entityCap;
		this.animationPlayer = new AnimationPlayer();
	}

	@Override
	public void playAnimation(StaticAnimation nextAnimation, float startAt)
	{
		this.pause = false;
		this.animationPlayer.getPlay().onUpdate(this.entityCap, this.animationPlayer);
		this.animationPlayer.getPlay().onFinish(this.entityCap, this.animationPlayer);
		nextAnimation.onStart(this.entityCap, this.animationPlayer);
		LinkAnimation linkAnim = nextAnimation.getLinkAnimation(nextAnimation.getPoseByTime(this.entityCap, 0.0F, 0.0F),
				startAt,
				this.entityCap,
				this.animationPlayer);
		this.animationPlayer.setPlayAnimation(linkAnim);
		
		this.nextPlaying = nextAnimation;
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
		this.animationPlayer.getPlay().onUpdate(this.entityCap, this.animationPlayer);

		if (this.animationPlayer.isEnd())
		{
			float exceedTime = this.animationPlayer.getExceedTime();
			this.animationPlayer.getPlay().onFinish(this.entityCap, this.animationPlayer);

			if (this.nextPlaying == null)
			{
				this.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
				this.pause = true;
			}
			else
			{
				this.animationPlayer.setPlayAnimation(this.nextPlaying);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime * 2); // Probably unfinished
				this.nextPlaying = null;
			}
		}
	}

	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation)
	{
		return this.getMainPlayer();
	}
	
	@Override
	public AnimationPlayer getMainPlayer()
	{
		return this.animationPlayer;
	}

	@Override
	public EntityState getEntityState()
	{
		return this.animationPlayer.getPlay().getState(this.animationPlayer.getElapsedTime());
	}
}