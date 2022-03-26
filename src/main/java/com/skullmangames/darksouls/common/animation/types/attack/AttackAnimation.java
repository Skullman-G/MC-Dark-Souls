package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.attack.Property.AttackProperty;
import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.MobCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
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
import net.minecraftforge.registries.RegistryObject;

public class AttackAnimation extends ActionAnimation
{
	public final Phase[] phases;

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, String index, String path,
			String armature)
	{
		this(convertTime, affectY, path, armature, new Phase(antic, preDelay, contact, recovery, index, null));
	}

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			@Nullable Collider collider, String index, String path, String armature)
	{
		this(convertTime, affectY, path, armature, new Phase(antic, preDelay, contact, recovery, index, collider));
	}

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, InteractionHand hand,
			@Nullable Collider collider, String index, String path, String armature)
	{
		this(convertTime, affectY, path, armature, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}

	public AttackAnimation(float convertTime, boolean affectY, String path, String armature, Phase... phases)
	{
		super(convertTime, affectY, path, armature);
		this.phases = phases;
	}

	@Override
	public void onUpdate(LivingCap<?> entitydata)
	{
		super.onUpdate(entitydata);
		
		LivingEntity entity = entitydata.getOriginalEntity();
		
		if (entitydata.isClientSide())
		{
			this.onClientUpdate(entitydata);
			return;
		}

		float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
		float prevElapsedTime = entitydata.getAnimator().getPlayer().getPrevElapsedTime();
		LivingCap.EntityState state = this.getState(elapsedTime);
		LivingCap.EntityState prevState = this.getState(prevElapsedTime);
		Phase phase = this.getPhaseByTime(elapsedTime);

		if (state == LivingCap.EntityState.FREE_CAMERA)
		{
			if (entitydata instanceof MobCap)
			{
				((Mob) entitydata.getOriginalEntity()).getNavigation().stop();
				LivingEntity target = entitydata.getTarget();
				if (target != null)
				{
					entitydata.rotateTo(target, 60.0F, false);
				}
			}
		} else if (state.shouldDetectCollision() || (prevState.getContactLevel() < 2 && state.getContactLevel() > 2))
		{
			if (!prevState.shouldDetectCollision())
			{
				entitydata.playSound(this.getSwingSound(entitydata, phase), 0.0F, 0.0F, 0.5F);
				if (entitydata instanceof ServerPlayerCap && !((ServerPlayerCap) entitydata).isCreativeOrSpectator())
				{
					WeaponCap weapon = ModCapabilities.getWeaponCap(entitydata.getOriginalEntity().getMainHandItem());
					float incr = weapon == null ? -4.0F : Math.min(-weapon.weight / 3, -4.0F);
					((ServerPlayerCap)entitydata).increaseStamina(incr);
				}
				else if (entitydata instanceof MobCap)
				{
					((MobCap<?>)entitydata).increaseStamina(-2.5F);
				}
				entitydata.currentlyAttackedEntity.clear();
			}

			Collider collider = this.getCollider(entitydata, elapsedTime);
			entitydata.getEntityModel(Models.SERVER).getArmature().initializeTransform();
			PublicMatrix4f jointTransform = entitydata.getServerAnimator().getColliderTransformMatrix(phase.jointIndexer);
			collider.transform(PublicMatrix4f.mul(entitydata.getModelMatrix(1.0F), jointTransform, null));
			List<Entity> list = entity.level.getEntities(entity, collider.getHitboxAABB());
			collider.extractHitEntities(list);

			if (list.size() > 0)
			{
				AttackResult attackResult = new AttackResult(entity, list);
				boolean flag1 = true;
				do
				{
					Entity e = attackResult.getEntity();
					Entity trueEntity = this.getTrueEntity(e);
					if (!entitydata.currentlyAttackedEntity.contains(trueEntity) && !entitydata.isTeam(e))
					{
						if (e instanceof LivingEntity || e instanceof PartEntity)
						{
							if (entity.level
									.clip(new ClipContext(new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
											new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
											ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
									.getType() == HitResult.Type.MISS)
							{

								float amount = this.getDamageAmount(entitydata, e, phase);
								IExtendedDamageSource source = this.getDamageSourceExt(entitydata, e, phase, amount);
								
								if (entitydata.hurtEntity(e, phase.hand, source, amount))
								{
									e.invulnerableTime = 0;
									e.level.playSound(null, e.getX(), e.getY(), e.getZ(), this.getHitSound(entitydata, phase), e.getSoundSource(),
											1.0F, 1.0F);
									if (flag1 && entitydata instanceof PlayerCap && trueEntity instanceof LivingEntity)
									{
										entitydata.getOriginalEntity().getItemInHand(phase.hand).hurtEnemy((LivingEntity) trueEntity,
												((PlayerCap<?>) entitydata).getOriginalEntity());
										flag1 = false;
									}
								}
								entitydata.currentlyAttackedEntity.add(trueEntity);
							}
						}
					}
				} while (attackResult.next());
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void onClientUpdate(LivingCap<?> entitydata)
	{
		float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
		float prevElapsedTime = entitydata.getAnimator().getPlayer().getPrevElapsedTime();
		Phase phase = this.getPhaseByTime(elapsedTime);
		ParticleSpawner spawner = phase.getProperty(AttackProperty.PARTICLE).orElse(null);
		
		if (spawner == null) return;
		
		LivingEntity entity = entitydata.getOriginalEntity();
		
		Collider collider = this.getCollider(entitydata, elapsedTime);
		Vector3d pos = collider.getCenter();
		pos = new Vector3d(-pos.x, pos.y, -pos.z);
		if (elapsedTime >= phase.contactEnd && prevElapsedTime - 0.4F < phase.contactEnd) spawner.spawnParticles((ClientLevel)entity.level, pos);
	}

	@Override
	public void onFinish(LivingCap<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);

		if (entitydata instanceof PlayerCap)
		{
			((PlayerCap<?>) entitydata).getEventListener().activateEvents(EventType.ON_ATTACK_END_EVENT, entitydata.currentlyAttackedEntity.size(),
					this.getId());
		}

		entitydata.currentlyAttackedEntity.clear();

		if (entitydata instanceof HumanoidCap && entitydata.isClientSide())
		{
			Mob entity = (Mob) entitydata.getOriginalEntity();
			if (entity.getTarget() != null && !entity.getTarget().isAlive())
				entity.setTarget((LivingEntity) null);
		}

		for (Phase phase : this.phases)
			phase.smashed = false;
	}

	@Override
	public LivingCap.EntityState getState(float time)
	{
		Phase phase = this.getPhaseByTime(time);

		if (time <= phase.begin || (phase.begin < time && time < phase.contactStart))
		{
			return LivingCap.EntityState.FREE_CAMERA;
		} else if (phase.contactStart <= time && time <= phase.contactEnd)
		{
			return LivingCap.EntityState.CONTACT;
		} else if (time < phase.end)
		{
			return LivingCap.EntityState.POST_DELAY;
		} else
		{
			return LivingCap.EntityState.FREE_INPUT;
		}
	}

	public Collider getCollider(LivingCap<?> entitydata, float elapsedTime)
	{
		Phase phase = this.getPhaseByTime(elapsedTime);
		return phase.collider != null ? phase.collider : entitydata.getColliderMatching(phase.hand);
	}
	
	@Override
	public AttackAnimation registerSound(RegistryObject<SoundEvent> sound, float time, boolean isRemote)
	{
		super.registerSound(sound, time, isRemote);
		return this;
	}

	public Entity getTrueEntity(Entity entity)
	{
		if (entity instanceof PartEntity)
		{
			return ((PartEntity<?>) entity).getParent();
		}

		return entity;
	}

	protected float getDamageAmount(LivingCap<?> entitydata, Entity target, Phase phase)
	{
		return entitydata.getDamageToEntity(target, phase.hand);
	}

	protected int getRequiredDeflectionLevel(Phase phase)
	{
		return phase.getProperty(AttackProperty.DEFLECTION).orElse(Deflection.NONE).getLevel();
	}

	protected SoundEvent getSwingSound(LivingCap<?> entitydata, Phase phase)
	{
		return phase.getProperty(AttackProperty.SWING_SOUND).orElse(entitydata.getSwingSound(phase.hand));
	}

	protected SoundEvent getHitSound(LivingCap<?> entitydata, Phase phase)
	{
		return phase.getProperty(AttackProperty.HIT_SOUND).orElse(entitydata.getWeaponHitSound(phase.hand));
	}

	protected IExtendedDamageSource getDamageSourceExt(LivingCap<?> entitydata, Entity target, Phase phase, float amount)
	{
		StunType stunType = phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.DEFAULT);
		DamageType damageType = phase.getProperty(AttackProperty.DAMAGE_TYPE).orElse(DamageType.REGULAR);
		float poiseDamage = (float) entitydata.getOriginalEntity().getAttributeValue(ModAttributes.POISE_DAMAGE.get());
		int staminaDmgMul = phase.getProperty(AttackProperty.STAMINA_DMG_MUL).orElse(1);
		IExtendedDamageSource extDmgSource = entitydata.getDamageSource(staminaDmgMul, stunType, amount, this.getRequiredDeflectionLevel(phase), damageType, poiseDamage);
		return extDmgSource;
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

	public int getIndexer(float elapsedTime)
	{
		return this.getPhaseByTime(elapsedTime).jointIndexer;
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
		protected final int jointIndexer;
		protected final InteractionHand hand;
		protected Collider collider;
		protected boolean smashed = false;

		public Phase(float antic, float preDelay, float contact, float recovery, String indexer)
		{
			this(antic, preDelay, contact, recovery, indexer, null);
		}
		
		public Phase(float antic, float preDelay, float contact, float recovery, String indexer, Collider collider)
		{
			this(antic, preDelay, contact, recovery, InteractionHand.MAIN_HAND, indexer, collider);
		}

		public Phase(float antic, float preDelay, float contact, float recovery, InteractionHand hand, String indexer, Collider collider)
		{
			this.begin = antic;
			this.contactStart = preDelay;
			this.contactEnd = contact;
			this.end = recovery;
			this.collider = collider;
			this.hand = hand;

			int coded = 0;
			if (indexer.length() == 0)
			{
				this.jointIndexer = -1;
			} else
			{
				for (int i = 0; i < indexer.length(); i++)
				{
					int value = indexer.charAt(i) - '0';
					coded = coded | value;
					coded = coded << 5;
				}
				this.jointIndexer = coded;
			}
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
		protected <V> Optional<V> getProperty(AttackProperty<V> propertyType)
		{
			return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
		}
	}
}