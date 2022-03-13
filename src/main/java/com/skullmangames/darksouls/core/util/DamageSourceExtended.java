package com.skullmangames.darksouls.core.util;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class DamageSourceExtended extends EntityDamageSource implements IExtendedDamageSource
{
	private float amount;
	private final int requiredDeflectionLevel;
	private final float poiseDamage;
	private final float staminaDamage;
	private final StunType stunType;
	private final DamageType damageType;
	
	public DamageSourceExtended(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage, float staminaDamage)
	{
		super(damageTypeIn, damageSourceEntityIn);
		
		this.stunType = stunType;
		this.amount = amount;
		this.damageType = damageType;
		this.poiseDamage = poiseDamage;
		this.requiredDeflectionLevel = requireddeflectionlevel;
		this.staminaDamage = staminaDamage;
	}
	
	@Override
	public int getRequiredDeflectionLevel()
	{
		return this.requiredDeflectionLevel;
	}
	
	@Override
	public float getAmount()
	{
		return this.amount;
	}
	
	@Override
	public void setAmount(float amount)
	{
		this.amount = amount;
	}

	@Override
	public StunType getStunType()
	{
		return this.stunType;
	}

	@Override
	public Entity getOwner()
	{
		return super.getDirectEntity();
	}

	@Override
	public String getType()
	{
		return super.getMsgId();
	}

	@Override
	public DamageType getDamageType()
	{
		return this.damageType;
	}

	@Override
	public float getPoiseDamage()
	{
		return this.poiseDamage;
	}

	@Override
	public boolean isHeadshot()
	{
		return false;
	}

	@Override
	public void setHeadshot(boolean value) {}

	@Override
	public float getStaminaDamage()
	{
		return this.staminaDamage;
	}
}