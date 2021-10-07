package com.skullmangames.darksouls.client.animation;

import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Pose;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.LinkAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.config.IngameConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseLayer
{
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	protected LinkAnimation linkAnimation = new LinkAnimation();
	
	public boolean pause;
	
	public BaseLayer(DynamicAnimation animation)
	{
		this.animationPlayer = new AnimationPlayer(animation);
		this.nextPlaying = new StaticAnimation();
	}

	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float modifyTime)
	{
		if (!this.animationPlayer.isEmpty()) this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		setLinkAnimation(nextAnimation, entitydata, modifyTime);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = nextAnimation;
	}
	
	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata)
	{
		if (!this.animationPlayer.isEmpty()) this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = null;
	}
	
	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float timeModifier)
	{
		if (this.animationPlayer.isEmpty()) return;
		Pose currentPose = this.animationPlayer.getCurrentPose(entitydata, Minecraft.getInstance().getFrameTime());
		nextAnimation.getLinkAnimation(currentPose, timeModifier, entitydata, this.linkAnimation);
	}
	
	public void update(LivingData<?> entitydata)
	{
		if (pause || this.animationPlayer.isEmpty())
		{
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}
		
		float frameTime = IngameConfig.A_TICK * this.animationPlayer.getPlay().getPlaySpeed(entitydata);
		
		DynamicAnimation animation = this.animationPlayer.getPlay();
		this.animationPlayer.update(frameTime);
		animation.onUpdate(entitydata);
		
		if (this.animationPlayer.isEnd())
		{
			if (nextPlaying != null)
			{
				float exceedTime = this.animationPlayer.getExceedTime();
				animation.onFinish(entitydata, true);
				this.nextPlaying.putOnPlayer(this.animationPlayer);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime);
				this.nextPlaying = null;
			}
		}
	}
	
	public void clear(LivingData<?> entitydata)
	{
		DynamicAnimation play = this.animationPlayer.getPlay();
		if (play != null)
		{
			play.onFinish(entitydata, animationPlayer.isEnd());
		}

		if (nextPlaying != null)
		{
			nextPlaying.onFinish(entitydata, false);
		}
	}
}