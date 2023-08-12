package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import net.minecraft.sounds.SoundEvent;

public interface Shield
{
	float getDefense(CoreDamageType damageType);
	ShieldType getShieldType();
	SoundEvent getBlockSound();
	float getStability();
	
	default int getDeflectionLevel()
	{
		return this.getShieldType().getDeflection().getLevel();
	}
	
	
	enum Deflection
	{
		NONE(0), LIGHT(1), MEDIUM(2), HEAVY(3), IMPOSSIBLE(4);
		
		private final int level;
		
		Deflection(int level)
		{
			this.level = level;
		}
		
		public int getLevel()
		{
			return this.level;
		}
	}
	
	enum ShieldType
	{
		NONE(Deflection.NONE),
		SMALL(Deflection.LIGHT),
		STANDARD(Deflection.MEDIUM),
		GREAT(Deflection.HEAVY),
		UNIQUE(Deflection.LIGHT),
		CRACKED_ROUND_SHIELD(Deflection.NONE),
		IRON_ROUND_SHIELD(Deflection.HEAVY);
		
		private final Deflection deflection;
		
		ShieldType(Deflection deflection)
		{
			this.deflection = deflection;
		}
		
		public Deflection getDeflection()
		{
			return this.deflection;
		}
	}
}
