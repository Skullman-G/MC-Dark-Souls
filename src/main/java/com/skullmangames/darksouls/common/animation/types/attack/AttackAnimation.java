package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.client.particles.spawner.ParticleSpawner;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.AnimationPlayer;
import com.skullmangames.darksouls.common.animation.Property;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.capability.entity.EntityState;
import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.config.IngameConfig;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
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
		
		if (state.shouldDetectCollision() || (prevState.getContactLevel() < 2 && state.getContactLevel() > 2))
		{
			if (!prevState.shouldDetectCollision())
			{
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
				entityCap.currentlyAttackedEntities.clear();
			}

			Collider collider = this.getCollider(entityCap, elapsedTime);
			entityCap.getEntityModel(Models.SERVER).getArmature().initializeTransform();				
			float prevPoseTime = phase.contactStart;
			float poseTime = phase.contactEnd;
			Vec3 prevColPos = collider.getWorldCenter();
			List<Entity> list = collider.updateAndFilterCollideEntity(entityCap, this, prevPoseTime, poseTime, phase.getColliderJointName(), this.getPlaySpeed(entityCap));
			
			if (list.size() > 0)
			{
				AttackResult attackResult = new AttackResult(entity, list);
				boolean flag1 = true;
				boolean shouldBreak = false;
				do
				{
					Entity e = attackResult.getEntity();
					Entity trueEntity = this.getTrueEntity(e);
					if (!entityCap.currentlyAttackedEntities.contains(trueEntity) && !entityCap.isTeam(trueEntity) && trueEntity instanceof LivingEntity)
					{
						if (entity.level.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
										new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
										ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
								.getType() == HitResult.Type.MISS)
						{
							float amount = this.getDamageAmount(entityCap, e, phase);
							ExtendedDamageSource source = this.getDamageSourceExt(entityCap, prevColPos, e, phase, amount);
							
							shouldBreak = this.onDamageTarget(entityCap, e);
							if (entityCap.hurtEntity(e, phase.hand, source, amount))
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
			collider.update(entityCap, phase.getColliderJointName());
			if (elapsedTime >= phase.contactEnd && prevElapsedTime - IngameConfig.A_TICK <= phase.contactEnd) spawner.spawnParticles((ClientLevel)entity.level, collider.getWorldCenter());
		}
		if (state.shouldDetectCollision() && !prevState.shouldDetectCollision())
		{
			entityCap.playSound(this.getSwingSound(entityCap, phase));
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingCap<?> entitypatch, float playTime, float partialTicks)
	{
		AnimationPlayer animPlayer = entitypatch.getAnimator().getPlayerFor(this);
		float prevElapsedTime = animPlayer.getPrevElapsedTime();
		float elapsedTime = animPlayer.getElapsedTime();
		this.getCollider(entitypatch, elapsedTime).draw(poseStack, buffer, entitypatch, this, prevElapsedTime, elapsedTime, partialTicks, this.getPlaySpeed(entitypatch));
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
		return phase.getProperty(AttackProperty.SWING_SOUND).orElse(() -> entityCap.getSwingSound(phase.hand)).get();
	}

	protected SoundEvent getHitSound(LivingCap<?> entityCap, Phase phase)
	{
		return phase.getProperty(AttackProperty.HIT_SOUND).orElse(() -> entityCap.getWeaponHitSound(phase.hand)).get();
	}

	protected ExtendedDamageSource getDamageSourceExt(LivingCap<?> entityCap, Vec3 attackPos, Entity target, Phase phase, float amount)
	{
		StunType stunType = phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.LIGHT);
		DamageType damageType = phase.getProperty(AttackProperty.DAMAGE_TYPE).orElse(DamageType.REGULAR);
		float poiseDamage = (float) entityCap.getOriginalEntity().getAttributeValue(ModAttributes.POISE_DAMAGE.get());
		int staminaDmgMul = phase.getProperty(AttackProperty.STAMINA_DMG_MUL).orElse(1);
		ExtendedDamageSource extDmgSource = entityCap.getDamageSource(attackPos, staminaDmgMul, stunType, amount, this.getRequiredDeflectionLevel(phase), damageType, poiseDamage);
		return extDmgSource;
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