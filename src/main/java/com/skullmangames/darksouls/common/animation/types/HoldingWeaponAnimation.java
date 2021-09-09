package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.MixPart;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.parser.xml.collada.AnimationDataExtractor;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

public class HoldingWeaponAnimation extends StaticAnimation
{
	public final StaticAnimation offHandAnimation;
	public final StaticAnimation bothHandAnimation;
	
	public HoldingWeaponAnimation(int id, float convertTime, boolean repeatPlay, String mainhandAnim, String offhandAnim, String bothhandAnim, String armature, boolean clientOnly)
	{
		super(id, convertTime, repeatPlay, mainhandAnim, armature, clientOnly, MixPart.RIGHT);
		this.offHandAnimation = new StaticAnimation(convertTime, repeatPlay, offhandAnim, armature, clientOnly, MixPart.LEFT);
		this.bothHandAnimation = new StaticAnimation(convertTime, repeatPlay, bothhandAnim, armature, clientOnly, MixPart.FULL);
	}
	
	// 0 = main, 1 = off, 2 = both
	public StaticAnimation getAnimation(int hand)
	{
		switch(hand)
		{
			default:
			case 0:
				return this;
				
			case 1:
				return this.offHandAnimation;
				
			case 2:
				return this.bothHandAnimation;
		}
	}
	
	@Override
	public void bind(Dist dist)
	{
		if (this.clientOnly && dist != Dist.CLIENT) return;
		
		if(this.offHandAnimation.animationDataPath != null)
		{
			Models<?> modeldata = dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
			Armature armature = modeldata.findArmature(this.armature);
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, this.offHandAnimation.animationDataPath), this.offHandAnimation, armature);
			this.offHandAnimation.animationDataPath = null;
		}
		if(this.bothHandAnimation.animationDataPath != null)
		{
			Models<?> modeldata = dist == Dist.CLIENT ? ClientModels.CLIENT : Models.SERVER;
			Armature armature = modeldata.findArmature(this.armature);
			AnimationDataExtractor.extractAnimation(new ResourceLocation(DarkSouls.MOD_ID, this.bothHandAnimation.animationDataPath), this.bothHandAnimation, armature);
			this.offHandAnimation.animationDataPath = null;
		}
		
		super.bind(dist);
		
		return;
	}
}
