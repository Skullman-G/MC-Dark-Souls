package com.skullmangames.darksouls.common.animation.types.attack;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.property.Property.AnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.Entity;

public class AADashAnimation extends AttackAnimation
{
	public AADashAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path, String armature)
	{
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, armature);
		this.addProperty(AnimationProperty.DIRECTIONAL, true);
	}
	
	public AADashAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path, boolean noDirectionAttack, String armature)
	{
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, armature);
	}
	
	@Override
	public IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target, Phase phase, float amount)
	{
		IExtendedDamageSource extSource = super.getDamageSourceExt(entitydata, target, phase, amount);
		extSource.setImpact(extSource.getImpact() * 1.4F);
		
		return extSource;
	}
}