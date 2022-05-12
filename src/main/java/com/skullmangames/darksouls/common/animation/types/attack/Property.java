package com.skullmangames.darksouls.common.animation.types.attack;

import com.skullmangames.darksouls.common.animation.TransformSheet;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import net.minecraft.sounds.SoundEvent;

public abstract class Property<T>
{
	public static class StaticAnimationProperty<T> extends Property<T>
	{
		public static final StaticAnimationProperty<StaticAnimation.Event[]> EVENTS = new StaticAnimationProperty<StaticAnimation.Event[]>();
	}

	public static class ActionAnimationProperty<T> extends Property<T>
	{
		public static final ActionAnimationProperty<Boolean> INTERRUPT_PREVIOUS_DELTA_MOVEMENT = new ActionAnimationProperty<Boolean>();
		public static final ActionAnimationProperty<Boolean> MOVE_VERTICAL = new ActionAnimationProperty<Boolean>();
		public static final ActionAnimationProperty<Boolean> MOVE_ON_LINK = new ActionAnimationProperty<Boolean>();
		public static final ActionAnimationProperty<ActionAnimation.ActionTime[]> ACTION_TIME = new ActionAnimationProperty<ActionAnimation.ActionTime[]>();
		public static final ActionAnimationProperty<MovementAnimationSet> MOVEMENT_ANIMATION_SETTER = new ActionAnimationProperty<MovementAnimationSet>();
		public static final ActionAnimationProperty<Boolean> AFFECT_SPEED = new ActionAnimationProperty<Boolean>();
	}
	
	@FunctionalInterface
	public static interface MovementAnimationSet
	{
		public void set(DynamicAnimation self, LivingCap<?> entityCap, TransformSheet transformSheet);
	}

	public static class AttackProperty<T> extends Property<T>
	{
		public static final AttackProperty<DamageType> DAMAGE_TYPE = new AttackProperty<DamageType>();
		public static final AttackProperty<StunType> STUN_TYPE = new AttackProperty<StunType>();
		public static final AttackProperty<Deflection> DEFLECTION = new AttackProperty<Deflection>();
		public static final AttackProperty<Integer> STAMINA_DMG_MUL = new AttackProperty<Integer>();

		public static final AttackProperty<SoundEvent> SWING_SOUND = new AttackProperty<SoundEvent>();
		public static final AttackProperty<SoundEvent> HIT_SOUND = new AttackProperty<SoundEvent>();

		public static final AttackProperty<ParticleSpawner> PARTICLE = new AttackProperty<ParticleSpawner>();

		public static final AttackProperty<Boolean> BLOCKING = new AttackProperty<Boolean>();
	}
}
