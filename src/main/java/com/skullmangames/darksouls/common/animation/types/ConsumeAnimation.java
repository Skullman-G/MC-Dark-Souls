package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap.EntityState;

public class ConsumeAnimation extends MirrorAnimation
{
	private LivingCap<?> entitydata;
	
	public ConsumeAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String armature, boolean clientOnly)
	{
		super(convertTime, repeatPlay, path1, path2, armature, clientOnly);
		this.mirrorAnimation = new StaticAnimation(false, convertTime, repeatPlay, path2, armature, clientOnly)
		{
			private LivingCap<?> entitydata;
			
			@Override
			public void onActivate(LivingCap<?> entitydata)
			{
				this.entitydata = entitydata;
				entitydata.getOriginalEntity().xRot = 0.0F;
				entitydata.getOriginalEntity().xRotO = 0.0F;
				float x = entitydata.getOriginalEntity().yRot;
				entitydata.rotateTo(x, 180.0F, true);
				
				super.onActivate(entitydata);
			}
			
			@Override
			public EntityState getState(float time)
			{
				if (this.entitydata.getOriginalEntity().getUseItemRemainingTicks() > 0) return EntityState.FREE_CAMERA;
				else return super.getState(time);
			}
		};
	}
	
	@Override
	public void onActivate(LivingCap<?> entitydata)
	{
		this.entitydata = entitydata;
		entitydata.getOriginalEntity().xRot = 0.0F;
		entitydata.getOriginalEntity().xRotO = 0.0F;
		float x = entitydata.getOriginalEntity().yRot;
		entitydata.rotateTo(x, 180.0F, true);
		
		super.onActivate(entitydata);
	}
	
	@Override
	public EntityState getState(float time)
	{
		if (this.entitydata.getOriginalEntity().getUseItemRemainingTicks() > 0) return EntityState.FREE_CAMERA;
		else return super.getState(time);
	}
}
