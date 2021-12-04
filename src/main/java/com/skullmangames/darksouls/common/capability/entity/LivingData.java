package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.AnimatorServer;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.common.entity.DataKeys;
import com.skullmangames.darksouls.common.item.WeaponItem;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.IndirectDamageSourceExtended;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.common.animation.types.HoldingWeaponAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public abstract class LivingData<T extends LivingEntity> extends EntityData<T>
{
	private float stunTimeReduction;
	protected boolean inaction;
	public LivingMotion currentMotion = LivingMotion.IDLE;
	public LivingMotion currentMixMotion = LivingMotion.NONE;
	protected Animator animator;
	public List<Entity> currentlyAttackedEntity;
	
	@Override
	public void onEntityConstructed(T entityIn)
	{
		super.onEntityConstructed(entityIn);
		if(this.orgEntity.level.isClientSide)
		{
			this.animator = new AnimatorClient(this);
			this.initAnimator(this.getClientAnimator());
		}
		else
		{
			this.animator = new AnimatorServer(this);
		}
		
		this.inaction = false;
		this.currentlyAttackedEntity = new ArrayList<Entity>();
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.initAttributes();
		this.orgEntity.pushthrough = 1.0F;
	}
	
	@Nullable
	public StaticAnimation getHoldingWeaponAnimation()
	{
		StaticAnimation animation = null;
		if (this.isHoldingWeaponWithHoldingAnimation(Hand.OFF_HAND))
		{
			animation = this.getHeldWeaponCapability(Hand.OFF_HAND).getHoldingAnimation();
			if (this.isHoldingWeaponWithHoldingAnimation(Hand.MAIN_HAND)) animation = ((HoldingWeaponAnimation)animation).getAnimation(2);
			else animation = ((HoldingWeaponAnimation)animation).getAnimation(1);
		}
		else if (this.isHoldingWeaponWithHoldingAnimation(Hand.MAIN_HAND))
		{
			animation = this.getHeldWeaponCapability(Hand.MAIN_HAND).getHoldingAnimation().getAnimation(0);
		}
		
		return animation;
	}
	
	protected abstract void initAnimator(AnimatorClient animatorClient);
	public abstract void updateMotion();
	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);
	
	protected void initAttributes()
	{
		this.orgEntity.getAttribute(ModAttributes.WEIGHT.get()).setBaseValue(this.orgEntity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(0.5D);
	}
	
	@Override
	protected void updateOnClient()
	{
		AnimatorClient animator = getClientAnimator();
		
		if(this.inaction)
		{
			this.currentMotion = LivingMotion.IDLE;
		}
		else
		{
			this.updateMotion();
			if(!animator.compareMotion(currentMotion))
			{
				animator.playLoopMotion();
			}
			if(!animator.compareMixMotion(currentMixMotion))
			{
				animator.playMixLoopMotion();
			}
		}
	}
	
	public boolean isHoldingWeaponWithHoldingAnimation(Hand hand)
	{
		WeaponCapability cap = this.getHeldWeaponCapability(hand);
		return cap != null && cap.getHoldingAnimation() != null;
	}
	
	@Override
	protected void updateOnServer()
	{
		if(stunTimeReduction > 0.0F) stunTimeReduction = Math.max(0.0F, stunTimeReduction - 0.05F);
	}
	
	@Override
	public void update()
	{
		this.updateInactionState();

		if (this.isClientSide())
		{
			this.updateOnClient();
		}
		else
		{
			this.updateOnServer();
		}

		this.animator.update();
		if (this.orgEntity.deathTime == 19) this.aboutToDeath();
	}

	public void updateInactionState()
	{
		EntityState state = this.getEntityState();
		this.inaction = state.isMovementLocked();
	}

	protected final void commonBipedCreatureAnimatorInit(AnimatorClient animatorClient)
	{
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	protected final void commonCreatureUpdateMotion()
	{
		if(this.orgEntity.getHealth() <= 0.0F)
		{
			currentMotion = LivingMotion.DEATH;
		}
		else if(orgEntity.getVehicle() != null)
		{
			currentMotion = LivingMotion.MOUNT;
		}
		else
		{
			if(orgEntity.getDeltaMovement().y < -0.55F)
			{
				currentMotion = LivingMotion.FALL;
			}
			else if(orgEntity.animationSpeed > 0.01F)
			{
				currentMotion = LivingMotion.WALKING;
			}
			else
			{
				currentMotion = LivingMotion.IDLE;
			}
		}
	}
	
	public void cancelUsingItem()
	{
		this.orgEntity.stopUsingItem();
		net.minecraftforge.event.ForgeEventFactory.onUseItemStop(this.orgEntity, this.orgEntity.getUseItem(), this.orgEntity.getUseItemRemainingTicks());
	}
	
	public CapabilityItem getHeldItemCapability(Hand hand)
	{
		return ModCapabilities.stackCapabilityGetter(this.orgEntity.getItemInHand(hand));
	}
	
	public WeaponCapability getHeldWeaponCapability(Hand hand)
	{
		return ModCapabilities.stackWeaponCapabilityGetter(this.orgEntity.getItemInHand(hand));
	}

	public boolean isInaction()
	{
		return this.inaction;
	}
	
	public boolean attackEntityFrom(DamageSource damageSource, float amount)
	{
		if(this.getEntityState().isInvincible())
		{
			if(damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagic())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean blockingAttack(IExtendedDamageSource damageSource)
	{
		if (!this.orgEntity.isBlocking() || damageSource == null || damageSource instanceof IndirectDamageSourceExtended) return false;
		
		WeaponCapability weaponCap = this.getHeldWeaponCapability(this.orgEntity.getUsedItemHand());
		if (!(weaponCap instanceof IShield)) return false;
		
		IShield shield = (IShield)weaponCap;
		Entity attacker = damageSource.getOwner();
		
		float health = this.orgEntity.getHealth() - (damageSource.getAmount() * (1 - shield.getPhysicalDefense()));
		if (health < 0) health = 0;
		this.orgEntity.setHealth(health);
		
		if (damageSource.getRequiredDeflectionLevel() > shield.getDeflectionLevel()) return false;

		LivingData<?> attackerData = (LivingData<?>)attacker.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (attackerData == null) return false;
		
		StaticAnimation deflectAnimation = attackerData.getDeflectAnimation();
		if (deflectAnimation == null) return false;
		
		float stuntime = 0.0F;
		attackerData.setStunTimeReduction();
		attackerData.getAnimator().playAnimation(deflectAnimation, stuntime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(deflectAnimation.getId(), attacker.getId(), stuntime), attacker);
		if(attacker instanceof ServerPlayerEntity)
		{
			ModNetworkManager.sendToPlayer(new STCPlayAnimation(deflectAnimation.getId(), attacker.getId(), stuntime), (ServerPlayerEntity)attacker);
		}
		
		return true;
	}
	
	public IExtendedDamageSource getDamageSource(StunType stunType, int animationId, float amount, int requireddeflectionlevel, DamageType damageType)
	{
		return IExtendedDamageSource.causeMobDamage(this.orgEntity, stunType, animationId, amount, requireddeflectionlevel, damageType);
	}
	
	public float getDamageToEntity(Entity targetEntity, Hand hand)
	{
		float damage = 1.0F;
		
		WeaponCapability weapon = this.getHeldWeaponCapability(hand);
		if (weapon != null)
		{
			damage += ((WeaponItem)weapon.getOriginalItem()).getDamage(this.orgEntity);
		}
		
		if (targetEntity instanceof LivingEntity)
		{
			damage += EnchantmentHelper.getDamageBonus(this.orgEntity.getItemInHand(hand), ((LivingEntity) targetEntity).getMobType());
		}
		else
		{
			damage += EnchantmentHelper.getDamageBonus(this.orgEntity.getItemInHand(hand), CreatureAttribute.UNDEFINED);
		}
		
		return damage;
	}
	
	public boolean hurtEntity(Entity hitTarget, Hand handIn, IExtendedDamageSource source, float amount)
	{
		boolean succed = hitTarget.hurt((DamageSource) source, amount);
		
		if (succed)
		{
			int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, this.orgEntity.getItemInHand(handIn));
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
	
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {}
	
	public void setStunTimeReduction()
	{
		this.stunTimeReduction += (1.0F - stunTimeReduction) * 0.8F;
	}

	public float getStunTimeTimeReduction()
	{
		return this.stunTimeReduction;
	}

	public void knockBackEntity(Entity entityIn, float power)
	{
		power *= 0.1D;
		
		double d1 = entityIn.getX() - this.orgEntity.getX();
        double d0;
        
		for (d0 = entityIn.getZ() - this.orgEntity.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
		{
            d1 = (Math.random() - Math.random()) * 0.01D;
        }
        
		if (orgEntity.getRandom().nextDouble() >= orgEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))
		{
        	Vector3d vec = orgEntity.getDeltaMovement();
        	
        	orgEntity.hasImpulse = true;
            float f = MathHelper.sqrt(d1 * d1 + d0 * d0);
            
            double x = vec.x;
            double y = vec.y;
            double z = vec.z;
            
            x /= 2.0D;
            z /= 2.0D;
            x -= d1 / (double)f * (double)power;
            z -= d0 / (double)f * (double)power;

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
	
	public float getMaxStunArmor()
	{
		ModifiableAttributeInstance stun_resistance = this.orgEntity.getAttribute(ModAttributes.MAX_STUN_ARMOR.get());
		return (float) (stun_resistance == null ? 0 : stun_resistance.getValue());
	}
	
	public float getStunArmor()
	{
		return this.getMaxStunArmor() == 0 ? 0 : this.orgEntity.getEntityData().get(DataKeys.STUN_ARMOR).floatValue();
	}
	
	public void setStunArmor(float value)
	{
		float f1 = Math.max(Math.min(value, this.getMaxStunArmor()), 0);
		this.orgEntity.getEntityData().set(DataKeys.STUN_ARMOR, f1);
	}
	
	public double getWeight()
	{
		return this.orgEntity.getAttributeValue(ModAttributes.WEIGHT.get());
	}
	
	public void rotateTo(float degree, float limit, boolean partialSync)
	{
		LivingEntity entity = this.getOriginalEntity();
		float amount = MathHelper.wrapDegrees(degree - entity.yRot);
		
        while(amount < -180.0F)
        {
        	amount += 360.0F;
        }
        
        while(amount > 180.0F)
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
        
		if(partialSync)
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
        float degree = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
    	rotateTo(degree, limit, partialSync);
	}
	
	public void playSound(SoundEvent sound, float minPitch, float maxPitch)
	{
		float randPitch = this.orgEntity.getRandom().nextFloat() * 2.0F - 1.0F;
		randPitch = Math.min(Math.max(randPitch, minPitch), maxPitch);
		if(!this.isClientSide())
		{
			this.orgEntity.level.playSound(null, orgEntity.getX(), orgEntity.getY(), orgEntity.getZ(), sound, orgEntity.getSoundSource(), 1.0F, 1.0F + randPitch);
		}
		else
		{
			this.orgEntity.level.playLocalSound(orgEntity.getX(), orgEntity.getY(), orgEntity.getZ(), sound, orgEntity.getSoundSource(), 1.0F, 1.0F + randPitch, false);
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
		float correct = (pitch > 0) ? 0.03333F * (float)Math.pow(pitch, 2) : -0.03333F * (float)Math.pow(pitch, 2);
		
		return MathHelper.clamp(correct, -30.0F, 30.0F);
	}
	
	public PublicMatrix4f getHeadMatrix(float partialTicks)
	{
		float bodyRot;
        float headRot;
        float headRotDest;
		
		if (inaction) headRotDest = 0;
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
		
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.xRotO, orgEntity.xRot, headRotDest, headRotDest, partialTicks, 1, 1, 1);
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
		}
		else
		{
			prevRotYaw = (inaction ? orgEntity.yRot : orgEntity.yBodyRotO);
			rotyaw = (inaction ? orgEntity.yRot : orgEntity.yBodyRot);
		}
		
		if (this.orgEntity.isBaby())
		{
			scaleX *= 0.5F;
			scaleY *= 0.5F;
			scaleZ *= 0.5F;
		}
		
		return PublicMatrix4f.getModelMatrixIntegrated((float)orgEntity.xOld, (float)orgEntity.getX(), (float)orgEntity.yOld, (float)orgEntity.getY(),
				(float)orgEntity.zOld, (float)orgEntity.getZ(), 0, 0, prevRotYaw, rotyaw, partialTicks, scaleX, scaleY, scaleZ);
	}
	
	public void resetLivingMixLoop()
	{
		this.currentMixMotion = LivingMotion.NONE;
		this.getClientAnimator().resetMixMotion();
	}
	
	public void reserveAnimationSynchronize(StaticAnimation animation)
	{
		this.animator.reserveAnimation(animation);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(animation.getId(), this.orgEntity.getId(), 0.0F), this.orgEntity);
	}
	
	public void playAnimationSynchronize(int id, float modifyTime)
	{
		this.animator.playAnimation(id, modifyTime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(id, this.orgEntity.getId(), modifyTime), this.orgEntity);
	}

	public void playAnimationSynchronize(StaticAnimation animation, float modifyTime)
	{
		this.playAnimationSynchronize(animation.getId(), modifyTime);
	}
	
	public void resetSize(EntitySize size)
	{
		// Dimensions
		/*EntitySize entitysize = this.orgEntity.size;
		EntitySize entitysize1 = size;
		this.orgEntity.size = entitysize1;
	    if (entitysize1.width < entitysize.width)
	    {
	    	double d0 = (double)entitysize1.width / 2.0D;
	    	this.orgEntity.setBoundingBox(new AxisAlignedBB(orgEntity.getX() - d0, orgEntity.getY(), orgEntity.getZ() - d0, orgEntity.getX() + d0,
	    			orgEntity.getY() + (double)entitysize1.height, orgEntity.getZ() + d0));
	    }
	    else
	    {
	    	AxisAlignedBB axisalignedbb = this.orgEntity.getBoundingBox();
	    	this.orgEntity.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width,
	    			axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
	    	
	    	if (entitysize1.width > entitysize.width && orgEntity.level.isClientSide)
	    	{
	    		float f = entitysize.width - entitysize1.width;
	        	this.orgEntity.move(MoverType.SELF, new Vector3d((double)f, 0.0D, (double)f));
	    	}
	    }*/
    }
	
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {}
	
	@SuppressWarnings("unchecked")
	public <A extends Animator>A getAnimator()
	{
		return (A)this.animator;
	}

	public AnimatorClient getClientAnimator()
	{
		return this.<AnimatorClient>getAnimator();
	}

	public AnimatorServer getServerAnimator()
	{
		return this.<AnimatorServer>getAnimator();
	}

	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return null;
	}

	@Override
	public void aboutToDeath()
	{
		this.animator.onEntityDeath();
	}

	@Override
	public T getOriginalEntity()
	{
		return orgEntity;
	}

	public SoundEvent getWeaponHitSound(Hand hand)
	{
		WeaponCapability cap = this.getHeldWeaponCapability(hand);
		if (cap == null) return null;
		return cap.getHitSound();
	}
	
	public SoundEvent getWeaponSmashSound(Hand hand)
	{
		WeaponCapability cap = this.getHeldWeaponCapability(hand);
		if (cap == null) return null;
		return cap.getSmashSound();
	}

	public SoundEvent getSwingSound(Hand hand)
	{
		WeaponCapability cap = this.getHeldWeaponCapability(hand);
		if (cap == null) return null;
		return cap.getSwingSound();
	}
	
	public HitParticleType getWeaponHitParticle(Hand hand)
	{
		WeaponCapability cap = this.getHeldWeaponCapability(hand);
		if (cap == null) return null;
		return cap.getHitParticle();
	}

	public Collider getColliderMatching(Hand hand)
	{
		WeaponCapability cap = this.getHeldWeaponCapability(hand);
		return cap != null ? cap.getWeaponCollider() : Colliders.fist;
	}

	public float getImpact()
	{
		return (float)this.orgEntity.getAttributeValue(ModAttributes.IMPACT.get());
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
		return this.animator.getPlayer().getPlay().getState(animator.getPlayer().getElapsedTime());
	}

	public static enum EntityState
	{
		FREE(false, false, false, true, 0),
		FREE_CAMERA(true, false, false, false, 1),
		FREE_INPUT(false, false, false, true, 3),
		PRE_DELAY(true, false, false, false, 1),
		CONTACT(true, true, false, false, 2),
		POST_DELAY(true, false, false, true, 3),
		HIT(true, false, false, false, 3),
		DISARMED(true, true, false, false, 3),
		DODGE(true, false, true, false, 3);
		
		boolean movementLock;
		boolean collideDetection;
		boolean invincible;
		boolean canAct;
		// none : 0, beforeContact : 1, contact : 2, afterContact : 3
		int contactLevel;
		
		EntityState(boolean movementLock, boolean collideDetection, boolean invincible, boolean canAct, int level)
		{
			this.movementLock = movementLock;
			this.collideDetection = collideDetection;
			this.invincible = invincible;
			this.canAct = canAct;
			this.contactLevel = level;
		}
		
		public boolean isMovementLocked()
		{
			return this.movementLock;
		}
		
		public boolean shouldDetectCollision()
		{
			return this.collideDetection;
		}
		
		public boolean isInvincible()
		{
			return this.invincible;
		}
		
		public boolean canAct()
		{
			return this.canAct;
		}
		
		public int getContactLevel()
		{
			return this.contactLevel;
		}
	}
}