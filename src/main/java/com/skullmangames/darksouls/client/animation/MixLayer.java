package com.skullmangames.darksouls.client.animation;

import java.util.Map;

import com.skullmangames.darksouls.common.animation.JointTransform;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.MixLinkAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MixLayer extends BaseLayer
{
	protected String[] maskedJointNames;
	protected boolean linkEndPhase;
	protected MixLinkAnimation mixLinkAnimation;
	
	public MixLayer(DynamicAnimation animation)
	{
		super(animation);
		this.linkEndPhase = false;
		this.maskedJointNames = new String[0];
		this.mixLinkAnimation = new MixLinkAnimation(this);
	}
	
	public boolean isActive()
	{
		return !this.animationPlayer.isEmpty();
	}
	
	@Override
	public void update(LivingCap<?> entitydata)
	{
		if (pause || this.animationPlayer.isEmpty())
		{
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}
		
		float frameTime = IngameConfig.A_TICK * this.animationPlayer.getPlay().getPlaySpeed(entitydata);
		
		DynamicAnimation animation = this.animationPlayer.getPlay();
		if (animation.shouldSynchronize())
		{
			this.animationPlayer.synchronizeTime(entitydata.getClientAnimator().getPlayer());
		}
		else
		{
			if (this.nextPlaying != null && this.nextPlaying.shouldSynchronize())
			{
				Map<String, JointTransform> transforms = this.nextPlaying.getPoseByTime(entitydata, entitydata.getClientAnimator().getPlayer().getElapsedTime()).getJointTransformData();
				animation.getTransfroms().forEach((name, sheet) ->
				{
					sheet.setEndKeyFrame(transforms.get(name));
				});
			}
			
			this.animationPlayer.update(frameTime);
			animation.onUpdate(entitydata);
		}
		
		if (this.animationPlayer.isEnd())
		{
			if (nextPlaying != null)
			{
				float exceedTime = this.animationPlayer.getExceedTime();
				animation.onFinish(entitydata, true);
				this.nextPlaying.putOnPlayer(this.animationPlayer);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime);
				if (animation.shouldSynchronize())
				{
					this.animationPlayer.synchronizeTime(entitydata.getClientAnimator().getPlayer());
				}
				this.nextPlaying = null;
			}
		}
	}
	
	public void setMixLinkAnimation(LivingCap<?> entitydata, float timeModifier)
	{
		AnimationDataExtractor.getMixLinkAnimation(timeModifier + entitydata.getClientAnimator().baseLayer.animationPlayer.getPlay().getConvertTime(),
				this.animationPlayer.getCurrentPose(entitydata, Minecraft.getInstance().getFrameTime()), this.mixLinkAnimation);
	}
	
	public void setJointMask(String... maskedJoint)
	{
		this.maskedJointNames = maskedJoint;
	}
	
	public boolean jointMasked(String s)
	{
		for(String str : this.maskedJointNames)
		{
			if(s.equals(str))
				return true;
		}
		return false;
	}
}