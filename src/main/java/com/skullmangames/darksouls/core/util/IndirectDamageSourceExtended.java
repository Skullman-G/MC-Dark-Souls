package com.skullmangames.darksouls.core.util;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class IndirectDamageSourceExtended extends IndirectEntityDamageSource implements ExtendedDamageSource
{
	private final float staminaDamage;
	private final float poiseDamage;
	private boolean headshot;
	private StunType stunType;
	private final Damage[] damages;

	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity owner, StunType stunType, float poiseDamage, float staminaDamage, Damage... damages)
	{
		super(damageTypeIn, source, owner);
		this.stunType = stunType;
		this.damages = damages;
		this.poiseDamage = poiseDamage;
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
		return 0;
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