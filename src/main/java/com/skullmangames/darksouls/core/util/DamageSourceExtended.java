package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class DamageSourceExtended extends EntityDamageSource implements IExtendedDamageSource
{
	private float impact;
	private StunType stunType;
	private final int id;
	private float amount;
	private int requiredDeflectionLevel;
	private DamageType damageType;
	
	public DamageSourceExtended(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, int id, float amount, int requireddeflectionlevel, DamageType damageType)
	{
		super(damageTypeIn, damageSourceEntityIn);
		
		LivingData<?> entityCap = (LivingData<?>) damageSourceEntityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		this.stunType = stunType;
		this.impact = entityCap.getImpact();
		this.id = id;
		this.amount = amount;
		this.damageType = damageType;
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
	public void setImpact(float amount)
	{
		this.impact = amount;
	}

	@Override
	public void setStunType(StunType stunType)
	{
		this.stunType = stunType;
	}

	@Override
	public float getImpact()
	{
		return impact;
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
		return this.id;
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