package com.skullmangames.darksouls.core.util;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class IndirectDamageSourceExtended extends IndirectEntityDamageSource implements IExtendedDamageSource
{
	private StunType stunType;
	private float amount;
	private DamageType damageType;

	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity owner, StunType stunType, DamageType damageType)
	{
		super(damageTypeIn, source, owner);
		this.stunType = stunType;
		this.damageType = damageType;
	}

	@Override
	public int getRequiredDeflectionLevel()
	{
		return 4;
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
	public void setStunType(StunType stunType)
	{
		this.stunType = stunType;
	}

	@Override
	public StunType getStunType()
	{
		return stunType;
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
	public int getSkillId()
	{
		return -1;
	}

	@Override
	public DamageType getAttackType()
	{
		return this.damageType;
	}

	@Override
	public void setAttackType(DamageType damageType)
	{
		this.damageType = damageType;
	}
}