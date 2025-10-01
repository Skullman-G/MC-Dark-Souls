package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimFrameData;
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
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.init.data.Colliders;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.AuxEffect;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.CoreDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damages;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.MovementDamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.collider.Collider;
import com.skullmangames.darksouls.core.util.json.JsonBuilder;
import com.skullmangames.darksouls.network.ModNetworkManager;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AttackAnimation extends ActionAnimation
{
	private final AttackType attackType;
	public final Phase[] phases;
	
	public AttackAnimation(ResourceLocation id, AttackType attackType, float convertTime,
			float begin, float contactStart, float contactEnd, float end,
			String jointName, AnimFrameData frameData,
			Function<Models<?>, Model> model,
			ImmutableMap<Property<?>, Object> properties, ImmutableMap<AttackProperty<?>, Object> attackProperties)
	{
		this(id, attackType, convertTime, frameData, model, properties,
				new Phase(begin, contactStart, contactEnd, end, InteractionHand.MAIN_HAND,
						jointName, null, attackProperties));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType, float convertTime,
			float begin, float contactStart, float contactEnd, float end,
			InteractionHand hand, String jointName, AnimFrameData frameData,
			Function<Models<?>, Model> model,
			ImmutableMap<Property<?>, Object> properties, ImmutableMap<AttackProperty<?>, Object> attackProperties)
	{
		this(id, attackType, convertTime, frameData, model, properties,
				new Phase(begin, contactStart, contactEnd, end, hand, jointName, null, attackProperties));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, AnimFrameData frameData,
			Function<Models<?>, Model> model, ImmutableMap<Property<?>, Object> properties, Phase... phases)
	{
		super(id, convertTime, frameData, model, properties);
		this.attackType = attackType;
		this.phases = phases;
	}
	
	public AttackType getAttackType()
	{
		return this.attackType;
	}
	
	private void handleStamina(LivingCap<?> entityCap, Phase phase)
	{
		if (entityCap instanceof ServerPlayerCap serverPlayer && !serverPlayer.isCreativeOrSpectator())
		{
			int incr = Math.min(-phase.getProperty(AttackProperty.STAMINA_USAGE).orElse(10), -10);
			serverPlayer.increaseStamina(incr);
		}
		else if (entityCap instanceof MobCap)
		{
			((MobCap<?>)entityCap).increaseStamina(-20);
		}
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
				this.handleStamina(entityCap, phase);
				entityCap.currentlyAttackedEntities.clear();
			}

			Collider collider = this.getCollider(entityCap, elapsedTime);
			entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();
			collider.update(entityCap, phase.getColliderJointName(), 1.0F);
			List<Entity> shields = collider.getShieldCollisions(entity);
			List<Entity> entities = collider.getEntityCollisions(entity, shields);
			
			AttackResult attackResult = new AttackResult(entityCap);
			attackResult.addEntities(entities, false);
			attackResult.addEntities(shields, true);
			
			if (!attackResult.isEmpty())
			{
				boolean hasHurtWithWeapon = false;
				boolean shouldBreak = false;
				
				for (AttackResult.TargetInfo targetInfo : attackResult.getTargetInfos())
				{
					Entity target = targetInfo.getEntity();
					Entity trueTarget = targetInfo.getTrueEntity();
					
					Damages damages = this.getDamageAmount(entityCap, target, phase);
					ExtendedDamageSource source = this.getDamageSourceExt(entityCap, targetInfo.wasBlocked(), collider.getMassCenter(), target, phase, damages);
					
					shouldBreak = this.onDamageTarget(entityCap, target);
					
					if (entityCap.hurtEntity(target, phase.hand, source))
					{
						target.invulnerableTime = 0;
						if (!hasHurtWithWeapon && entityCap instanceof PlayerCap && trueTarget instanceof LivingEntity)
						{
							entityCap.getOriginalEntity().getItemInHand(phase.hand).hurtEnemy((LivingEntity) trueTarget,
									((PlayerCap<?>) entityCap).getOriginalEntity());
							hasHurtWithWeapon = true;
						}
					}
					
					entityCap.currentlyAttackedEntities.add(trueTarget);
					entityCap.slashDelay = 3;
					
					if (shouldBreak) break;
				}
				
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
				entityCap.currentlyAttackedEntities.clear();
			}

			Collider collider = this.getCollider(entityCap, elapsedTime);
			entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();
			collider.update(entityCap, phase.getColliderJointName(), 1.0F);
			List<Entity> shields = collider.getShieldCollisions(entity);
			List<Entity> entities = collider.getEntityCollisions(entity, shields);
			
			AttackResult attackResult = new AttackResult(entityCap);
			attackResult.addEntities(entities, false);
			
			for (AttackResult.TargetInfo targetInfo : attackResult.getTargetInfos())
			{
				Entity trueTarget = targetInfo.getTrueEntity();
				if (trueTarget instanceof Player playerEntity)
				{
					PlayerCap<?> playerCap = (PlayerCap<?>)playerEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
					if (playerCap != null && playerCap.getEntityState().isDodging())
					{
						ModNetworkManager.connection.shakeCamForEntity(playerEntity, 10, 1.0F);
					}
				}
			}
		}
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

	protected Damages getDamageAmount(LivingCap<?> entityCap, Entity target, Phase phase)
	{
		return entityCap.getDamageToEntity(target, phase.hand);
	}

	protected Deflection getRequiredDeflection(Phase phase)
	{
		return phase.getProperty(AttackProperty.DEFLECTION).orElse(Deflection.NONE);
	}
	
	protected StunType getStunType(LivingCap<?> entityCap, Entity target, Phase phase)
	{
		return phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.LIGHT);
	}
	
	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, boolean wasBlocked,
			Vec3 attackPos, Entity target, Phase phase, Damages damages)
	{
		StunType stunType = this.getStunType(entityCap, target, phase);
		Deflection deflection = this.getRequiredDeflection(phase);
		
		int poiseDamage = phase.getProperty(AttackProperty.POISE_DAMAGE).orElse(5);
		int staminaDmg = phase.getProperty(AttackProperty.STAMINA_DAMAGE).orElse(1);
		
		Set<AuxEffect> auxEffects = new HashSet<>();
		if (phase.getProperty(AttackProperty.DEPENDS_ON_WEAPON).orElse(true))
		{
			DamageType movDamageType = phase.getProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE).orElse(MovementDamageType.REGULAR);
			damages.replace(CoreDamageType.PHYSICAL, movDamageType);
			
			WeaponCap weapon = entityCap.getHeldWeaponCap(phase.hand);
			if (weapon != null) auxEffects = weapon.getAuxEffects();
		}
		else damages.mul(0);
		
		ExtendedDamageSource source = entityCap.getDamageSource(attackPos, staminaDmg, stunType, deflection, poiseDamage, damages)
				.addAuxEffects(auxEffects);
		source.setWasBlocked(wasBlocked);
		
		return source;
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
		protected final float begin;
		public final float contactStart;
		public final float contactEnd;
		protected final float end;
		protected final String jointName;
		protected final InteractionHand hand;
		protected Collider collider;
		protected final Map<AttackProperty<?>, Object> properties;

		public Phase(float begin, float contactStart, float contactEnd, float end, InteractionHand hand, String jointName, @Nullable Collider collider,
				ImmutableMap<AttackProperty<?>, Object> properties)
		{
			this.begin = begin;
			this.contactStart = contactStart;
			this.contactEnd = contactEnd;
			this.end = end;
			this.collider = collider;
			this.hand = hand;
			this.jointName = jointName;
			this.properties = properties;
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
		protected final PhaseBuilder[] phases;
		
		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float begin, float contactStart, float contactEnd, float end, String jointName, ResourceLocation path,
				Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new PhaseBuilder(id, begin, contactStart, contactEnd, end, jointName, null));
		}

		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float begin, float contactStart, float contactEnd, float end,
				@Nullable ResourceLocation colliderId, String jointName, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new PhaseBuilder(id, begin, contactStart, contactEnd, end, jointName, colliderId));
		}

		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float begin, float contactStart, float contactEnd, float end, InteractionHand hand,
				String jointName, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new PhaseBuilder(id, begin, contactStart, contactEnd, end, hand, jointName, null));
		}
		
		public Builder(ResourceLocation id, AttackType attackType,
				float convertTime, float begin, float contactStart, float contactEnd, float end, InteractionHand hand,
				@Nullable ResourceLocation colliderId, String jointName, ResourceLocation path, Function<Models<?>, Model> model)
		{
			this(id, attackType, convertTime, path, model, new PhaseBuilder(id, begin, contactStart, contactEnd, end, hand, jointName, colliderId));
		}
		
		public Builder(ResourceLocation id, AttackType attackType, float convertTime, ResourceLocation path, Function<Models<?>, Model> model, PhaseBuilder... phases)
		{
			super(id, convertTime, path, model);
			this.attackType = attackType;
			this.phases = phases;
		}
		
		public Builder(ResourceLocation location, JsonObject json)
		{
			super(location, json);
			
			this.attackType = AttackType.fromString(json.get("attack_type").getAsString());
			
			JsonArray jsonPhases = json.get("phases").getAsJsonArray();
			int phasesLength = jsonPhases.size();
			this.phases = new PhaseBuilder[phasesLength];
			
			for (int i = 0; i < phasesLength; i++)
			{
				JsonObject jsonPhase = jsonPhases.get(i).getAsJsonObject();
				this.phases[i] = new PhaseBuilder(id, jsonPhase);
			}
		}
		
		@Override
		public JsonObject toJson()
		{
			JsonObject json = super.toJson();
			
			json.addProperty("attack_type", this.attackType.toString());
			
			JsonArray jsonPhases = new JsonArray();
			json.add("phases", jsonPhases);
			
			for (PhaseBuilder phase : this.phases) jsonPhases.add(phase.toJson());
			
			return json;
		}
		
		@Override
		public Builder addProperty(Property<?> property, Object value)
		{
			if (property instanceof AttackProperty<?> attackProperty)
			{
				for (PhaseBuilder phase : this.phases) phase.addProperty(attackProperty, value);
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
			Phase[] builtPhases = new Phase[this.phases.length];
			for (int i = 0; i < builtPhases.length; i++)
			{
				builtPhases[i] = this.phases[i].build();
			}
			
			register.put(this.getId(), new AttackAnimation(this.id, this.attackType, this.convertTime,
					this.getFrameData(), this.model, this.properties.build(), builtPhases));
		}
	}
	
	public static class PhaseBuilder implements JsonBuilder<Phase>
	{
		private static final Logger LOGGER = LogUtils.getLogger();
		
		private final ResourceLocation id;
		private final float begin;
		private final float contactStart;
		private final float contactEnd;
		private final float end;
		private final InteractionHand hand;
		private final String jointName;
		@Nullable private final ResourceLocation colliderId;
		
		protected final ImmutableMap.Builder<AttackProperty<?>, Object> properties = new ImmutableMap.Builder<>();
		
		public PhaseBuilder(ResourceLocation id, float begin, float contactStart, float contactEnd, float end, String jointName)
		{
			this(id, begin, contactStart, contactEnd, end, InteractionHand.MAIN_HAND, jointName, null);
		}
		
		public PhaseBuilder(ResourceLocation id, float begin, float contactStart, float contactEnd, float end, String jointName,
				@Nullable ResourceLocation colliderId)
		{
			this(id, begin, contactStart, contactEnd, end, InteractionHand.MAIN_HAND, jointName, colliderId);
		}
		
		public PhaseBuilder(ResourceLocation id, float begin, float contactStart, float contactEnd, float end, InteractionHand hand, String jointName)
		{
			this(id, begin, contactStart, contactEnd, end, hand, jointName, null);
		}
		
		public PhaseBuilder(ResourceLocation id, float begin, float contactStart, float contactEnd, float end, InteractionHand hand,
				String jointName, @Nullable ResourceLocation colliderId)
		{
			this.id = id;
			this.begin = begin;
			this.contactStart = contactStart;
			this.contactEnd = contactEnd;
			this.end = end;
			this.hand = hand;
			this.jointName = jointName;
			this.colliderId = colliderId;
		}
		
		public PhaseBuilder(ResourceLocation location, JsonObject json)
		{
			this.id = location;
			this.begin = json.get("begin").getAsFloat();
			this.contactStart = json.get("contact_start").getAsFloat();
			this.contactEnd = json.get("contact_end").getAsFloat();
			this.end = json.get("end").getAsFloat();
			this.jointName = json.get("weapon_bone_name").getAsString();
			this.hand = InteractionHand.valueOf(json.get("hand").getAsString());
			
			JsonElement colliderJson = json.get("collider");
			this.colliderId = colliderJson != null ? ResourceLocation.tryParse(colliderJson.getAsString()) : null;
			
			JsonObject properties = json.get("properties").getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : properties.entrySet())
			{
				Property<?> property = Property.GET_BY_NAME.get(entry.getKey());
				if (property instanceof AttackProperty<?> attackProperty)
				{
					this.addProperty(attackProperty, attackProperty.jsonConverter.fromJson(entry.getValue()));
				}
				else LOGGER.error("Error while reading phase properties of "+location+". The property with the name "+entry.getKey()+" is not an AttackProperty.");
			}
		}
		
		public PhaseBuilder addProperty(AttackProperty<?> property, Object value)
		{
			this.properties.put(property, value);
			return this;
		}
		
		@Override
		public ResourceLocation getId()
		{
			return this.id;
		}

		@Override
		public JsonObject toJson()
		{
			JsonObject json = new JsonObject();
			json.addProperty("begin", this.begin);
			json.addProperty("contact_start", this.contactStart);
			json.addProperty("contact_end", this.contactEnd);
			json.addProperty("end", this.end);
			json.addProperty("weapon_bone_name", this.jointName);
			json.addProperty("hand", this.hand.name());
			if (this.colliderId != null) json.addProperty("collider", this.colliderId.toString());
			
			JsonObject properties = new JsonObject();
			json.add("properties", properties);
			
			this.properties.build().forEach((p, v) ->
			{
				properties.add(p.name, p.jsonConverter.toJson(v));
			});
			
			return json;
		}

		@Override
		public Phase build()
		{
			Collider collider = this.colliderId == null ? null : Colliders.getCollider(this.colliderId).create();
			return new Phase(this.begin, this.contactStart, this.contactEnd, this.end, this.hand, this.jointName, collider, this.properties.build());
		}
	}
}