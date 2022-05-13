package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.ServerAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.IndirectDamageSourceExtended;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.core.util.timer.EventTimer;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public abstract class LivingCap<T extends LivingEntity> extends EntityCapability<T>
{
	public LivingMotion currentMotion = LivingMotion.IDLE;
	public LivingMotion currentMixMotion = LivingMotion.NONE;
	protected Animator animator;
	public List<Entity> currentlyAttackedEntity;
	private float poiseDef;
	private EventTimer poiseTimer = new EventTimer((past) -> poiseDef = this.getPoise());
	private float stamina;

	@Override
	public void onEntityConstructed(T entityIn)
	{
		super.onEntityConstructed(entityIn);

		this.animator = DarkSouls.getAnimator(this);
		this.animator.init();
		
		this.currentlyAttackedEntity = new ArrayList<Entity>();
	}

	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.initAttributes();
		this.poiseDef = this.getPoise();
		this.stamina = this.getMaxStamina();
	}

	public void setStamina(float value)
	{
		this.stamina = value;
	}

	public void increaseStamina(float increment)
	{
		this.setStamina(MathUtils.clamp(this.stamina + increment, -5F, this.getMaxStamina()));
	}

	public float getStamina()
	{
		return this.stamina;
	}

	public float getMaxStamina()
	{
		return (float) this.orgEntity.getAttributeValue(ModAttributes.MAX_STAMINA.get());
	}

	public float getPoiseDef()
	{
		return this.poiseDef;
	}

	public boolean decreasePoiseDef(float decr)
	{
		this.poiseDef -= decr;
		this.poiseTimer.start(5);
		return this.poiseDef <= 0.0F;
	}

	public float getPoise()
	{
		return (float) this.orgEntity.getAttributeValue(ModAttributes.POISE.get());
	}

	public float getPoiseDamage()
	{
		return (float) this.orgEntity.getAttributeValue(ModAttributes.POISE_DAMAGE.get());
	}

	@Nullable
	public StaticAnimation getHoldingWeaponAnimation()
	{
		StaticAnimation animation = this.isHoldingWeaponWithHoldingAnimation(InteractionHand.MAIN_HAND)
				? this.getHeldWeaponCapability(InteractionHand.MAIN_HAND).getHoldingAnimation()
				: null;

		return animation;
	}

	public abstract void initAnimator(ClientAnimator animatorClient);

	public abstract void updateMotion();

	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);

	protected void initAttributes()
	{
	}

	@Override
	protected void updateOnClient()
	{
		ClientAnimator animator = this.getClientAnimator();
		if (!this.isInaction()) this.updateMotion();
		animator.update();
	}

	public boolean isHoldingWeaponWithHoldingAnimation(InteractionHand hand)
	{
		WeaponCap cap = this.getHeldWeaponCapability(hand);
		return cap != null && cap.getHoldingAnimation() != null;
	}

	@Override
	public void update()
	{
		if (this.isClientSide())
		{
			this.updateOnClient();
		} else
		{
			this.updateOnServer();
		}

		if (this.orgEntity.deathTime == 19)
			this.onDeath();
	}

	@Override
	protected void updateOnServer()
	{
		this.poiseTimer.drain(1);
		this.animator.update();
	}

	protected final void commonBipedCreatureAnimatorInit(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}

	protected final void commonCreatureUpdateMotion()
	{
		if (this.orgEntity.getHealth() <= 0.0F)
		{
			currentMotion = LivingMotion.DEATH;
		} else if (orgEntity.getVehicle() != null)
		{
			currentMotion = LivingMotion.MOUNT;
		} else
		{
			if (orgEntity.getDeltaMovement().y < -0.55F)
			{
				currentMotion = LivingMotion.FALL;
			} else if (orgEntity.animationSpeed > 0.01F)
			{
				if (this.orgEntity.isSprinting())
					this.currentMotion = LivingMotion.RUNNING;
				else
					this.currentMotion = LivingMotion.WALKING;
			} else if (this.orgEntity.getUseItemRemainingTicks() > 0 && this.isBlocking())
			{
				this.currentMotion = LivingMotion.BLOCKING;
			} else
			{
				currentMotion = LivingMotion.IDLE;
			}
		}

		if (this.currentMotion == LivingMotion.BLOCKING)
			this.currentMixMotion = LivingMotion.NONE;
		else if (this.orgEntity.getUseItemRemainingTicks() > 0 && this.isBlocking())
			this.currentMixMotion = LivingMotion.BLOCKING;
	}

	public void cancelUsingItem()
	{
		this.orgEntity.stopUsingItem();
		net.minecraftforge.event.ForgeEventFactory.onUseItemStop(this.orgEntity, this.orgEntity.getUseItem(),
				this.orgEntity.getUseItemRemainingTicks());
	}

	public ItemCapability getHeldItemCapability(InteractionHand hand)
	{
		return ModCapabilities.getItemCapability(this.orgEntity.getItemInHand(hand));
	}

	public MeleeWeaponCap getHeldWeaponCapability(InteractionHand hand)
	{
		return ModCapabilities.getMeleeWeaponCap(this.orgEntity.getItemInHand(hand));
	}

	public boolean isInaction()
	{
		return this.getEntityState().isMovementLocked();
	}

	public boolean attackEntityFrom(DamageSource damageSource, float amount)
	{
		if (this.getEntityState().isInvincible())
		{
			if (damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagic())
			{
				return false;
			}
		}

		return true;
	}

	public boolean isBlocking()
	{
		EntityState entitystate = this.getEntityState();
		if (entitystate.isBlocking())
			return true;
		if (!this.orgEntity.isUsingItem() || this.orgEntity.getUseItem().isEmpty())
			return false;
		ItemStack stack = this.orgEntity.getUseItem();
		Item item = stack.getItem();
		ItemCapability shield = ModCapabilities.getItemCapability(stack);
		return item.getUseAnimation(stack) == UseAnim.BLOCK || shield instanceof IShield;
	}

	public boolean blockingAttack(IExtendedDamageSource damageSource)
	{
		if (!this.isBlocking())
			return false;
		if (damageSource == null || damageSource instanceof IndirectDamageSourceExtended)
			return true;

		IShield shield = (IShield) this.getHeldWeaponCapability(this.orgEntity.getUsedItemHand());
		Entity attacker = damageSource.getOwner();

		damageSource.setAmount(damageSource.getAmount() * (1 - shield.getPhysicalDefense()));

		if (damageSource.getRequiredDeflectionLevel() > shield.getDeflectionLevel())
			return true;

		LivingCap<?> attackerCap = (LivingCap<?>) attacker.getCapability(ModCapabilities.CAPABILITY_ENTITY, null)
				.orElse(null);
		if (attackerCap == null)
			return true;

		StaticAnimation deflectAnimation = attackerCap.getDeflectAnimation();
		if (deflectAnimation == null)
			return true;

		float stuntime = 0.0F;
		attackerCap.getAnimator().playAnimation(deflectAnimation, stuntime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(
				new STCPlayAnimation(deflectAnimation, stuntime, attackerCap), attacker);
		if (attacker instanceof ServerPlayer)
		{
			ModNetworkManager.sendToPlayer(new STCPlayAnimation(deflectAnimation, stuntime, attackerCap),
					(ServerPlayer) attacker);
		}

		return true;
	}

	public IExtendedDamageSource getDamageSource(int staminaDmgMul, StunType stunType, float amount,
			int requireddeflectionlevel, DamageType damageType, float poiseDamage)
	{
		WeaponCap weapon = ModCapabilities.getWeaponCap(this.orgEntity.getMainHandItem());
		float staminaDmg = Math.max(4, weapon.getStaminaDamage()) * staminaDmgMul;
		return IExtendedDamageSource.causeMobDamage(this.orgEntity, stunType, amount, requireddeflectionlevel,
				damageType, poiseDamage, staminaDmg);
	}

	public float getDamageToEntity(Entity targetEntity, InteractionHand hand)
	{
		float damage = (float) this.orgEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (targetEntity instanceof LivingEntity)
		{
			damage += EnchantmentHelper.getDamageBonus(this.orgEntity.getItemInHand(hand),
					((LivingEntity) targetEntity).getMobType());
		} else
		{
			damage += EnchantmentHelper.getDamageBonus(this.orgEntity.getItemInHand(hand), MobType.UNDEFINED);
		}

		return damage;
	}

	public boolean hurtEntity(Entity hitTarget, InteractionHand handIn, IExtendedDamageSource source, float amount)
	{
		boolean succed = hitTarget.hurt((DamageSource) source, amount);

		if (succed)
		{
			int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT,
					this.orgEntity.getItemInHand(handIn));
			if (hitTarget instanceof LivingEntity)
			{
				if (j > 0 && !hitTarget.isOnFire())
					hitTarget.setSecondsOnFire(j * 4);
			}
		}

		return succed;
	}

	public StaticAnimation getDeflectAnimation()
	{
		return null;
	}

	public void gatherDamageDealt(IExtendedDamageSource source, float amount)
	{
	}

	public void knockBackEntity(Entity entityIn, float power)
	{
		power *= 0.1D;

		double d1 = entityIn.getX() - this.orgEntity.getX();
		double d0;

		for (d0 = entityIn.getZ() - this.orgEntity.getZ(); d1 * d1
				+ d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
		{
			d1 = (Math.random() - Math.random()) * 0.01D;
		}

		if (orgEntity.getRandom().nextDouble() >= orgEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))
		{
			Vec3 vec = orgEntity.getDeltaMovement();

			orgEntity.hasImpulse = true;
			float f = (float) Math.sqrt(d1 * d1 + d0 * d0);

			double x = vec.x;
			double y = vec.y;
			double z = vec.z;

			x /= 2.0D;
			z /= 2.0D;
			x -= d1 / (double) f * (double) power;
			z -= d0 / (double) f * (double) power;

			if (!orgEntity.isOnGround())
			{
				y /= 2.0D;
				y += (double) power;

				if (y > 0.4000000059604645D)
				{
					y = 0.4000000059604645D;
				}
			}

			orgEntity.setDeltaMovement(x, y, z);
		}
	}

	public void rotateTo(float degree, float limit, boolean partialSync)
	{
		LivingEntity entity = this.getOriginalEntity();
		float amount = Mth.wrapDegrees(degree - entity.yRot);

		while (amount < -180.0F)
		{
			amount += 360.0F;
		}

		while (amount > 180.0F)
		{
			amount -= 360.0F;
		}

		if (amount > limit)
		{
			amount = limit;
		}
		if (amount < -limit)
		{
			amount = -limit;
		}

		float f1 = entity.yRot + amount;

		if (partialSync)
		{
			entity.yRotO = f1;
			entity.yHeadRotO = f1;
			entity.yBodyRotO = f1;
		}

		entity.yRot = f1;
		entity.yHeadRot = f1;
		entity.yBodyRot = f1;
	}

	public void rotateTo(Entity target, float limit, boolean partialSync)
	{
		double d0 = target.getX() - this.orgEntity.getX();
		double d1 = target.getZ() - this.orgEntity.getZ();
		float degree = (float) (Math.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
		this.rotateTo(degree, limit, partialSync);
	}
	
	public void playSound(SoundEvent sound)
	{
		this.playSound(sound, 1.0F, 1.0F);
	}

	public void playSound(SoundEvent sound, float minPitch, float maxPitch)
	{
		this.playSound(sound, minPitch, maxPitch, 1.0F);
	}

	public void playSound(SoundEvent sound, float minPitch, float maxPitch, float volume)
	{
		float randPitch = this.orgEntity.getRandom().nextFloat() * 2.0F - 1.0F;
		randPitch = Math.min(Math.max(randPitch, minPitch), maxPitch);
		if (!this.isClientSide())
		{
			this.orgEntity.level.playSound(null, orgEntity.getX(), orgEntity.getY(), orgEntity.getZ(), sound,
					orgEntity.getSoundSource(), volume, 1.0F + randPitch);
		} else
		{
			this.orgEntity.level.playSound(null, orgEntity.getX(), orgEntity.getY(), orgEntity.getZ(), sound,
					orgEntity.getSoundSource(), volume, 1.0F + randPitch);
		}
	}

	public LivingEntity getTarget()
	{
		return this.orgEntity.getLastHurtMob();
	}

	public float getAttackDirectionPitch()
	{
		float partialTicks = DarkSouls.isPhysicalClient() ? Minecraft.getInstance().getFrameTime() : 1.0F;
		float pitch = -this.getOriginalEntity().getViewXRot(partialTicks);
		float correct = (pitch > 0) ? 0.03333F * (float) Math.pow(pitch, 2) : -0.03333F * (float) Math.pow(pitch, 2);

		return MathUtils.clamp(correct, -30.0F, 30.0F);
	}

	public PublicMatrix4f getHeadMatrix(float partialTicks)
	{
		float bodyRot;
		float headRot;
		float headRotDest;

		if (this.isInaction())
			headRotDest = 0;
		else
		{
			bodyRot = MathUtils.interpolateRotation(orgEntity.yBodyRotO, orgEntity.yBodyRot, partialTicks);
			headRot = MathUtils.interpolateRotation(orgEntity.yHeadRotO, orgEntity.yHeadRot, partialTicks);
			headRotDest = headRot - bodyRot;

			if (orgEntity.getControllingPassenger() != null)
			{
				headRotDest = MathUtils.clamp(headRotDest, 45.0F);
			}
		}

		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.xRotO, orgEntity.xRot, headRotDest,
				headRotDest, partialTicks, 1, 1, 1);
	}

	@Override
	public PublicMatrix4f getModelMatrix(float partialTicks)
	{
		float prevRotYaw;
		float rotyaw;
		float scaleX = 1.0F;
		float scaleY = 1.0F;
		float scaleZ = 1.0F;

		if (orgEntity.getControllingPassenger() instanceof LivingEntity)
		{
			LivingEntity ridingEntity = (LivingEntity) orgEntity.getControllingPassenger();
			prevRotYaw = ridingEntity.yBodyRotO;
			rotyaw = ridingEntity.yBodyRot;
		} else
		{
			prevRotYaw = (this.isInaction() ? orgEntity.yRot : orgEntity.yBodyRotO);
			rotyaw = (this.isInaction() ? orgEntity.yRot : orgEntity.yBodyRot);
		}

		if (this.orgEntity.isBaby())
		{
			scaleX *= 0.5F;
			scaleY *= 0.5F;
			scaleZ *= 0.5F;
		}

		return PublicMatrix4f.getModelMatrixIntegrated((float) orgEntity.xOld, (float) orgEntity.getX(),
				(float) orgEntity.yOld, (float) orgEntity.getY(), (float) orgEntity.zOld, (float) orgEntity.getZ(), 0,
				0, prevRotYaw, rotyaw, partialTicks, scaleX, scaleY, scaleZ);
	}

	public void reserveAnimation(StaticAnimation animation)
	{
		this.animator.reserveAnimation(animation);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(
				new STCPlayAnimation(animation, this.orgEntity.getId(), 0.0F), this.orgEntity);
	}

	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier)
	{
		this.playAnimationSynchronized(animation, convertTimeModifier, STCPlayAnimation::new);
	}

	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier,
			AnimationPacketProvider packetProvider)
	{
		this.animator.playAnimation(animation, convertTimeModifier);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(packetProvider.get(animation, convertTimeModifier, this),
				this.orgEntity);
	}

	@FunctionalInterface
	public static interface AnimationPacketProvider
	{
		public STCPlayAnimation get(StaticAnimation animation, float convertTimeModifier, LivingCap<?> entityCap);
	}

	protected void playReboundAnimation()
	{
		this.getClientAnimator().playReboundAnimation();
	}

	public void onArmorSlotChanged(AttributeItemCap fromCap, AttributeItemCap toCap, EquipmentSlot slotType)
	{
	}

	@SuppressWarnings("unchecked")
	public <A extends Animator> A getAnimator()
	{
		return (A) this.animator;
	}

	public ClientAnimator getClientAnimator()
	{
		return this.<ClientAnimator>getAnimator();
	}

	public ServerAnimator getServerAnimator()
	{
		return this.<ServerAnimator>getAnimator();
	}

	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return null;
	}

	public void onDeath()
	{
		this.getAnimator().playDeathAnimation();
	}

	@Override
	public T getOriginalEntity()
	{
		return orgEntity;
	}

	public SoundEvent getWeaponHitSound(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldWeaponCapability(hand);
		if (cap == null)
			return null;
		return cap.getHitSound();
	}

	public SoundEvent getWeaponSmashSound(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldWeaponCapability(hand);
		if (cap == null)
			return null;
		return cap.getSmashSound();
	}

	public SoundEvent getSwingSound(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldWeaponCapability(hand);
		if (cap == null)
			return ModSoundEvents.FIST_SWING.get();
		return cap.getSwingSound();
	}

	public Collider getColliderMatching(InteractionHand hand)
	{
		MeleeWeaponCap cap = this.getHeldWeaponCapability(hand);
		return cap != null ? cap.getWeaponCollider() : Colliders.FIST;
	}

	public boolean isTeam(Entity entityIn)
	{
		if (orgEntity.getControllingPassenger() != null && orgEntity.getControllingPassenger().equals(entityIn))
			return true;
		else if (this.isMountedTeam(entityIn))
			return true;

		return this.orgEntity.isAlliedTo(entityIn);
	}

	private boolean isMountedTeam(Entity entityIn)
	{
		LivingEntity orgEntity = this.getOriginalEntity();
		for (Entity passanger : orgEntity.getPassengers())
		{
			if (passanger.equals(entityIn))
				return true;
		}

		for (Entity passanger : entityIn.getPassengers())
		{
			if (passanger.equals(orgEntity))
				return true;
		}

		return false;
	}

	public boolean isFirstPerson()
	{
		return false;
	}

	public EntityState getEntityState()
	{
		return this.animator.getEntityState();
	}
}