package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.types.attack.Property.StaticAnimationProperty;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

public class AttackAnimation extends ActionAnimation
{
	public final Phase[] phases;

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, String index, String path,
			Function<Models<?>, Model> model)
	{
		this(convertTime, path, model, new Phase(antic, preDelay, contact, recovery, index, null));
	}

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery,
			@Nullable Collider collider, String index, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, path, model, new Phase(antic, preDelay, contact, recovery, index, collider));
	}

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, InteractionHand hand,
			@Nullable Collider collider, String index, String path, Function<Models<?>, Model> model)
	{
		this(convertTime, path, model, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}

	public AttackAnimation(float convertTime, String path, Function<Models<?>, Model> model, Phase... phases)
	{
		super(convertTime, path, model);
		this.phases = phases;
	}

	@Override
	public void onUpdate(LivingCap<?> entityCap)
	{
		super.onUpdate(entityCap);
		
		LivingEntity entity = entityCap.getOriginalEntity();
		
		if (entityCap.isClientSide())
		{
			this.onClientUpdate(entityCap);
			return;
		}

		AnimationPlayer animPlayer = entityCap.getAnimator().getPlayerFor(this);
		float elapsedTime = animPlayer.getElapsedTime();
		float prevElapsedTime = animPlayer.getPrevElapsedTime();
		EntityState state = this.getState(elapsedTime);
		EntityState prevState = this.getState(prevElapsedTime);
		Phase phase = this.getPhaseByTime(elapsedTime);

		if (state == EntityState.FREE_CAMERA)
		{
			if (entityCap instanceof MobCap)
			{
				((Mob) entityCap.getOriginalEntity()).getNavigation().stop();
				LivingEntity target = entityCap.getTarget();
				if (target != null)
				{
					entityCap.rotateTo(target, 60.0F, false);
				}
			}
		} else if (state.shouldDetectCollision() || (prevState.getContactLevel() < 2 && state.getContactLevel() > 2))
		{
			if (!prevState.shouldDetectCollision())
			{
				entityCap.playSound(this.getSwingSound(entityCap, phase), 0.0F, 0.0F, 0.5F);
				if (entityCap instanceof ServerPlayerCap && !((ServerPlayerCap) entityCap).isCreativeOrSpectator())
				{
					WeaponCap weapon = ModCapabilities.getWeaponCap(entityCap.getOriginalEntity().getMainHandItem());
					float incr = weapon == null ? -4.0F : Math.min(-weapon.weight / 3, -4.0F);
					((ServerPlayerCap)entityCap).increaseStamina(incr);
				}
				else if (entityCap instanceof MobCap)
				{
					((MobCap<?>)entityCap).increaseStamina(-2.5F);
				}
				entityCap.currentlyAttackedEntity.clear();
			}

			Collider collider = this.getCollider(entityCap, elapsedTime);
			entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();				
			float prevPoseTime = phase.contactStart;
			float poseTime = phase.contactEnd;
			List<Entity> list = collider.updateAndFilterCollideEntity(entityCap, this, prevPoseTime, poseTime, phase.getColliderJointName(), this.getPlaySpeed(entityCap));

			if (list.size() > 0)
			{
				AttackResult attackResult = new AttackResult(entity, list);
				boolean flag1 = true;
				do
				{
					Entity e = attackResult.getEntity();
					Entity trueEntity = this.getTrueEntity(e);
					if (!entityCap.currentlyAttackedEntity.contains(trueEntity) && !entityCap.isTeam(e))
					{
						if (e instanceof LivingEntity || e instanceof PartEntity)
						{
							if (entity.level
									.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
											new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
											ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
									.getType() == HitResult.Type.MISS)
							{

								float amount = this.getDamageAmount(entityCap, e, phase);
								IExtendedDamageSource source = this.getDamageSourceExt(entityCap, e, phase, amount);
								
								if (entityCap.hurtEntity(e, phase.hand, source, amount))
								{
									e.invulnerableTime = 0;
									e.level.playSound(null, e.getX(), e.getY(), e.getZ(), this.getHitSound(entityCap, phase), e.getSoundSource(),
											1.0F, 1.0F);
									if (flag1 && entityCap instanceof PlayerCap && trueEntity instanceof LivingEntity)
									{
										entityCap.getOriginalEntity().getItemInHand(phase.hand).hurtEnemy((LivingEntity) trueEntity,
												((PlayerCap<?>) entityCap).getOriginalEntity());
										flag1 = false;
									}
								}
								entityCap.currentlyAttackedEntity.add(trueEntity);
							}
						}
					}
				} while (attackResult.next());
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onClientUpdate(LivingCap<?> entityCap)
	{
		AnimationPlayer animPlayer = entityCap.getAnimator().getPlayerFor(this);
		float elapsedTime = animPlayer.getElapsedTime();
		float prevElapsedTime = animPlayer.getPrevElapsedTime();
		Phase phase = this.getPhaseByTime(elapsedTime);
		ParticleSpawner spawner = phase.getProperty(AttackProperty.PARTICLE).orElse(null);
		
		if (spawner == null) return;
		
		LivingEntity entity = entityCap.getOriginalEntity();
		
		Collider collider = this.getCollider(entityCap, elapsedTime);
		Vector3d pos = collider.getCenter();
		pos = new Vector3d(-pos.x, pos.y, -pos.z);
		if (elapsedTime >= phase.contactEnd && prevElapsedTime - 0.4F < phase.contactEnd) spawner.spawnParticles((ClientLevel)entity.level, pos);
	}

	@Override
	public void onFinish(LivingCap<?> entityCap, boolean isEnd)
	{
		super.onFinish(entityCap, isEnd);
		entityCap.currentlyAttackedEntity.clear();
		if (entityCap instanceof HumanoidCap && entityCap.isClientSide())
		{
			Mob entity = (Mob) entityCap.getOriginalEntity();
			if (entity.getTarget() != null && !entity.getTarget().isAlive())
				entity.setTarget((LivingEntity) null);
		}
		for (Phase phase : this.phases)
			phase.smashed = false;
	}

	@Override
	public EntityState getState(float time)
	{
		Phase phase = this.getPhaseByTime(time);

		if (time <= phase.begin || (phase.begin < time && time < phase.contactStart))
		{
			return EntityState.FREE_CAMERA;
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

	protected float getDamageAmount(LivingCap<?> entityCap, Entity target, Phase phase)
	{
		return entityCap.getDamageToEntity(target, phase.hand);
	}

	protected int getRequiredDeflectionLevel(Phase phase)
	{
		return phase.getProperty(AttackProperty.DEFLECTION).orElse(Deflection.NONE).getLevel();
	}

	protected SoundEvent getSwingSound(LivingCap<?> entityCap, Phase phase)
	{
		return phase.getProperty(AttackProperty.SWING_SOUND).orElse(entityCap.getSwingSound(phase.hand));
	}

	protected SoundEvent getHitSound(LivingCap<?> entityCap, Phase phase)
	{
		return phase.getProperty(AttackProperty.HIT_SOUND).orElse(entityCap.getWeaponHitSound(phase.hand));
	}

	protected IExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, Entity target, Phase phase, float amount)
	{
		StunType stunType = phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.DEFAULT);
		DamageType damageType = phase.getProperty(AttackProperty.DAMAGE_TYPE).orElse(DamageType.REGULAR);
		float poiseDamage = (float) entityCap.getOriginalEntity().getAttributeValue(ModAttributes.POISE_DAMAGE.get());
		int staminaDmgMul = phase.getProperty(AttackProperty.STAMINA_DMG_MUL).orElse(1);
		IExtendedDamageSource extDmgSource = entityCap.getDamageSource(staminaDmgMul, stunType, amount, this.getRequiredDeflectionLevel(phase), damageType, poiseDamage);
		return extDmgSource;
	}
	
	@Override
	public <V> AttackAnimation addProperty(StaticAnimationProperty<V> propertyType, V value)
	{
		return (AttackAnimation)super.addProperty(propertyType, value);
	}

	public <V> AttackAnimation addProperty(AttackProperty<V> propertyType, V value)
	{
		for (int i = 0; i < this.phases.length; i++)
		{
			this.addProperty(propertyType, value, i);
		}
		return this;
	}

	public <V> AttackAnimation addProperty(AttackProperty<V> propertyType, V value, int index)
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

	public static class Phase
	{
		protected final Map<AttackProperty<?>, Object> properties = new HashMap<AttackProperty<?>, Object>();;
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

		public <V> Phase addProperty(AttackProperty<V> propertyType, V value)
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