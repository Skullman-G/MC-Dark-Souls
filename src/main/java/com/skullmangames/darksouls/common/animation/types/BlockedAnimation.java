package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.core.init.Models;

import net.minecraft.resources.ResourceLocation;

public class BlockedAnimation extends ActionAnimation
{
	public BlockedAnimation(ResourceLocation id, float convertTime, ResourceLocation path, Function<Models<?>, Model> model)
	{
		super(id, convertTime, path, model);
	}
	
	@Override
	public EntityState getState(float time)
	{
		return EntityState.BLOCK;
	}
}
