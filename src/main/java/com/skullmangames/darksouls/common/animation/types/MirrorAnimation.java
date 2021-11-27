package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation mirrorAnimation;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String armature, boolean clientOnly)
	{
		super(true, convertTime, repeatPlay, path1, armature, clientOnly);
		this.mirrorAnimation = new StaticAnimation(false, convertTime, repeatPlay, path2, armature, clientOnly);
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
	public void bind(Dist dist)
	{
		if (this.clientOnly && dist != Dist.CLIENT) return;
		
		if(mirrorAnimation.path != null)
		{
			Models<?> modeldata = dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
			Armature armature = modeldata.findArmature(this.armature);
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, mirrorAnimation.path), mirrorAnimation, armature);
			mirrorAnimation.path = null;
		}
		
		super.bind(dist);
		
		return;
	}
}