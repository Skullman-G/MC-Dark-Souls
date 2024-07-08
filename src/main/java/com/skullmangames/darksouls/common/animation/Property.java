package com.skullmangames.darksouls.common.animation;

import java.util.HashMap;
import java.util.Map;
import com.skullmangames.darksouls.client.animation.AnimationLayer;
import com.skullmangames.darksouls.common.animation.events.AnimEvent;
import com.skullmangames.darksouls.common.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.JsonElementConverter;

import net.minecraft.resources.ResourceLocation;

public abstract class Property<T>
{
	public static Map<String, Property<? extends Object>> GET_BY_NAME = new HashMap<>();
	
	public final String name;
	public final JsonElementConverter<T> jsonConverter;
	
	public Property(String name, JsonElementConverter<T> jsonConverter)
	{
		this.name = name;
		this.jsonConverter = jsonConverter;
		GET_BY_NAME.put(this.name, this);
	}
	
	public static class StaticAnimationProperty<T> extends Property<T>
	{
		private StaticAnimationProperty(String name, JsonElementConverter<T> jsonConverter)
		{
			super(name, jsonConverter);
		}
		
		public static final StaticAnimationProperty<AnimEvent[]> EVENTS = new StaticAnimationProperty<>("events", JsonElementConverter.EVENTS);
		public static final StaticAnimationProperty<Boolean> SHOULD_SYNC = new StaticAnimationProperty<>("should_sync", JsonElementConverter.BOOLEAN);
		public static final StaticAnimationProperty<AnimationLayer.LayerPart> LAYER_PART = new StaticAnimationProperty<>("layer_part", JsonElementConverter.ENUM(AnimationLayer.LayerPart.class));
		public static final StaticAnimationProperty<ResourceLocation> DEATH_ANIMATION = new StaticAnimationProperty<>("death_animation", JsonElementConverter.RESOURCE_LOCATION);
	}

	public static class ActionAnimationProperty<T> extends Property<T>
	{
		private ActionAnimationProperty(String name, JsonElementConverter<T> jsonConverter)
		{
			super(name, jsonConverter);
		}
		
		public static final ActionAnimationProperty<Boolean> INTERRUPT_PREVIOUS_DELTA_MOVEMENT = new ActionAnimationProperty<>("interrupt_previous_delta_movement", JsonElementConverter.BOOLEAN);
		public static final ActionAnimationProperty<Boolean> MOVE_VERTICAL = new ActionAnimationProperty<>("move_vertical", JsonElementConverter.BOOLEAN);
		public static final ActionAnimationProperty<MovementAnimationSet> MOVEMENT_ANIMATION_SETTER = new ActionAnimationProperty<MovementAnimationSet>("movement_animation_setter", JsonElementConverter.MOVEMENT_ANIMATION_SET);
		public static final ActionAnimationProperty<Boolean> AFFECT_SPEED = new ActionAnimationProperty<>("affect_speed", JsonElementConverter.BOOLEAN);
		public static final ActionAnimationProperty<Boolean> ALLOW_MIX_LAYERS = new ActionAnimationProperty<>("allow_mix_layers", JsonElementConverter.BOOLEAN);
		public static final ActionAnimationProperty<Boolean> PUNISHABLE = new ActionAnimationProperty<>("punishable", JsonElementConverter.BOOLEAN);
		public static final ActionAnimationProperty<Boolean> IS_HIT = new ActionAnimationProperty<>("is_hit", JsonElementConverter.BOOLEAN);
	}
	
	public static class AimingAnimationProperty<T> extends Property<T>
	{
		private AimingAnimationProperty(String name, JsonElementConverter<T> jsonConverter)
		{
			super(name, jsonConverter);
		}
		
		public static final AimingAnimationProperty<Boolean> IS_REBOUND = new AimingAnimationProperty<>("is_rebound", JsonElementConverter.BOOLEAN);
	}
	
	@FunctionalInterface
	public static interface MovementAnimationSet
	{
		public void set(DynamicAnimation self, LivingCap<?> entityCap, TransformSheet transformSheet);
	}

	public static class AttackProperty<T> extends Property<T>
	{
		private AttackProperty(String name, JsonElementConverter<T> jsonConverter)
		{
			super(name, jsonConverter);
		}
		
		public static final AttackProperty<MovementDamageType> MOVEMENT_DAMAGE_TYPE = new AttackProperty<>("movement_damage_type", JsonElementConverter.ENUM(MovementDamageType.class));
		public static final AttackProperty<StunType> STUN_TYPE = new AttackProperty<>("stun_type", JsonElementConverter.ENUM(StunType.class));
		public static final AttackProperty<Deflection> DEFLECTION = new AttackProperty<>("deflection", JsonElementConverter.ENUM(Deflection.class));
		public static final AttackProperty<Integer> STAMINA_USAGE = new AttackProperty<>("stamina_usage", JsonElementConverter.INTEGER);
		public static final AttackProperty<Integer> STAMINA_DAMAGE = new AttackProperty<>("stamina_damage", JsonElementConverter.INTEGER);
		public static final AttackProperty<Integer> POISE_DAMAGE = new AttackProperty<>("poise_damage", JsonElementConverter.INTEGER);
		public static final AttackProperty<Boolean> DEPENDS_ON_WEAPON = new AttackProperty<>("depends_on_weapon", JsonElementConverter.BOOLEAN);

		public static final AttackProperty<Boolean> BLOCKING = new AttackProperty<>("blocking", JsonElementConverter.BOOLEAN);
	}
	
	public static class DeathProperty<T> extends Property<T>
	{
		private DeathProperty(String name, JsonElementConverter<T> jsonConverter)
		{
			super(name, jsonConverter);
		}
		
		public static final DeathProperty<Float> DISAPPEAR_AT = new DeathProperty<>("disappear_at", JsonElementConverter.FLOAT);
	}
	
	
}
