package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.core.init.ModAttributes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;

public interface IExtendedDamageSource
{
	public static DamageSourceExtended causePlayerDamage(Player player, StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage)
	{
        return new DamageSourceExtended("player", player, stunType, amount, requireddeflectionlevel, damageType, poiseDamage);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage)
	{
        return new DamageSourceExtended("mob", mob, stunType, amount, requireddeflectionlevel, damageType, poiseDamage);
    }
	
	public static DamageSourceExtended getFrom(IExtendedDamageSource original)
	{
		return new DamageSourceExtended(original.getType(), original.getOwner(), original.getStunType(), original.getAmount(), original.getRequiredDeflectionLevel(), original.getDamageType(), original.getPoiseDamage());
	}
	
	public float getAmount();
	public void setAmount(float amount);
	public StunType getStunType();
	public Entity getOwner();
	public String getType();
	public int getRequiredDeflectionLevel();
	public DamageType getDamageType();
	public float getPoiseDamage();
	public boolean isHeadshot();
	public void setHeadshot(boolean value);
	
	public enum StunType
	{
		NONE(0), DEFAULT(1), SMASH_FRONT(2), SMASH_BACK(2);
		
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