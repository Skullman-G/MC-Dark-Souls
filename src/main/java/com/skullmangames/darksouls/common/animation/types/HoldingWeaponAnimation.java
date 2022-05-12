package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.server.packs.resources.ResourceManager;

public class HoldingWeaponAnimation extends StaticAnimation
{
	public final StaticAnimation offHandAnimation;
	public final StaticAnimation bothHandAnimation;
	
	public HoldingWeaponAnimation(float convertTime, boolean repeatPlay, String mainhandAnim, String offhandAnim, String bothhandAnim, Function<Models<?>, Model> model)
	{
		super(convertTime, repeatPlay, mainhandAnim, model);
		this.offHandAnimation = new StaticAnimation(convertTime, repeatPlay, offhandAnim, model, true);
		this.bothHandAnimation = new StaticAnimation(convertTime, repeatPlay, bothhandAnim, model, true);
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
	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this);
		load(resourceManager, models, this.offHandAnimation);
		load(resourceManager, models, this.bothHandAnimation);
	}
}
