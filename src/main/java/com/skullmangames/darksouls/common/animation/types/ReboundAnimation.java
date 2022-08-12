package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Models;

public class ReboundAnimation extends AimingAnimation
{
	public ReboundAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, Function<Models<?>, Model> model)
	{
		super(convertTime, repeatPlay, path1, path2, path3, model);
	}

	public ReboundAnimation(boolean repeatPlay, String path1, String path2, String path3, Function<Models<?>, Model> model)
	{
		this(IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, model);
	}

	@Override
	public EntityState getState(float time)
	{
		return EntityState.POST_CONTACT;
	}

	@Override
	public boolean isReboundAnimation()
	{
		return true;
	}
}