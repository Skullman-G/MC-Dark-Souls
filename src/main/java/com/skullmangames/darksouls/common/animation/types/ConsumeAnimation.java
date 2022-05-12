package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.capability.entity.EntityState;

public class ConsumeAnimation extends MirrorAnimation
{
	private LivingCap<?> entityCap;
	
	public ConsumeAnimation(float convertTime, boolean repeatPlay, String path1, String path2, Function<Models<?>, Model> model)
	{
		super(convertTime, repeatPlay, path1, path2, model);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, model)
		{
			private LivingCap<?> entityCap;
			
			@Override
			public void onStart(LivingCap<?> entityCap)
			{
				this.entityCap = entityCap;
				entityCap.getOriginalEntity().xRot = 0.0F;
				entityCap.getOriginalEntity().xRotO = 0.0F;
				float x = entityCap.getOriginalEntity().yRot;
				entityCap.rotateTo(x, 180.0F, true);
				
				super.onStart(entityCap);
			}
			
			@Override
			public EntityState getState(float time)
			{
				if (this.entityCap.getOriginalEntity().getUseItemRemainingTicks() > 0) return EntityState.FREE_CAMERA;
				else return super.getState(time);
			}
		};
	}
	
	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		this.entityCap = entityCap;
		entityCap.getOriginalEntity().xRot = 0.0F;
		entityCap.getOriginalEntity().xRotO = 0.0F;
		float x = entityCap.getOriginalEntity().yRot;
		entityCap.rotateTo(x, 180.0F, true);
		
		super.onStart(entityCap);
	}
	
	@Override
	public EntityState getState(float time)
	{
		if (this.entityCap.getOriginalEntity().getUseItemRemainingTicks() > 0) return EntityState.FREE_CAMERA;
		else return super.getState(time);
	}
}
