package com.skullmangames.darksouls.common.animation.types;

import java.util.function.Function;

import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.Models;

public class DeathAnimation extends InvincibleAnimation
{
	public DeathAnimation(float convertTime, String path, Function<Models<?>, Model> model)
	{
		super(convertTime, path, model);
	}
	
	@Override
	public void onStart(LivingCap<?> entityCap)
	{
		super.onStart(entityCap);
		entityCap.getOriginalEntity().deathTime = 0;
	}
	
	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		entityCap.getOriginalEntity().deathTime = 0;
	}
	
	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		entityCap.onDeath();
		entityCap.getOriginalEntity().deathTime = 19;
	}
	
	@Override
	public <V> DeathAnimation addProperty(Property<V> propertyType, V value)
	{
		return (DeathAnimation)super.addProperty(propertyType, value);
	}
}
