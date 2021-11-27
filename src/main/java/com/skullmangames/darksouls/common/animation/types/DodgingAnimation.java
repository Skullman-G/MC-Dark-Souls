package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.util.Formulars;

import net.minecraft.entity.EntitySize;

public class DodgingAnimation extends ActionAnimation
{
	private final EntitySize size;
	
	public DodgingAnimation(float convertTime, boolean affectVelocity, String path, float width, float height, String armature)
	{
		this(convertTime, 0.0F, affectVelocity, path, width, height, armature);
	}
	
	public DodgingAnimation(float convertTime, float delayTime, boolean affectVelocity, String path, float width, float height, String armature)
	{
		super(convertTime, delayTime, affectVelocity, path, armature);
		
		if(width > 0.0F || height > 0.0F)
		{
			this.size = EntitySize.scalable(width, height);
		}
		else
		{
			this.size = null;
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata)
	{
		super.onUpdate(entitydata);
		if(this.size != null)
		{
			entitydata.resetSize(size);
		}
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		if(this.size != null)
		{
			entitydata.getOriginalEntity().setLocationFromBoundingbox();
		}
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return Formulars.getRollAnimationSpeedPenalty((float)entitydata.getWeight(), entitydata);
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		if(time < this.delayTime)
		{
			return LivingData.EntityState.PRE_DELAY;
		}
		else
		{
			return LivingData.EntityState.DODGE;
		}
	}
}