package com.skullmangames.darksouls.core.util;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class IndirectDamageSourceExtended extends IndirectEntityDamageSource implements ExtendedDamageSource
{
	private float amount;
	private final float staminaDamage;
	private final float poiseDamage;
	private boolean headshot;
	private StunType stunType;
	private final DamageType damageType;

	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity owner, float amount, StunType stunType, DamageType damageType, float poiseDamage, float staminaDamage)
	{
		super(damageTypeIn, source, owner);
		this.stunType = stunType;
		this.damageType = damageType;
		this.poiseDamage = poiseDamage;
		this.amount = amount;
		this.staminaDamage = staminaDamage;
	}

	@Override
	public int getRequiredDeflectionLevel()
	{
		return 0;
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
		return this.getEntity();
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
		return this.headshot;
	}

	@Override
	public void setHeadshot(boolean value)
	{
		this.headshot = value;
	}

	@Override
	public float getStaminaDamage()
	{
		return this.staminaDamage;
	}

	@Override
	public void setStunType(StunType value)
	{
		this.stunType = value;
	}

	@Override
	public Entity getSource()
	{
		return this.getDirectEntity();
	}
}