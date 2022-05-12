package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;

public class MirrorAnimation extends StaticAnimation
{
	public StaticAnimation mirror;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, Function<Models<?>, Model> model)
	{
		super(convertTime, repeatPlay, path1, model);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, model, true);
	}
	
	public StaticAnimation getAnimation(InteractionHand hand)
	{
		switch(hand)
		{
			case MAIN_HAND:
				return this;
			case OFF_HAND:
				return mirror;
		}
		
		return null;
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager, Models<?> models)
	{
		load(resourceManager, models, this);
		load(resourceManager, models, this.mirror);
	}
}