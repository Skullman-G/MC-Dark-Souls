package com.skullmangames.darksouls.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class BellGargoyle extends PathfinderMob
{
	public BellGargoyle(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	public boolean canSpawnSprintParticle()
	{
		return false;
	}
	
	@Override
	protected int calculateFallDamage(float distance, float p_225508_2_)
	{
		if (distance <= 30.0F) return 0;
		return super.calculateFallDamage(distance, p_225508_2_);
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	public boolean isPushable()
	{
		return false;
	}
	
	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 825D)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
				.add(Attributes.ATTACK_SPEED, 1.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
	
	@Override
	public boolean removeWhenFarAway(double distance)
	{
		return false;
	}
}
