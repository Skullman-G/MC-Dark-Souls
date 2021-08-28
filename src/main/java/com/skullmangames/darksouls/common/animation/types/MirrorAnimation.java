package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation mirrorAnimation;
	
	public MirrorAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2)
	{
		super(id, convertTime, repeatPlay, path1);
		this.mirrorAnimation = new StaticAnimation(convertTime, repeatPlay, path2);
	}
	
	public StaticAnimation checkHandAndReturnAnimation(Hand hand)
	{
		switch(hand)
		{
			case MAIN_HAND:
				return this;
			case OFF_HAND:
				return mirrorAnimation;
		}
		
		return null;
	}
	
	@Override
	public StaticAnimation bindFull(Armature armature)
	{
		if(mirrorAnimation.animationDataPath != null)
		{
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, mirrorAnimation.animationDataPath), mirrorAnimation, armature);
			mirrorAnimation.animationDataPath = null;
		}
		
		return super.bindFull(armature);
	}
}