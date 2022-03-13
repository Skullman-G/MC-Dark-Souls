package com.skullmangames.darksouls.common.animation.types;

import com.skullmangames.darksouls.common.capability.entity.IEquipLoaded;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

public class DodgingAnimation extends ActionAnimation
{
	private float encumbrance = 0.0F;
	
	public DodgingAnimation(float convertTime, boolean affectVelocity, String path, String armature)
	{
		this(convertTime, 0.0F, affectVelocity, path, armature);
	}
	
	public DodgingAnimation(float convertTime, float delayTime, boolean affectVelocity, String path, String armature)
	{
		super(convertTime, delayTime, affectVelocity, path, armature);
	}
	
	@Override
	public void onActivate(LivingData<?> entity)
	{
		super.onActivate(entity);
		if (entity instanceof IEquipLoaded)
		{
			this.encumbrance = ((IEquipLoaded)entity).getEncumbrance();
		}
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);
		this.encumbrance = 0.0F;
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata)
	{
		return 1.0F - this.encumbrance / 2;
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
			return LivingData.EntityState.INVINCIBLE;
		}
	}
}