package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;

import net.minecraft.sounds.SoundEvent;

public interface IShield
{
	public float getDefense(DamageType damageType);
	public ShieldType getShieldType();
	public SoundEvent getBlockSound();
	
	public default int getDeflectionLevel()
	{
		return this.getShieldType().getDeflection().getLevel();
	}
	
	
	public enum Deflection
	{
		NONE(0), LIGHT(1), MEDIUM(2), HEAVY(3), IMPOSSIBLE(4);
		
		private final int level;
		
		private Deflection(int level)
		{
			this.level = level;
		}
		
		public int getLevel()
		{
			return this.level;
		}
	}
	
	public enum ShieldType
	{
		NONE(Deflection.NONE),
		SMALL(Deflection.LIGHT),
		NORMAL(Deflection.MEDIUM),
		GREAT(Deflection.HEAVY),
		UNIQUE(Deflection.LIGHT),
		CRACKED_ROUND_SHIELD(Deflection.NONE),
		IRON_ROUND_SHIELD(Deflection.HEAVY);
		
		
		private final Deflection deflection;
		
		private ShieldType(Deflection deflection)
		{
			this.deflection = deflection;
		}
		
		public Deflection getDeflection()
		{
			return this.deflection;
		}
	}
}
