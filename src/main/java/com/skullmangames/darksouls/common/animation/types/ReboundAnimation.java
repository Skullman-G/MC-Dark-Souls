package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;

public class ReboundAnimation extends AimingAnimation
{
	public ReboundAnimation(ResourceLocation id, float convertTime, boolean repeatPlay,
			ResourceLocation path1, ResourceLocation path2, ResourceLocation path3, Function<Models<?>, Model> model)
	{
		super(id, convertTime, repeatPlay, path1, path2, path3, model);
	}

	public ReboundAnimation(ResourceLocation id, boolean repeatPlay,
			ResourceLocation path1, ResourceLocation path2, ResourceLocation path3, Function<Models<?>, Model> model)
	{
		this(id, IngameConfig.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, model);
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