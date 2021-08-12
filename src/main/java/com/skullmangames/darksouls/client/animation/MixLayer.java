package com.skullmangames.darksouls.client.animation;

import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.MixLinkAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
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
		this.mixLinkAnimation = new MixLinkAnimation();
	}
	
	public void setMixLinkAnimation(LivingData<?> entitydata, float timeModifier)
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