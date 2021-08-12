package com.skullmangames.darksouls.common.animation.property;

import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.ValueCorrector;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

public abstract class Property<T>
{
	public static class AnimationProperty<T> extends Property<T>
	{
		public static final AnimationProperty<Boolean> LOCK_ROTATION = new AnimationProperty<Boolean> ();
		public static final AnimationProperty<Boolean> DIRECTIONAL = new AnimationProperty<Boolean> ();
		
	}
	
	public static class DamageProperty<T> extends Property<T>
	{
		public static final DamageProperty<ValueCorrector> MAX_STRIKES = new DamageProperty<ValueCorrector> ();
		public static final DamageProperty<ValueCorrector> DAMAGE = new DamageProperty<ValueCorrector> ();
		public static final DamageProperty<ValueCorrector> ARMOR_NEGATION = new DamageProperty<ValueCorrector> ();
		public static final DamageProperty<ValueCorrector> IMPACT = new DamageProperty<ValueCorrector> ();
		public static final DamageProperty<DamageType> DAMAGE_TYPE = new DamageProperty<DamageType> ();
		public static final DamageProperty<StunType> STUN_TYPE = new DamageProperty<StunType> ();
		public static final DamageProperty<SoundEvent> SWING_SOUND = new DamageProperty<SoundEvent> ();
		public static final DamageProperty<SoundEvent> HIT_SOUND = new DamageProperty<SoundEvent> ();
		public static final DamageProperty<RegistryObject<HitParticleType>> PARTICLE = new DamageProperty<RegistryObject<HitParticleType>> ();
	}
}
