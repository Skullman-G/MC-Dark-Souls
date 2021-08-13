package com.skullmangames.darksouls.common.animation.types.attack;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.property.Property.AnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.Entity;

public class AADashAnimation extends AttackAnimation
{
	public AADashAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path)
	{
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
		this.addProperty(AnimationProperty.DIRECTIONAL, true);
	}
	
	public AADashAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path, boolean noDirectionAttack)
	{
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
	}
	
	@Override
	public IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target, Phase phase)
	{
		IExtendedDamageSource extSource = super.getDamageSourceExt(entitydata, target, phase);
		extSource.setImpact(extSource.getImpact() * 1.4F);
		
		return extSource;
	}
}