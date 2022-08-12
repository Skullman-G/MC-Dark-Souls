package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;

public interface ExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(PlayerEntity player, StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage, float staminaDamage)
	{
        return new DamageSourceExtended("player", player, stunType, amount, requireddeflectionlevel, damageType, poiseDamage, staminaDamage);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage, float staminaDamage)
	{
        return new DamageSourceExtended("mob", mob, stunType, amount, requireddeflectionlevel, damageType, poiseDamage, staminaDamage);
    }
	
	public static DamageSourceExtended getFrom(ExtendedDamageSource org)
	{
		return new DamageSourceExtended(org.getType(), org.getOwner(), org.getStunType(), org.getAmount(), org.getRequiredDeflectionLevel(), org.getDamageType(), org.getPoiseDamage(), org.getStaminaDamage());
	}
	
	public static DamageSourceExtended getFrom(DamageSource org, float amount)
	{
		int reqDeflection = amount > 5.0F ? Deflection.MEDIUM.getLevel() : amount > 10.0F ? Deflection.HEAVY.getLevel() : Deflection.LIGHT.getLevel();
		StunType stunType = amount > 10.0F ? StunType.SMASH_BACK : StunType.DEFAULT;
		float poiseDamage = 1.0F;
		float staminaDamage = 1.0F;
		LivingCap<?> cap = org.getDirectEntity() instanceof LivingEntity ? (LivingCap<?>)org.getDirectEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null) : null;
		if (cap != null)
		{
			MeleeWeaponCap weapon = cap.getHeldWeaponCapability(Hand.MAIN_HAND);
			if (weapon != null)
			{
				poiseDamage = weapon.poiseDamage;
				staminaDamage = weapon.getStaminaDamage();
			}
			
		}
		return new DamageSourceExtended(org.getMsgId(), org.getDirectEntity(), stunType, amount, reqDeflection, DamageType.REGULAR, poiseDamage, staminaDamage);
	}
	
	public static IndirectDamageSourceExtended getIndirectFrom(ExtendedDamageSource org)
	{
		return new IndirectDamageSourceExtended(org.getType(), org.getSource(), org.getOwner(), org.getAmount(), org.getStunType(), org.getDamageType(), org.getPoiseDamage(), org.getStaminaDamage());
	}
	
	public static IndirectDamageSourceExtended getIndirectFrom(IndirectEntityDamageSource org, float amount)
	{
		return new IndirectDamageSourceExtended(org.getMsgId(), org.getDirectEntity(), org.getEntity(), amount, StunType.DEFAULT, DamageType.REGULAR, 1.0F, 0.0F);
	}
	
	public float getAmount();
	public void setAmount(float amount);
	public StunType getStunType();
	public Entity getOwner();
	public Entity getSource();
	public String getType();
	public int getRequiredDeflectionLevel();
	public DamageType getDamageType();
	public float getPoiseDamage();
	public boolean isHeadshot();
	public void setHeadshot(boolean value);
	public float getStaminaDamage();
	public void setStunType(StunType value);
	
	public enum StunType
	{
		NONE(0), DEFAULT(1), DISARMED(1), SMASH_FRONT(2), SMASH_BACK(2);
		
		private final int level;
		
		StunType(int level)
		{
			this.level = level;
		}
		
		public int getLevel()
		{
			return this.level;
		}
		
		public StunType downgrade()
		{
			if (this.level == 2) return DEFAULT;
			else return NONE;
		}
	}
	
	public enum DamageType
	{
		REGULAR, STRIKE, SLASH, THRUST;
		
		public Attribute getDefenseAttribute()
		{
			switch (this)
			{
				default: return ModAttributes.STANDARD_DEFENSE.get();
				case STRIKE: return ModAttributes.STRIKE_DEFENSE.get();
				case SLASH: return ModAttributes.SLASH_DEFENSE.get();
				case THRUST: return ModAttributes.THRUST_DEFENSE.get();
			}
		}
	}
}