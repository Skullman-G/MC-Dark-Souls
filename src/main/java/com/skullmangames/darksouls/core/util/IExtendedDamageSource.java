package com.skullmangames.darksouls.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface IExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(PlayerEntity player, StunType stunType, DamageType damageType, int id, float amount, int requireddeflectionlevel)
	{
        return new DamageSourceExtended("player", player, stunType, damageType, id, amount, requireddeflectionlevel);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, StunType stunType, DamageType damageType, int id, float amount, int requireddeflectionlevel)
	{
        return new DamageSourceExtended("mob", mob, stunType, damageType, id, amount, requireddeflectionlevel);
    }
	
	public static DamageSourceExtended getFrom(IExtendedDamageSource original)
	{
		return new DamageSourceExtended(original.getType(), original.getOwner(), original.getStunType(), original.getExtDamageType(), original.getSkillId(), original.getAmount(), original.getRequiredDeflectionLevel());
	}
	
	public void setImpact(float amount);
	public void setArmorNegation(float amount);
	public void setStunType(StunType stunType);
	public float getImpact();
	public float getAmount();
	public float getArmorNegation();
	public int getSkillId();
	public StunType getStunType();
	public DamageType getExtDamageType();
	public Entity getOwner();
	public String getType();
	public int getRequiredDeflectionLevel();
	
	public static enum StunType
	{
		SHORT, LONG, HOLD
	}
	
	public static enum DamageType
	{
		PHYSICAL, MAGIC
	}
}