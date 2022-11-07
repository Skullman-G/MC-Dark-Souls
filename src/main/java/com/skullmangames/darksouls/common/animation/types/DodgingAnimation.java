package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;

public class DodgingAnimation extends ActionAnimation
{
	protected float encumbrance = 0.0F;
	private final boolean canRotate;
	
	public DodgingAnimation(float convertTime, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, false, path, model);
	}
	
	public DodgingAnimation(float convertTime, boolean canRotate, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, canRotate, 0.0F, path, model);
	}
	
	public DodgingAnimation(float convertTime, boolean canRotate, float delayTime, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, delayTime, path, model);
		this.canRotate = canRotate;
	}
	
	@Override
	public void onStart(LivingCap<?> entity)
	{
		super.onStart(entity);
		if (entity instanceof EquipLoaded)
		{
			this.encumbrance = ((EquipLoaded)entity).getEncumbrance();
		}
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		this.encumbrance = 0.0F;
	}
	
	@Override
	protected Vector3f getCoordVector(LivingCap<?> entityCap, DynamicAnimation animation)
	{
		Vector3f vec = super.getCoordVector(entityCap, animation);
		if (this.canRotate) vec.mul(1.5F - this.encumbrance);
		else vec.mul(2.0F - this.encumbrance);
		return vec;
	}
	
	@Override
	public EntityState getState(float time)
	{
		return this.canRotate ? EntityState.R_INVINCIBLE : EntityState.INVINCIBLE;
	}
}