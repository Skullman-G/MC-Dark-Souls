package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.MixPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation mirrorAnimation;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String armature, boolean clientOnly)
	{
		this(convertTime, repeatPlay, path1, path2, armature, clientOnly, false, false);
	}
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String armature, boolean clientOnly, boolean mixPart, boolean sync)
	{
		super(true, convertTime, repeatPlay, path1, armature, clientOnly, mixPart ? MixPart.RIGHT : MixPart.FULL);
		this.mirrorAnimation = new StaticAnimation(false, convertTime, repeatPlay, path2, armature, clientOnly, mixPart ? MixPart.LEFT : MixPart.FULL);
		
		if (sync)
		{
			this.sync = true;
			this.mirrorAnimation.sync = true;
		}
	}
	
	public StaticAnimation getAnimation(InteractionHand hand)
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
		}
		
		super.bind(dist);
		
		return;
	}
}