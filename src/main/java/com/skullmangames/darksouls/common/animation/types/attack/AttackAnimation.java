package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.client.particles.spawner.ParticleSpawner;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
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
import com.skullmangames.darksouls.config.ClientConfig;
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

public class AttackAnimation extends ActionAnimation
{
	private final AttackType attackType;
	public final Phase[] phases;

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, float antic, float preDelay, float contact, float recovery, String index, ResourceLocation path,
			Function<Models<?>, Model> model)
	{
		this(id, attackType, convertTime, path, model, new Phase(antic, preDelay, contact, recovery, index, null));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, float antic, float preDelay, float contact, float recovery,
			@Nullable Collider collider, String index, ResourceLocation path, Function<Models<?>, Model> model)
	{
		this(id, attackType, convertTime, path, model, new Phase(antic, preDelay, contact, recovery, index, collider));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, InteractionHand hand,
			@Nullable Collider collider, String index, ResourceLocation path, Function<Models<?>, Model> model)
	{
		this(id, attackType, convertTime, path, model, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}

	public AttackAnimation(ResourceLocation id, AttackType attackType,
			float convertTime, ResourceLocation path, Function<Models<?>, Model> model, Phase... phases)
	{
		super(id, convertTime, path, model);
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
			Vec3 prevColPos = collider.getMassCenter();
			List<Entity> list = collider.updateAndFilterCollideEntity(entityCap, phase.getColliderJointName(), 1.0F);
			
			if (list.size() > 0)
			{
				AttackResult attackResult = new AttackResult(entity, list);
				boolean flag1 = true;
				boolean shouldBreak = false;
				do
				{
					Entity e = attackResult.getEntity();
					Entity trueEntity = this.getTrueEntity(e);
					if (!entityCap.currentlyAttackedEntities.contains(trueEntity) && !entityCap.isTeam(trueEntity) && (trueEntity instanceof LivingEntity
						|| trueEntity instanceof BreakableObject))
					{
						if (entity.level.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
										new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
										ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
								.getType() == HitResult.Type.MISS)
						{
							Damages damages = this.getDamageAmount(entityCap, e, phase);
							ExtendedDamageSource source = this.getDamageSourceExt(entityCap, prevColPos, e, phase, damages);
							
							shouldBreak = this.onDamageTarget(entityCap, e);
							if (entityCap.hurtEntity(e, phase.hand, source))
							{
								e.invulnerableTime = 0;
								e.level.playSound(null, e.getX(), e.getY(), e.getZ(), this.getHitSound(entityCap, phase), e.getSoundSource(), 1.0F, 1.0F);
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
		float prevElapsedTime = animPlayer.getPrevElapsedTime();
		EntityState state = this.getState(elapsedTime);
		EntityState prevState = this.getState(prevElapsedTime);
		Phase phase = this.getPhaseByTime(elapsedTime);
		ParticleSpawner spawner = phase.getProperty(AttackProperty.PARTICLE).orElse(null);
		Collider collider = this.getCollider(entityCap, elapsedTime);
		LivingEntity entity = entityCap.getOriginalEntity();
		
		if (spawner != null)
		{
			collider.update(entityCap, phase.getColliderJointName(), 1.0F);
			if (elapsedTime >= phase.contactEnd && prevElapsedTime - ClientConfig.A_TICK <= phase.contactEnd) spawner.spawnParticles((ClientLevel)entity.level, collider.getMassCenter());
		}
		if (state.shouldDetectCollision() && !prevState.shouldDetectCollision())
		{
			entityCap.playSound(this.getSwingSound(entityCap, phase));
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
		for (Phase phase : this.phases) phase.smashed = false;
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

	protected int getRequiredDeflectionLevel(Phase phase)
	{
		return phase.getProperty(AttackProperty.DEFLECTION).orElse(Deflection.NONE).getLevel();
	}

	protected SoundEvent getSwingSound(LivingCap<?> entityCap, Phase phase)
	{
		return phase.getProperty(AttackProperty.SWING_SOUND).orElse(() -> entityCap.getSwingSound(phase.hand)).get();
	}

	protected SoundEvent getHitSound(LivingCap<?> entityCap, Phase phase)
	{
		return phase.getProperty(AttackProperty.HIT_SOUND).orElse(() -> entityCap.getWeaponHitSound(phase.hand)).get();
	}

	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, Vec3 attackPos, Entity target, Phase phase, Damages damages)
	{
		StunType stunType = phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.LIGHT);
		DamageType movDamageType = phase.getProperty(AttackProperty.MOVEMENT_DAMAGE_TYPE).orElse(MovementDamageType.REGULAR);
		damages.replace(CoreDamageType.PHYSICAL, movDamageType);
		int poiseDamage = phase.getProperty(AttackProperty.POISE_DAMAGE).orElse(5);
		int staminaDmg = phase.getProperty(AttackProperty.STAMINA_USAGE).orElse(1);
		WeaponCap weapon = entityCap.getHeldWeaponCap(phase.hand);
		Set<AuxEffect> auxEffects = weapon == null ? new HashSet<>() : weapon.getAuxEffects();
		return entityCap.getDamageSource(attackPos, staminaDmg, stunType, this.getRequiredDeflectionLevel(phase), poiseDamage, damages).addAuxEffects(auxEffects);
	}

	public <V> AttackAnimation addProperty(Property<V> propertyType, V value)
	{
		if (propertyType instanceof AttackProperty)
		{
			for (int i = 0; i < this.phases.length; i++)
			{
				this.addProperty(propertyType, value, i);
			}
		}
		else super.addProperty(propertyType, value);
		return this;
	}

	public <V> AttackAnimation addProperty(Property<V> propertyType, V value, int index)
	{
		this.phases[index].addProperty(propertyType, value);
		return this;
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
	
	@Override
	public AttackAnimation register(Builder<ResourceLocation, StaticAnimation> builder)
	{
		return (AttackAnimation)super.register(builder);
	}

	public static class Phase
	{
		protected final Map<Property<?>, Object> properties = new HashMap<Property<?>, Object>();
		protected final float begin;
		protected final float contactStart;
		protected final float contactEnd;
		protected final float end;
		protected final String jointName;
		protected final InteractionHand hand;
		protected Collider collider;
		protected boolean smashed = false;

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

		public <V> Phase addProperty(Property<V> propertyType, V value)
		{
			this.properties.put(propertyType, value);
			return this;
		}

		public void addProperties(Set<Map.Entry<AttackProperty<?>, Object>> set)
		{
			for (Map.Entry<AttackProperty<?>, Object> entry : set)
			{
				this.properties.put(entry.getKey(), entry.getValue());
			}
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
}