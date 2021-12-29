package com.skullmangames.darksouls.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface IExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(PlayerEntity player, StunType stunType, int id, float amount, int requireddeflectionlevel, DamageType damageType)
	{
        return new DamageSourceExtended("player", player, stunType, id, amount, requireddeflectionlevel, damageType);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, StunType stunType, int id, float amount, int requireddeflectionlevel, DamageType damageType)
	{
        return new DamageSourceExtended("mob", mob, stunType, id, amount, requireddeflectionlevel, damageType);
    }
	
	public static DamageSourceExtended getFrom(IExtendedDamageSource original)
	{
		return new DamageSourceExtended(original.getType(), original.getOwner(), original.getStunType(), original.getSkillId(), original.getAmount(), original.getRequiredDeflectionLevel(), original.getAttackType());
	}
	
	public void setImpact(float amount);
	public void setStunType(StunType stunType);
	public float getImpact();
	public float getAmount();
	public void setAmount(float amount);
	public int getSkillId();
	public StunType getStunType();
	public Entity getOwner();
	public String getType();
	public int getRequiredDeflectionLevel();
	public DamageType getAttackType();
	public void setAttackType(DamageType damageType);
	
	
	public static enum StunType
	{
		SHORT, HOLD, LONG, SMASH_FRONT, SMASH_BACK
	}
	
	public enum DamageType
	{
		STANDARD, STRIKE, SLASH, THRUST
	}
}