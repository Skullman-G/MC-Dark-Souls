package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.AnimationType;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.item.Shield.Deflection;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap.AttackType;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.entity.BreakableObject;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

public class AttackAnimation extends ActionAnimation
{
	private final AttackType attackType;
	public final Phase[] phases;

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, float antic, float preDelay, float contact, float recovery, String index, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, attackType, convertTime, path, model, properties,
				new Phase(antic, preDelay, contact, recovery, index, null));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, float antic, float preDelay, float contact, float recovery,
			@Nullable Collider collider, String index, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, attackType, convertTime, path, model, properties,
				new Phase(antic, preDelay, contact, recovery, index, collider));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, InteractionHand hand,
			@Nullable Collider collider, String index, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties)
	{
		this(id, attackType, convertTime, path, model, properties,
				new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, ResourceLocation path,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties, Phase... phases)
	{
		super(id, convertTime, path, model, properties);
		this.attackType = attackType;
		this.phases = phases;
	}
	
	public AttackType getAttackType()
	{
		return this.attackType;
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		
		if (entityCap.isClientSide())
		{
			this.onClientUpdate(entityCap);
			return;
		}
		
		LivingEntity entity = entityCap.getOriginalEntity();

		AnimationPlayer animPlayer = entityCap.getAnimator().getPlayerFor(this);
		float elapsedTime = animPlayer.getElapsedTime();
		float prevElapsedTime = animPlayer.getPrevElapsedTime();
		EntityState state = this.getState(elapsedTime);
		EntityState prevState = this.getState(prevElapsedTime);
		Phase phase = this.getPhaseByTime(elapsedTime);
		
		entityCap.weaponCollider = this.getCollider(entityCap, elapsedTime);
		
		if (state.shouldDetectCollision() || (prevState.getContactLevel() < 2 && state.getContactLevel() > 2))
		{
			if (!prevState.shouldDetectCollision())
			{
				if (entityCap instanceof ServerPlayerCap && !((ServerPlayerCap) entityCap).isCreativeOrSpectator())
				{
					int incr = Math.min(-phase.getProperty(AttackProperty.STAMINA_USAGE).orElse(25), -25);
					((ServerPlayerCap)entityCap).increaseStamina(incr);
				}
				else if (entityCap instanceof MobCap)
				{
					((MobCap<?>)entityCap).increaseStamina(-20);
				}
				entityCap.currentlyAttackedEntities.clear();
			}

			Collider collider = this.getCollider(entityCap, elapsedTime);
			entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();
			Vec3 prevColPos = null;
			if (entityCap.lastColTransform != null)
			{
				collider.transform(entityCap.lastColTransform);
				prevColPos = collider.getMassCenter();
			}
			collider.update(entityCap, phase.getColliderJointName(), 1.0F, true);
			if (prevColPos == null) prevColPos = collider.getMassCenter();
			List<Entity> shields = collider.getShieldCollisions(entity);
			List<Entity> entities = collider.getEntityCollisions(entity);
			entities.removeIf((e) -> shields.contains(e));
			
			if (!shields.isEmpty() || !entities.isEmpty())
			{
				AttackResult attackResult = new AttackResult(entity);
				attackResult.addEntities(entities, false);
				attackResult.addEntities(shields, true);
				
				boolean flag1 = true;
				boolean shouldBreak = false;
				
				do
				{
					Entity e = attackResult.getEntity();
					Entity trueEntity = this.getTrueEntity(e);
					if (!entityCap.currentlyAttackedEntities.contains(trueEntity) && !entityCap.isTeam(trueEntity) && (trueEntity instanceof LivingEntity
						|| trueEntity instanceof BreakableObject))
					{
						// Check if a block is in the way
						if (entity.level.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
										new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
										ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
								.getType() == HitResult.Type.MISS)
						{
							Damages damages = this.getDamageAmount(entityCap, e, phase);
							ExtendedDamageSource source = this.getDamageSourceExt(entityCap, prevColPos, e, phase, damages);
							source.setWasBlocked(attackResult.wasBlocked());
							
							shouldBreak = this.onDamageTarget(entityCap, e);
							if (entityCap.hurtEntity(e, phase.hand, source))
							{
								e.invulnerableTime = 0;
								if (flag1 && entityCap instanceof PlayerCap && trueEntity instanceof LivingEntity)
								{
									entityCap.getOriginalEntity().getItemInHand(phase.hand).hurtEnemy((LivingEntity) trueEntity,
											((PlayerCap<?>) entityCap).getOriginalEntity());
									flag1 = false;
								}
							}
							entityCap.currentlyAttackedEntities.add(trueEntity);
							entityCap.slashDelay = 3;
						}
					}
					if (shouldBreak) break;
				} while (attackResult.next());
				
				this.onAttackFinish(entityCap, shouldBreak);
			}
		}
	}
	
	protected boolean onDamageTarget(LivingCap<?> entityCap, Entity target)
	{
		return false;
	}
	
	protected void onAttackFinish(LivingCap<?> entityCap, boolean critical) {}
	
	@OnlyIn(Dist.CLIENT)
	public void onClientUpdate(LivingCap<?> entityCap)
	{
		AnimationPlayer animPlayer = entityCap.getAnimator().getPlayerFor(this);
		float elapsedTime = animPlayer.getElapsedTime();
		
		entityCap.weaponCollider = this.getCollider(entityCap, elapsedTime);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entityCap, float partialTicks)
	{
		AnimationPlayer animPlayer = entityCap.getAnimator().getPlayerFor(this);
		float elapsedTime = animPlayer.getElapsedTime();
		this.getCollider(entityCap, elapsedTime).draw(entityCap, this.getPathIndexByTime(elapsedTime), partialTicks);
	}
	
	@Override
	public float getPlaySpeed(LivingCap<?> entityCap)
	{
		float speed = super.getPlaySpeed(entityCap);
		if (entityCap.slashDelay > 0)
		{
			speed *= 0.1F;
			--entityCap.slashDelay;
		}
		return speed;
	}
	
	public String getPathIndexByTime(float elapsedTime)
	{
		return this.getPhaseByTime(elapsedTime).jointName;
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		entityCap.weaponCollider = null;
		entityCap.currentlyAttackedEntities.clear();
		if (entityCap instanceof HumanoidCap && entityCap.isClientSide())
		{
			Mob entity = (Mob) entityCap.getOriginalEntity();
			if (entity.getTarget() != null && !entity.getTarget().isAlive())
				entity.setTarget((LivingEntity) null);
		}
		entityCap.lastColTransform = null;
	}

	@Override
	public EntityState getState(float time)
	{
		Phase phase = this.getPhaseByTime(time);

		if (time <= phase.begin || (phase.begin < time && time < phase.contactStart))
		{
			return EntityState.PRE_CONTACT;
		} else if (phase.contactStart <= time && time <= phase.contactEnd)
		{
			return EntityState.CONTACT;
		} else if (time < phase.end)
		{
			return EntityState.POST_CONTACT;
		} else
		{
			return EntityState.FREE_INPUT;
		}
	}
	
	@Override
	public <V> Optional<V> getPropertyByTime(AttackProperty<V> propertyType, float elapsedTime)
	{
		return this.getPhaseByTime(elapsedTime).getProperty(propertyType);
	}

	public Collider getCollider(LivingCap<?> entityCap, float elapsedTime)
	{
		Phase phase = this.getPhaseByTime(elapsedTime);
		return phase.collider != null ? phase.collider : entityCap.getColliderMatching(phase.hand);
	}

	public Entity getTrueEntity(Entity entity)
	{
		if (entity instanceof PartEntity)
		{
			return ((PartEntity<?>) entity).getParent();
		}

		return entity;
	}

	protected Damages getDamageAmount(LivingCap<?> entityCap, Entity target, Phase phase)
	{
		return entityCap.getDamageToEntity(target, phase.hand);
	}

	protected Deflection getRequiredDeflection(Phase phase)
	{
		return phase.getProperty(AttackProperty.DEFLECTION).orElse(Deflection.NONE);
	}

	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, Vec3 attackPos, Entity target, Phase phase, Damages damages)
	{
		StunType stunType = phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.LIGHT);
		Set<AuxEffect> auxEffects = new HashSet<>();
		
		if (phase.getProperty(AttackProperty.DEPENDS_ON_WEAPON).orElse(true))
		{
			DamageType movDamageType = phase.getProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE).orElse(MovementDamageType.REGULAR);
			damages.replace(CoreDamageType.PHYSICAL, movDamageType);
			WeaponCap weapon = entityCap.getHeldWeaponCap(phase.hand);
			auxEffects = weapon == null ? new HashSet<>() : weapon.getAuxEffects();
		}
		else damages.mul(0);
		
		int poiseDamage = phase.getProperty(AttackProperty.POISE_DAMAGE).orElse(5);
		int staminaDmg = phase.getProperty(AttackProperty.STAMINA_DAMAGE).orElse(0);
		
		return entityCap.getDamageSource(attackPos, staminaDmg, stunType, this.getRequiredDeflection(phase), poiseDamage, damages).addAuxEffects(auxEffects);
	}

	public Phase getPhaseByTime(float elapsedTime)
	{
		Phase currentPhase = null;
		for (Phase phase : this.phases)
		{
			currentPhase = phase;
			if (phase.end > elapsedTime)
			{
				break;
			}
		}
		return currentPhase;
	}

	public static class Phase
	{
		protected final Map<Property<?>, Object> properties = new HashMap<>();
		protected final float begin;
		public final float contactStart;
		public final float contactEnd;
		protected final float end;
		protected final String jointName;
		protected final InteractionHand hand;
		protected Collider collider;

		public Phase(float antic, float preDelay, float contact, float recovery, String jointName)
		{
			this(antic, preDelay, contact, recovery, jointName, null);
		}
		
		public Phase(float antic, float preDelay, float contact, float recovery, String jointName, Collider collider)
		{
			this(antic, preDelay, contact, recovery, InteractionHand.MAIN_HAND, jointName, collider);
		}

		public Phase(float antic, float preDelay, float contact, float recovery, InteractionHand hand, String jointName, Collider collider)
		{
			this.begin = antic;
			this.contactStart = preDelay;
			this.contactEnd = contact;
			this.end = recovery;
			this.collider = collider;
			this.hand = hand;
			this.jointName = jointName;
		}
		
		public void addProperty(Property<?> property, Object value)
		{
			this.properties.put(property, value);
		}

		@SuppressWarnings("unchecked")
		public <V> Optional<V> getProperty(AttackProperty<V> propertyType)
		{
			return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
		}
		
		public String getColliderJointName()
		{
			return this.jointName;
		}
	}
	
	public static class Builder extends ActionAnimation.Builder
	{
		protected final AttackType attackType;
		protected final Phase[] phases;
		
		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float antic, float preDelay, float contact, float recovery, String index, ResourceLocation path,
				Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new Phase(antic, preDelay, contact, recovery, index, null));
		}

		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float antic, float preDelay, float contact, float recovery,
				@Nullable Collider collider, String index, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new Phase(antic, preDelay, contact, recovery, index, collider));
		}

		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, InteractionHand hand,
				@Nullable Collider collider, String index, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
		}
		
		public Builder(ResourceLocation id, AttackType attackType, float convertTime, ResourceLocation path, Function<Models<?>, Model> model, Phase... phases)
		{
			super(id, convertTime, path, model);
			this.attackType = attackType;
			this.phases = phases;
		}
		
		@SuppressWarnings({ "rawtypes" })
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			
			this.attackType = AttackType.fromString(json.get("attack_type").getAsString());
			
			JsonArray jsonPhases = json.get("phases").getAsJsonArray();
			int phasesLength = jsonPhases.size();
			AttackAnimation.Phase[] ps = new AttackAnimation.Phase[phasesLength];
			
			for (int i = 0; i < phasesLength; i++)
			{
				JsonObject jsonPhase = jsonPhases.get(i).getAsJsonObject();
				float start = jsonPhase.get("begin").getAsFloat();
				float preDelay = jsonPhase.get("contact_start").getAsFloat();
				float contact = jsonPhase.get("contact_end").getAsFloat();
				float end = jsonPhase.get("end").getAsFloat();
				String weaponBoneName = jsonPhase.get("weapon_bone_name").getAsString();
				ps[i] = new AttackAnimation.Phase(start, preDelay, contact, end, weaponBoneName);
				
				JsonObject properties = jsonPhase.get("properties").getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : properties.entrySet())
				{
					Property property = Property.GET_BY_NAME.get(entry.getKey());
					ps[i].addProperty(property, property.jsonConverter.fromJson(entry.getValue()));
				}
			}
			
			this.phases = ps;
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			
			json.addProperty("attack_type", this.attackType.toString());
			
			JsonArray jsonPhases = new JsonArray();
			json.add("phases", jsonPhases);
			
			for (AttackAnimation.Phase phase : this.phases)
			{
				JsonObject jsonPhase = new JsonObject();
				jsonPhases.add(jsonPhase);
				jsonPhase.addProperty("begin", phase.begin);
				jsonPhase.addProperty("contact_start", phase.contactStart);
				jsonPhase.addProperty("contact_end", phase.contactEnd);
				jsonPhase.addProperty("end", phase.end);
				jsonPhase.addProperty("weapon_bone_name", phase.jointName);
				
				JsonObject properties = new JsonObject();
				jsonPhase.add("properties", properties);
				
				phase.properties.forEach((p, v) ->
				{
					properties.add(p.name, p.jsonConverter.toJson(v));
				});
			}
			
			return json;
		}
		
		@Override
		public <V> Builder addProperty(Property<V> property, V value)
		{
			if (property instanceof AttackProperty)
			{
				for (Phase phase : this.phases) phase.addProperty(property, value);
			}
			else super.addProperty(property, value);
			return this;
		}
		
		@Override
		public AnimationType getAnimType()
		{
			return AnimationType.ATTACK;
		}
		
		@Override
		public void register(ImmutableMap.Builder<ResourceLocation, StaticAnimation> register)
		{
			register.put(this.getId(), new AttackAnimation(this.id, this.attackType, this.convertTime,
					this.location, this.model, this.properties.build(), this.phases));
		}
	}
}