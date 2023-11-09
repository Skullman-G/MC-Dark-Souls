package com.skullmangames.darksouls.common.capability.item;

import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import net.minecraft.sounds.SoundEvent;

public interface Shield
{
	float getDefense(CoreDamageType damageType);
	SoundEvent getBlockSound();
	float getStability();
	Deflection getDeflection();
	
	default boolean canDeflect(Deflection other)
	{
		return this.getDeflection().canDeflect(other);
	}
	
	
	enum Deflection
	{
		NONE(0), LIGHT(1), MEDIUM(2), HEAVY(3), IMPOSSIBLE(4);
		
		private final int level;
		
		private Deflection(int level)
		{
			this.level = level;
		}
		
		public boolean canDeflect(Deflection other)
		{
			return this.level > other.level;
		}
	}
}
