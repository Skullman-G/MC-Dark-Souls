package com.skullmangames.darksouls.common.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.capability.entity.BipedMobData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.MobData;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.item.IShield.Deflection;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.AttackResult;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.Property.AttackProperty;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.PartEntity;

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

	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand,
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
	public void onUpdate(LivingData<?> entitydata)
	{
		super.onUpdate(entitydata);

		if (entitydata.isClientSide())
			return;

		float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
		float prevElapsedTime = entitydata.getAnimator().getPlayer().getPrevElapsedTime();
		LivingData.EntityState state = this.getState(elapsedTime);
		LivingData.EntityState prevState = this.getState(prevElapsedTime);
		Phase phase = this.getPhaseByTime(elapsedTime);
		LivingEntity entity = entitydata.getOriginalEntity();

		if (state == LivingData.EntityState.FREE_CAMERA)
		{
			if (entitydata instanceof MobData)
			{
				((MobEntity) entitydata.getOriginalEntity()).getNavigation().stop();
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
				entitydata.playSound(this.getSwingSound(entitydata, phase), 0.0F, 0.0F);
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
									.clip(new RayTraceContext(new Vector3d(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()),
											new Vector3d(entity.getX(), entity.getY() + entity.getBbHeight() * 0.5F, entity.getZ()),
											RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity))
									.getType() == RayTraceResult.Type.MISS)
							{

								float amount = this.getDamageAmount(entitydata, e, phase);
								IExtendedDamageSource source = this.getDamageSourceExt(entitydata, e, phase, amount);
								if (entitydata.hurtEntity(e, phase.hand, source, amount))
								{
									e.invulnerableTime = 0;
									e.level.playSound(null, e.getX(), e.getY(), e.getZ(), this.getHitSound(entitydata, phase), e.getSoundSource(),
											1.0F, 1.0F);
									if (flag1 && entitydata instanceof PlayerData && trueEntity instanceof LivingEntity)
									{
										entitydata.getOriginalEntity().getItemInHand(phase.hand).hurtEnemy((LivingEntity) trueEntity,
												((PlayerData<?>) entitydata).getOriginalEntity());
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

	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		super.onFinish(entitydata, isEnd);

		if (entitydata instanceof PlayerData)
		{
			((PlayerData<?>) entitydata).getEventListener().activateEvents(EventType.ON_ATTACK_END_EVENT, entitydata.currentlyAttackedEntity.size(),
					this.getId());
		}

		entitydata.currentlyAttackedEntity.clear();

		if (entitydata instanceof BipedMobData && entitydata.isClientSide())
		{
			MobEntity entity = (MobEntity) entitydata.getOriginalEntity();
			if (entity.getTarget() != null && !entity.getTarget().isAlive())
				entity.setTarget((LivingEntity) null);
		}

		for (Phase phase : this.phases)
			phase.smashed = false;
	}

	@Override
	public LivingData.EntityState getState(float time)
	{
		Phase phase = this.getPhaseByTime(time);

		if (time <= phase.antic || (phase.antic < time && time < phase.preDelay))
		{
			return LivingData.EntityState.FREE_CAMERA;
		} else if (phase.preDelay <= time && time <= phase.contact)
		{
			return LivingData.EntityState.CONTACT;
		} else if (time < phase.recovery)
		{
			return LivingData.EntityState.POST_DELAY;
		} else
		{
			return LivingData.EntityState.FREE_INPUT;
		}
	}

	public Collider getCollider(LivingData<?> entitydata, float elapsedTime)
	{
		Phase phase = this.getPhaseByTime(elapsedTime);
		return phase.collider != null ? phase.collider : entitydata.getColliderMatching(phase.hand);
	}
	
	@Override
	public AttackAnimation registerSound(SoundEvent sound, float time, boolean isRemote)
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

	protected float getDamageAmount(LivingData<?> entitydata, Entity target, Phase phase)
	{
		return entitydata.getDamageToEntity(target, phase.hand);
	}

	protected int getRequiredDeflectionLevel(Phase phase)
	{
		return phase.getProperty(AttackProperty.DEFLECTION).orElse(Deflection.NONE).getLevel();
	}

	protected SoundEvent getSwingSound(LivingData<?> entitydata, Phase phase)
	{
		return phase.getProperty(AttackProperty.SWING_SOUND).orElse(entitydata.getSwingSound(phase.hand));
	}

	protected SoundEvent getHitSound(LivingData<?> entitydata, Phase phase)
	{
		return phase.getProperty(AttackProperty.HIT_SOUND).orElse(entitydata.getWeaponHitSound(phase.hand));
	}

	protected IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target, Phase phase, float amount)
	{
		StunType stunType = phase.getProperty(AttackProperty.STUN_TYPE).orElse(StunType.SHORT);
		DamageType damageType = phase.getProperty(AttackProperty.DAMAGE_TYPE).orElse(DamageType.STANDARD);
		IExtendedDamageSource extDmgSource = entitydata.getDamageSource(stunType, this.getId(), amount, this.getRequiredDeflectionLevel(phase), damageType);
		
		phase.getProperty(AttackProperty.IMPACT).ifPresent((opt) ->
		{
			extDmgSource.setImpact(opt.get(extDmgSource.getImpact()));
		});

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
			if (phase.recovery > elapsedTime)
			{
				break;
			}
		}
		return currentPhase;
	}

	public static class Phase
	{
		protected final Map<AttackProperty<?>, Object> properties = new HashMap<AttackProperty<?>, Object>();;
		protected final float antic;
		protected final float preDelay;
		protected final float contact;
		protected final float recovery;
		protected final int jointIndexer;
		protected final Hand hand;
		protected Collider collider;
		protected boolean smashed = false;

		public Phase(float antic, float preDelay, float contact, float recovery, String indexer)
		{
			this(antic, preDelay, contact, recovery, indexer, null);
		}
		
		public Phase(float antic, float preDelay, float contact, float recovery, String indexer, Collider collider)
		{
			this(antic, preDelay, contact, recovery, Hand.MAIN_HAND, indexer, collider);
		}

		public Phase(float antic, float preDelay, float contact, float recovery, Hand hand, String indexer, Collider collider)
		{
			this.antic = antic;
			this.preDelay = preDelay;
			this.contact = contact;
			this.recovery = recovery;
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