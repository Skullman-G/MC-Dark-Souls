package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class DamageSourceExtended extends EntityDamageSource implements IExtendedDamageSource
{
	private float impact;
	private float armorNegation;
	private StunType stunType;
	private DamageType damageType;
	private final int id;
	private float amount;
	private int requiredDeflectionLevel;
	
	public DamageSourceExtended(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, DamageType damageType, int id, float amount, int requireddeflectionlevel)
	{
		super(damageTypeIn, damageSourceEntityIn);
		
		LivingData<?> entityCap = (LivingData<?>) damageSourceEntityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		this.stunType = stunType;
		this.damageType = damageType;
		this.impact = entityCap.getImpact();
		this.armorNegation = entityCap.getArmorNegation();
		this.id = id;
		this.amount = amount;
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
	public void setImpact(float amount)
	{
		this.impact = amount;
	}

	@Override
	public void setArmorNegation(float amount)
	{
		this.armorNegation = amount;
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
	public float getArmorNegation()
	{
		return armorNegation;
	}

	@Override
	public StunType getStunType()
	{
		return stunType;
	}

	@Override
	public DamageType getExtDamageType()
	{
		return damageType;
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
}