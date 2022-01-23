package com.skullmangames.darksouls.common.capability.projectile;

import net.minecraft.world.entity.projectile.Projectile;

public class CapabilityProjectile<T extends Projectile>
{
	private float impact;
	private float armorNegation;
	
	public void onJoinWorld(T projectileEntity)
	{
		this.armorNegation = 0.0F;
		this.impact = 0.0F;
	}
	
	public float getArmorNegation()
	{
		return this.armorNegation;
	}
	
	public float getImpact()
	{
		return this.impact;
	}
}