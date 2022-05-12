package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Models;

public class HitAnimation extends ActionAnimation
{
	public HitAnimation(float convertTime, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, path, model);
	}

	@Override
	public EntityState getState(float time)
	{
		return EntityState.HIT;
	}
}