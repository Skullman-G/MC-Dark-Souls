package com.skullmangames.darksouls.common.capability.projectile;

import net.minecraft.world.entity.projectile.Projectile;

public class CapabilityProjectile<T extends Projectile>
{
	private final float poiseDamage;
	
	public CapabilityProjectile(float poiseDamage)
	{
		this.poiseDamage = poiseDamage;
	}
	
	public float getPoiseDamage()
	{
		return this.poiseDamage;
	}
}