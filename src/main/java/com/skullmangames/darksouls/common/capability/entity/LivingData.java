package com.skullmangames.darksouls.common.capability.entity;

import java.util.ArrayList;
import java.util.List;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.common.animation.Animator;
import com.skullmangames.darksouls.common.animation.AnimatorServer;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.entity.DataKeys;
import com.skullmangames.darksouls.common.particle.HitParticleType;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.Colliders;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModelInit;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;
import com.skullmangames.darksouls.core.util.physics.Collider;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
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
	protected boolean inaction = false;
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
	}
	
	protected abstract void initAnimator(AnimatorClient animatorClient);
	public abstract void updateMotion();
	public abstract <M extends Model> M getEntityModel(ModelInit<M> modelDB);
	
	protected void initAttributes()
	{
		this.orgEntity.getAttribute(AttributeInit.WEIGHT.get()).setBaseValue(this.orgEntity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.orgEntity.getAttribute(AttributeInit.MAX_STRIKES.get()).setBaseValue(1.0D);
		this.orgEntity.getAttribute(AttributeInit.ARMOR_NEGATION.get()).setBaseValue(0.0D);
		this.orgEntity.getAttribute(AttributeInit.IMPACT.get()).setBaseValue(0.5D);
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
	
	@Override
	protected void updateOnServer()
	{
		if(stunTimeReduction > 0.0F)
		{
			stunTimeReduction = Math.max(0.0F, stunTimeReduction - 0.05F);
		}
	}
	
	@Override
	public void update()
	{
		updateInactionState();

		if (isRemote())
		{
			updateOnClient();
		}
		else
		{
			updateOnServer();
		}

		animator.update();
		if (orgEntity.deathTime == 19)
		{
			aboutToDeath();
		}
	}

	public void updateInactionState()
	{
		EntityState state = this.getEntityState();
		if (!state.isCameraRotationLocked() && !state.isMovementLocked())
		{
			this.inaction = false;
		}
		else
		{
			this.inaction = true;
		}
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
			else if(orgEntity.swingTime > 0.01F)
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
	
	public IExtendedDamageSource getDamageSource(StunType stunType, DamageType damageType, int animationId)
	{
		return IExtendedDamageSource.causeMobDamage(orgEntity, stunType, damageType, animationId);
	}
	
	public float getDamageToEntity(Entity targetEntity, Hand hand)
	{
		float damage = 0;
		if (hand == Hand.MAIN_HAND)
		{
			damage = (float) orgEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
		}
		else
		{
			damage = (float) orgEntity.getAttributeValue(AttributeInit.OFFHAND_ATTACK_DAMAGE.get());
		}
		
		float bonus;
		if (targetEntity instanceof LivingEntity)
		{
			bonus = EnchantmentHelper.getDamageBonus(this.orgEntity.getItemInHand(hand), ((LivingEntity) targetEntity).getMobType());
		}
		else
		{
			bonus = EnchantmentHelper.getDamageBonus(this.orgEntity.getItemInHand(hand), CreatureAttribute.UNDEFINED);
		}
		
		return damage + bonus;
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
	
	public void gatherDamageDealt(IExtendedDamageSource source, float amount)
	{
		
	}
	
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
		ModifiableAttributeInstance stun_resistance = this.orgEntity.getAttribute(AttributeInit.MAX_STUN_ARMOR.get());
		return (float) (stun_resistance == null ? 0 : stun_resistance.getValue());
	}
	
	public float getStunArmor()
	{
		return getMaxStunArmor() == 0 ? 0 : this.orgEntity.getEntityData().get(DataKeys.STUN_ARMOR).floatValue();
	}
	
	public void setStunArmor(float value)
	{
		float f1 = Math.max(Math.min(value, this.getMaxStunArmor()), 0);
		this.orgEntity.getEntityData().set(DataKeys.STUN_ARMOR, f1);
	}
	
	public double getWeight()
	{
		return this.orgEntity.getAttributeValue(AttributeInit.WEIGHT.get());
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
		if(!this.isRemote())
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
		float f;
        float f1;
        float f2;
		
		if (inaction)
		{
			f2 = 0;
		}
		else
		{
			f = MathUtils.interpolateRotation(orgEntity.yBodyRotO, orgEntity.yBodyRot, partialTicks);
			f1 = MathUtils.interpolateRotation(orgEntity.yHeadRotO, orgEntity.yHeadRot, partialTicks);
			f2 = f1 - f;

			if (orgEntity.getControllingPassenger() != null)
			{
				if (f2 > 45.0F)
				{
					f2 = 45.0F;
				} else if (f2 < -45.0F)
				{
					f2 = -45.0F;
				}
			}
		}
		
		return PublicMatrix4f.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.xRotO, orgEntity.xRot, f2, f2, partialTicks, 1, 1, 1);
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
	
	public void reserverAnimationSynchronize(StaticAnimation animation)
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
	
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType)
	{
		
	}
	
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

	public abstract StaticAnimation getHitAnimation(StunType stunType);

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
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null)
			return cap.getHitSound();

		return null;
	}

	public SoundEvent getSwingSound(Hand hand)
	{
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null)
		{
			return cap.getSmashingSound();
		}

		return null;
	}
	
	public HitParticleType getWeaponHitParticle(Hand hand)
	{
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null) return cap.getHitParticle();

		return null;
	}

	public Collider getColliderMatching(Hand hand)
	{
		CapabilityItem itemCap = this.getHeldItemCapability(hand);
		return itemCap != null ? itemCap.getWeaponCollider() : Colliders.fist;
	}

	public int getHitEnemies()
	{
		return (int) this.orgEntity.getAttributeValue(AttributeInit.MAX_STRIKES.get());
	}

	public float getArmorNegation()
	{
		return (float) this.orgEntity.getAttributeValue(AttributeInit.ARMOR_NEGATION.get());
	}

	public float getImpact()
	{
		return (float) this.orgEntity.getAttributeValue(AttributeInit.IMPACT.get());
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
		FREE(false, false, false, false, true, 0),
		FREE_CAMERA(false, true, false, false, false, 1),
		FREE_INPUT(false, false, false, false, true, 3),
		PRE_DELAY(true, true, false, false, false, 1),
		CONTACT(true, true, true, false, false, 2),
		ROTATABLE_CONTACT(false, true, true, false, false, 2),
		POST_DELAY(true, true, false, false, true, 3),
		ROTATABLE_POST_DELAY(false, true, false, false, true, 3),
		HIT(true, true, false, false, false, 3),
		DODGE(true, true, false, true, false, 3);
		
		boolean cameraLock;
		boolean movementLock;
		boolean collideDetection;
		boolean invincible;
		boolean canAct;
		// none : 0, beforeContact : 1, contact : 2, afterContact : 3
		int level;
		
		EntityState(boolean cameraLock, boolean movementLock, boolean collideDetection, boolean invincible, boolean canAct, int level)
		{
			this.cameraLock = cameraLock;
			this.movementLock = movementLock;
			this.collideDetection = collideDetection;
			this.invincible = invincible;
			this.canAct = canAct;
			this.level = level;
		}
		
		public boolean isCameraRotationLocked()
		{
			return this.cameraLock;
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
		
		public int getLevel()
		{
			return this.level;
		}
	}
}