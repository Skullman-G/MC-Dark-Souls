package com.skullmangames.darksouls.common.animation;

import java.util.function.Supplier;

import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public abstract class Property<T>
{
	public static class StaticAnimationProperty<T> extends Property<T>
	{
		public static final StaticAnimationProperty<StaticAnimation.Event[]> EVENTS = new StaticAnimationProperty<StaticAnimation.Event[]>();
		public static final StaticAnimationProperty<Boolean> SHOULD_SYNC = new StaticAnimationProperty<Boolean>();
		public static final StaticAnimationProperty<AnimationLayer.LayerPart> LAYER_PART = new StaticAnimationProperty<AnimationLayer.LayerPart>();
		public static final StaticAnimationProperty<ResourceLocation> DEATH_ANIMATION = new StaticAnimationProperty<ResourceLocation>();
	}

	public static class ActionAnimationProperty<T> extends Property<T>
	{
		public static final ActionAnimationProperty<Boolean> INTERRUPT_PREVIOUS_DELTA_MOVEMENT = new ActionAnimationProperty<Boolean>();
		public static final ActionAnimationProperty<Boolean> MOVE_VERTICAL = new ActionAnimationProperty<Boolean>();
		public static final ActionAnimationProperty<MovementAnimationSet> MOVEMENT_ANIMATION_SETTER = new ActionAnimationProperty<MovementAnimationSet>();
		public static final ActionAnimationProperty<Boolean> AFFECT_SPEED = new ActionAnimationProperty<Boolean>();
		public static final ActionAnimationProperty<Boolean> ALLOW_MIX_LAYERS = new ActionAnimationProperty<Boolean>();
	}
	
	@FunctionalInterface
	public static interface MovementAnimationSet
	{
		public void set(DynamicAnimation self, LivingCap<?> entityCap, TransformSheet transformSheet);
	}

	public static class AttackProperty<T> extends Property<T>
	{
		public static final AttackProperty<MovementDamageType> MOVEMENT_DAMAGE_TYPE = new AttackProperty<MovementDamageType>();
		public static final AttackProperty<StunType> STUN_TYPE = new AttackProperty<StunType>();
		public static final AttackProperty<Deflection> DEFLECTION = new AttackProperty<Deflection>();
		public static final AttackProperty<Integer> STAMINA_USAGE = new AttackProperty<Integer>();
		public static final AttackProperty<Integer> STAMINA_DAMAGE = new AttackProperty<Integer>();
		public static final AttackProperty<Integer> POISE_DAMAGE = new AttackProperty<Integer>();
		public static final AttackProperty<Boolean> DEPENDS_ON_WEAPON = new AttackProperty<Boolean>();

		public static final AttackProperty<Supplier<SoundEvent>> HIT_SOUND = new AttackProperty<Supplier<SoundEvent>>();

		public static final AttackProperty<Boolean> BLOCKING = new AttackProperty<Boolean>();
	}
	
	public static class DeathProperty<T> extends Property<T>
	{
		public static final DeathProperty<Float> DISAPPEAR_AT = new DeathProperty<Float>();
	}
	
	
}
