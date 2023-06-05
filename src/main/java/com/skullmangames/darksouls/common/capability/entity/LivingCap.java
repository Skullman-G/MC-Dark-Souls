package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.ServerAnimator;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.Property.AttackProperty;
import com.skullmangames.darksouls.common.animation.types.DeathAnimation;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.common.capability.item.SpellcasterWeaponCap;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModParticles;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.Damage;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.core.util.timer.EventTimer;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCEntityImpactParticles;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class LivingCap<T extends LivingEntity> extends EntityCapability<T>
{
	public LivingMotion currentMotion = LivingMotion.IDLE;
	public Map<LayerPart, LivingMotion> currentMixMotions = new HashMap<>();
	protected Animator animator;
	public List<Entity> currentlyAttackedEntities;
	private float poiseDef;
	private EventTimer poiseTimer = new EventTimer((past) -> this.poiseDef = this.getPoise());
	private float stamina;
	public Vec3 futureTeleport = Vec3.ZERO;
	public int slashDelay;
	public Entity criticalTarget;

	@Override
	public void onEntityConstructed(T entityIn)
	{
		super.onEntityConstructed(entityIn);
		for (LayerPart part : LayerPart.mixLayers()) this.currentMixMotions.put(part, LivingMotion.NONE);
		this.animator = DarkSouls.getAnimator(this);
		this.animator.init();
		this.currentlyAttackedEntities = new ArrayList<Entity>();
	}

	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.initAttributes();
		this.poiseDef = this.getPoise();
		this.stamina = this.getMaxStamina();
	}
	
	public float getSpellBuff()
	{
		return (float)this.orgEntity.getAttributeValue(ModAttributes.SPELL_BUFF.get());
	}
	
	public void onHeldItemChange(ItemCapability toChange, ItemStack stack, InteractionHand hand)
	{
		this.cancelUsingItem();
	}
	
	public boolean isMounted()
	{
		return this.orgEntity.getVehicle() != null;
	}

	public void setStamina(float value)
	{
		this.stamina = value;
	}

	public void increaseStamina(float increment)
	{
		this.setStamina(MathUtils.clamp(this.stamina + increment, -5, this.getMaxStamina()));
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
		this.poiseDef = Math.max(0, this.poiseDef - decr);
		this.poiseTimer.start(10);
		return this.poiseDef <= 0.0F;
	}

	public float getPoise()
	{
		return (float) this.orgEntity.getAttributeValue(ModAttributes.POISE.get());
	}
	
	public boolean isTwohanding()
	{
		return false;
	}

	public abstract void initAnimator(ClientAnimator animatorClient);

	public abstract void updateMotion();

	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);

	protected void initAttributes() {}

	@Override
	protected void updateOnClient()
	{
		if (!this.isInaction()) this.updateMotion();
		this.animator.update();
	}

	public boolean isHoldingWeaponWithHoldingAnimation(InteractionHand hand)
	{
		WeaponCap cap = this.getHeldWeaponCapability(hand);
		return cap != null && cap.hasHoldingAnimation();
	}

	@Override
	public void update()
	{
		if (this.isClientSide())
		{
			this.updateOnClient();
		}
		else
		{
			this.updateOnServer();
		}
	}
	
	public void makeImpactParticles(Vec3 impactPos, boolean blocked)
	{
		if (this.isClientSide())
		{
			AABB bb = this.orgEntity.getBoundingBox();
			double x = this.getX() + MathUtils.clamp(impactPos.x - this.getX(), bb.getXsize() / 3);
			double y = this.getY() + MathUtils.clamp(impactPos.y - this.getY(), bb.getYsize() / 3);
			double z = this.getZ() + MathUtils.clamp(impactPos.z - this.getZ(), bb.getZsize() / 3);
			
			if (blocked)
			{
				for (int i = 0; i < 10; i++)
				{
					double xd = MathUtils.dir(impactPos.x - this.getX()) * 0.25F * this.orgEntity.getRandom().nextDouble();
					double zd = MathUtils.dir(impactPos.z - this.getZ()) * 0.25F * this.orgEntity.getRandom().nextDouble();
					this.getLevel().addParticle(ModParticles.SPARK.get(), x, y, z, xd, 0.2D * this.orgEntity.getRandom().nextDouble(), zd);
				}
			}
			else
			{
				for (int i = 0; i < 20; i++)
				{
					double xd = MathUtils.dir(impactPos.x - this.getX()) * 0.5F * this.orgEntity.getRandom().nextDouble();
					double zd = MathUtils.dir(impactPos.z - this.getZ()) * 0.5F * this.orgEntity.getRandom().nextDouble();
					this.getLevel().addParticle(ModParticles.BLOOD.get(), x, y, z, xd, 0.2D, zd);
				}
			}
		}
		else
		{
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCEntityImpactParticles(this.orgEntity.getId(), impactPos, blocked), this.orgEntity);
		}
	}
	
	public boolean canBackstab(Entity target)
	{
		if (!(target instanceof LivingEntity)) return false;
		float attackAngle = ((float)Math.toDegrees(Math.atan2(target.getX() - this.orgEntity.getX(), target.getZ() - this.orgEntity.getZ())) + 360F) % 360F;
		float yRotTarget = target.getYRot() - 180;
		if (yRotTarget < -180) yRotTarget += 360F;
		float angleBetween = Math.abs(-yRotTarget - attackAngle);
		LivingCap<?> cap = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		return cap != null && (cap instanceof HumanoidCap || cap instanceof PlayerCap) && !cap.getEntityState().isInvincible() && angleBetween <= 225 && angleBetween >= 135;
	}

	@Override
	protected void updateOnServer()
	{
		this.poiseTimer.drain(1);
		this.animator.update();
	}

	protected final void commonBipedAnimatorInit(ClientAnimator animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNTED, Animations.BIPED_HORSEBACK_IDLE);
	}

	protected final void commonMotionUpdate()
	{
		if (this.isMounted())
		{
			this.currentMotion = LivingMotion.MOUNTED;
		}
		else
		{
			if (this.orgEntity.getDeltaMovement().y < -0.55F)
			{
				currentMotion = LivingMotion.FALL;
			}
			else if (this.orgEntity.animationSpeed > 0.01F)
			{
				if (this.orgEntity.isSprinting())
					this.currentMotion = LivingMotion.RUNNING;
				else
					this.currentMotion = LivingMotion.WALKING;
			}
			else if (this.orgEntity.getUseItemRemainingTicks() > 0 && this.isBlocking())
			{
				this.currentMotion = LivingMotion.BLOCKING;
			}
			else
			{
				currentMotion = LivingMotion.IDLE;
			}
		}
		
		boolean leftChanged = false;
		boolean rightChanged = false;
		
		if (this.orgEntity.getUseItemRemainingTicks() != 0)
		{
			InteractionHand hand = this.orgEntity.getUsedItemHand();
			LayerPart layerPart = hand == InteractionHand.MAIN_HAND ? LayerPart.RIGHT : LayerPart.LEFT;
			if (this.currentMotion != LivingMotion.BLOCKING && this.isBlocking()) this.currentMixMotions.put(layerPart, LivingMotion.BLOCKING);
			else
			{
				UseAnim useAction = this.orgEntity.getItemInHand(this.orgEntity.getUsedItemHand()).getUseAnimation();
				switch (useAction)
				{
					case BOW:
						this.currentMixMotions.put(layerPart, LivingMotion.AIMING);
						break;
						
					case CROSSBOW:
						this.currentMixMotions.put(layerPart, LivingMotion.RELOADING);
						break;
						
					case SPEAR:
						this.currentMixMotions.put(layerPart, LivingMotion.AIMING);
						break;
						
					case DRINK:
						this.currentMixMotions.put(layerPart, LivingMotion.DRINKING);
						break;
						
					case EAT:
						this.currentMixMotions.put(layerPart, LivingMotion.EATING);
						break;
						
					default:
						this.currentMixMotions.put(layerPart, LivingMotion.NONE);
						break;
				}
			}
			
			if (layerPart == LayerPart.LEFT) leftChanged = true;
			else rightChanged = true;
		}
		
		if (CrossbowItem.isCharged(this.orgEntity.getMainHandItem()))
		{
			this.currentMixMotions.put(LayerPart.UP, LivingMotion.AIMING);
			leftChanged = true;
			rightChanged = true;
		}
		
		if (!leftChanged) this.currentMixMotions.put(LayerPart.LEFT, LivingMotion.NONE);
		if (!rightChanged) this.currentMixMotions.put(LayerPart.RIGHT, LivingMotion.NONE);
		if (!leftChanged && !rightChanged) this.currentMixMotions.put(LayerPart.UP, LivingMotion.NONE);
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
	
	public SpellcasterWeaponCap getHeldSpellcasterWeaponCap(InteractionHand hand)
	{
		return ModCapabilities.getSpellcasterWeaponCap(this.orgEntity.getItemInHand(hand));
	}

	public MeleeWeaponCap getHeldWeaponCapability(InteractionHand hand)
	{
		return ModCapabilities.getMeleeWeaponCap(this.orgEntity.getItemInHand(hand));
	}

	public boolean isInaction()
	{
		return this.getEntityState().isMovementLocked();
	}
	
	public boolean canBlock()
	{
		return !this.isMounted();
	}
	
	public void onDeath() {}

	public boolean onHurt(DamageSource damageSource, float amount)
	{
		ExtendedDamageSource extSource = ExtendedDamageSource.getFrom(damageSource, amount);
		DamageType damageType = extSource.getDamages()[0].getType();
		
		if (this.getEntityState().isInvincible() && damageType != DamageType.CRITICAL)
		{
			if (damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagic())
			{
				return false;
			}
		}
		
		boolean indirect = damageSource instanceof IndirectEntityDamageSource;
		
		// Damage Calculation
		if (!indirect)
		{
			for (Damage damage : extSource.getDamages())
			{
				Attribute defAttribute = damage.getType().getDefenseAttribute();
				if (this.orgEntity.getAttribute(defAttribute) != null)
				{
					damage.setAmount(Math.max(damage.getAmount() - (float)this.orgEntity.getAttributeValue(defAttribute), damage.getAmount() * 0.5F));
				}
			}
		}
		if (this.blockingAttack(extSource))
		{
			this.orgEntity.actuallyHurt((DamageSource)extSource, extSource.getAmount());
			return false;
		}

		return true;
	}
	
	public void onActuallyHurt(DamageSource damageSource)
	{
		ExtendedDamageSource extSource = ExtendedDamageSource.getFrom(damageSource, 0);
		boolean headshot = extSource.isHeadshot();
		float poiseDamage = extSource.getPoiseDamage();
		StunType stunType = extSource.getStunType();
		
		this.makeImpactParticles(extSource.getAttackPos(), extSource.wasBlocked());
		
		// Stun Animation
		boolean poiseBroken = this.decreasePoiseDef(poiseDamage);
		if (!poiseBroken && !headshot) extSource.setStunType(stunType.downgrade());
		StaticAnimation hitAnimation = this.getHitAnimation(extSource);
		
		if(hitAnimation != null)
		{
			if (stunType.getLevel() == 3)
			{
				if (this.isMounted()) this.orgEntity.stopRiding();
			}
			this.playAnimationSynchronized(hitAnimation, 0.0F);
		}
	}

	public boolean isBlocking()
	{
		if (this.animator.getMainPlayer().getPlay().getPropertyByTime(AttackProperty.BLOCKING, this.animator.getMainPlayer().getElapsedTime()).orElse(false)) return true;
		if (!this.orgEntity.isUsingItem() || this.orgEntity.getUseItem().isEmpty()) return false;
		ItemStack stack = this.orgEntity.getUseItem();
		Item item = stack.getItem();
		ItemCapability shield = ModCapabilities.getItemCapability(stack);
		return item.getUseAnimation(stack) == UseAnim.BLOCK || shield instanceof IShield;
	}

	public boolean blockingAttack(ExtendedDamageSource damageSource)
	{
		if (!this.isBlocking()) return false;

		IShield shield = (IShield)this.getHeldWeaponCapability(this.orgEntity.getUsedItemHand());
		Entity attacker = damageSource.getOwner();
		if (attacker == null) return false;
		
		for (Damage damage : damageSource.getDamages())
		{
			damage.setAmount(damage.getAmount() * (1 - shield.getDefense(damage.getType().getCoreType())));
		}
		
		damageSource.setWasBlocked(true);
		
		this.playSound(shield.getBlockSound());

		if (attacker != null && damageSource.getRequiredDeflectionLevel() <= shield.getDeflectionLevel() && !damageSource.isIndirect())
		{
			LivingCap<?> attackerCap = (LivingCap<?>) attacker.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (attackerCap == null) return true;

			StaticAnimation deflectAnimation = attackerCap.getDeflectAnimation();
			if (deflectAnimation == null) return true;

			attackerCap.playAnimationSynchronized(deflectAnimation, 0.0F);
		}
		
		return true;
	}

	public ExtendedDamageSource getDamageSource(Vec3 attackPos, int staminaDamage, StunType stunType, float amount,
			int requireddeflectionlevel, DamageType damageType, float poiseDamage)
	{
		return ExtendedDamageSource.causeMobDamage(this.orgEntity, attackPos, stunType, requireddeflectionlevel, poiseDamage, staminaDamage, new Damage(damageType, amount));
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

	public boolean hurtEntity(Entity hitTarget, InteractionHand handIn, ExtendedDamageSource source, float amount)
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
	
	public DeathAnimation getDeathAnimation(ExtendedDamageSource dmgSource)
	{
		return null;
	}

	public void gatherDamageDealt(ExtendedDamageSource source, float amount) {}

	public void knockBackEntity(Entity entity, float power)
	{
		this.knockBackEntity(entity, power, power, power);
	}
	
	public void knockBackEntity(Entity entity, float powerX, float powerY, float powerZ)
	{
		double dx = this.orgEntity.getX() - entity.getX();
		double dz = this.orgEntity.getZ() - entity.getZ();

		this.knockback(entity, dx, dz, powerX, powerY, powerZ);
	}
	
	public void knockBackFromEntity(Entity entity, float powerX, float powerY, float powerZ)
	{
		double dx = entity.getX() - this.orgEntity.getX();
		double dz = entity.getZ() - this.orgEntity.getZ();

		this.knockback(this.orgEntity, dx, dz, powerX, powerY, powerZ);
	}
	
	private void knockback(Entity entity, double dx, double dz, float powerX, float powerY, float powerZ)
	{
		Vec3 vec = entity.getDeltaMovement();

		entity.hasImpulse = true;
		float f = (float) Math.sqrt(dx * dx + dz * dz);

		double x = vec.x;
		double y = vec.y;
		double z = vec.z;

		x /= 2.0D;
		z /= 2.0D;
		x -= dx / (double) f * (double) powerX;
		z -= dz / (double) f * (double) powerZ;

		if (entity.isOnGround())
		{
			y /= 2.0D;
			y += (double) powerY / 2;

			if (y > 0.4000000059604645D)
			{
				y = 0.4000000059604645D;
			}
		}

		entity.setDeltaMovement(x, y, z);
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
		double dx = target.getX() - this.orgEntity.getX();
		double dz = target.getZ() - this.orgEntity.getZ();
		float degree = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
		this.rotateTo(degree, limit, partialSync);
	}
	
	public void playSound(SoundEvent sound)
	{
		this.playSound(sound, 1.0F, false);
	}

	public void playSound(SoundEvent sound, boolean moves)
	{
		this.playSound(sound, 1.0F, moves);
	}

	public void playSound(SoundEvent sound, float volume, boolean moves)
	{
		if (this.isClientSide())
		{
			if (moves) ModNetworkManager.connection.playEntitySound(this.orgEntity, sound, volume);
			else ModNetworkManager.connection.playSound(this.orgEntity, sound, volume);
		}
		else
		{
			if (moves) this.getLevel().playSound(null, this.orgEntity, sound, this.orgEntity.getSoundSource(), volume, 1.0F);
			else this.getLevel().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.orgEntity.getSoundSource(), volume, 1.0F);
		}
	}

	public LivingEntity getTarget()
	{
		return this.orgEntity.getLastHurtMob();
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

	public void onArmorSlotChanged(AttributeItemCap fromCap, AttributeItemCap toCap, EquipmentSlot slotType) {}

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

	public StaticAnimation getHitAnimation(ExtendedDamageSource dmgSource)
	{
		return null;
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