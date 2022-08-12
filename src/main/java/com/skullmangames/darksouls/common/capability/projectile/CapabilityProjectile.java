package com.skullmangames.darksouls.common.capability.projectile;

import net.minecraft.entity.projectile.ProjectileEntity;

public class CapabilityProjectile<T extends ProjectileEntity>
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