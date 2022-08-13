package com.skullmangames.darksouls.core.util;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class DamageSourceExtended extends EntityDamageSource implements ExtendedDamageSource
{
	private final int requiredDeflectionLevel;
	private final float poiseDamage;
	private final float staminaDamage;
	private StunType stunType;
	private final Damage[] damages;
	
	public DamageSourceExtended(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, int requireddeflectionlevel, float poiseDamage, float staminaDamage, Damage... damages)
	{
		super(damageTypeIn, damageSourceEntityIn);
		
		this.stunType = stunType;
		this.damages = damages;
		this.poiseDamage = poiseDamage;
		this.requiredDeflectionLevel = requireddeflectionlevel;
		this.staminaDamage = staminaDamage;
	}
	
	@Override
	public float getAmount()
	{
		float amount = 0;
		for (Damage damage : this.getDamages()) amount += damage.getAmount();
		return amount;
	}
	
	@Override
	public int getRequiredDeflectionLevel()
	{
		return this.requiredDeflectionLevel;
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
	public Damage[] getDamages()
	{
		return this.damages;
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