package com.skullmangames.darksouls.common.animation.types;

import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.capability.entity.IEquipLoaded;
import com.skullmangames.darksouls.common.capability.entity.LivingData;

import net.minecraftforge.api.distmarker.Dist;

public class DodgingAnimation extends ActionAnimation
{
	protected float encumbrance = 0.0F;
	protected float recovery;
	
	public DodgingAnimation(float convertTime, boolean affectVelocity, String path, String armature)
	{
		this(convertTime, 0.0F, 0.0F, affectVelocity, path, armature);
	}
	
	public DodgingAnimation(float convertTime, float delayTime, float recovery, boolean affectVelocity, String path, String armature)
	{
		super(convertTime, delayTime, affectVelocity, path, armature);
		this.recovery = recovery;
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
		return 1.3F - this.encumbrance / 2;
	}
	
	@Override
	protected Vector3f getCoordVector(LivingData<?> entitydata)
	{
		Vector3f vec = super.getCoordVector(entitydata);
		vec.mul(1.5F);
		return vec;
	}
	
	@Override
	public LivingData.EntityState getState(float time)
	{
		if(time < this.delayTime)
		{
			return LivingData.EntityState.PRE_DELAY;
		}
		else if (time < this.recovery)
		{
			return LivingData.EntityState.INVINCIBLE;
		}
		else
		{
			return LivingData.EntityState.FREE;
		}
	}
	
	@Override
	public void bind(Dist dist)
	{
		super.bind(dist);
		if (this.clientOnly && dist != Dist.CLIENT) return;
		if(this.recovery > 0.0F || this.recovery > this.delayTime) return;
		this.recovery = this.totalTime;
	}
}