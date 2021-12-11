package com.skullmangames.darksouls.core.util;

import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ValueCorrector;

import net.minecraft.util.SoundEvent;

public abstract class Property<T>
{
	public static class AttackProperty<T> extends Property<T>
	{
		public static final AttackProperty<ValueCorrector> IMPACT = new AttackProperty<ValueCorrector>();
		public static final AttackProperty<DamageType> DAMAGE_TYPE = new AttackProperty<DamageType>();
		public static final AttackProperty<StunType> STUN_TYPE = new AttackProperty<StunType>();
		public static final AttackProperty<Deflection> DEFLECTION = new AttackProperty<Deflection>();

		public static final AttackProperty<SoundEvent> SWING_SOUND = new AttackProperty<SoundEvent>();
		public static final AttackProperty<SoundEvent> HIT_SOUND = new AttackProperty<SoundEvent>();
	}
}
