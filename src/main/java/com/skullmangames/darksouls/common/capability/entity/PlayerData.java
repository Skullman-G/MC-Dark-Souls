package com.skullmangames.darksouls.common.capability.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.DataKeys;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.event.EntityEventListener;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.event.PlayerEvent;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.CursedFoodStats;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;

public abstract class PlayerData<T extends PlayerEntity> extends LivingData<T>
{
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("e6beeac4-77d2-11eb-9439-0242ac130002");
	protected float yaw;
	protected EntityEventListener eventListeners;
	protected int tickSinceLastAction;
	
	protected float stamina;
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.foodData = new CursedFoodStats();
		this.eventListeners = new EntityEventListener(this);
		this.orgEntity.getEntityData().define(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
		this.tickSinceLastAction = 40;
		this.eventListeners.addEventListener(EventType.ON_ACTION_EVENT, PlayerEvent.makeEvent(ACTION_EVENT_UUID, (player, args) ->
		{
			player.tickSinceLastAction = 0;
			return false;
		}));
		
		if (!this.orgEntity.hasEffect(EffectInit.UNDEAD_CURSE.get()))
		{
			EffectInstance effectinstance = new EffectInstance(EffectInit.UNDEAD_CURSE.get(), 1000000000);
			this.orgEntity.addEffect(effectinstance);
		}
		
		for (Stat stat : Stats.getStats()) stat.init(this.orgEntity);
		
		this.stamina = this.getMaxStamina();
	}
	
	@Override
	public void initAnimator(AnimatorClient animatorClient)
	{
		animatorClient.mixLayerLeft.setJointMask("Shoulder_L", "Arm_L", "Hand_L");
		animatorClient.mixLayerRight.setJointMask("Shoulder_R", "Arm_R", "Hand_R");
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
		animatorClient.addLivingAnimation(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
		animatorClient.addLivingAnimation(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
		animatorClient.addLivingAnimation(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.DRINKING, Animations.BIPED_DRINK);
		animatorClient.addLivingAnimation(LivingMotion.CONSUME_SOUL, Animations.BIPED_CONSUME_SOUL);
		animatorClient.addLivingAnimation(LivingMotion.EATING, Animations.BIPED_EAT);
		animatorClient.addLivingMixAnimation(LivingMotion.BLOCKING, Animations.BIPED_BLOCK);
		animatorClient.addLivingMixAnimation(LivingMotion.AIMING, Animations.BIPED_BOW_AIM);
		animatorClient.addLivingMixAnimation(LivingMotion.RELOADING, Animations.BIPED_CROSSBOW_RELOAD);
		animatorClient.addLivingMixAnimation(LivingMotion.SHOTING, Animations.BIPED_BOW_REBOUND);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	public void changeYaw(float amount)
	{
		this.yaw = amount;
	}
	
	@Override
	public void updateOnServer()
	{
		super.updateOnServer();
		this.tickSinceLastAction++;
		float stunArmor = this.getStunArmor();
		float maxStunArmor = this.getMaxStunArmor();
		
		if (!this.isCreativeOrSpectator())
		{
			this.increaseStamina(this.orgEntity.isSprinting() ? -0.1F
					: this.orgEntity.isBlocking() ? 0.05F : 0.1F);
		}
		
		if (stunArmor < maxStunArmor && this.tickSinceLastAction > 60)
		{
			float stunArmorFactor = 1.0F + (stunArmor / maxStunArmor);
			float healthFactor = this.orgEntity.getHealth() / this.orgEntity.getMaxHealth();
			this.setStunArmor(stunArmor + maxStunArmor * 0.01F * healthFactor * stunArmorFactor);
		}
		
		if (maxStunArmor < stunArmor) this.setStunArmor(maxStunArmor);
	}
	
	public float getAttackSpeed()
	{
		return (float)this.orgEntity.getAttributeValue(Attributes.ATTACK_SPEED);
	}
	
	public EntityEventListener getEventListener()
	{
		return this.eventListeners;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount)
	{
		if(super.attackEntityFrom(damageSource, amount))
		{
			this.tickSinceLastAction = 0;
			return true;
		}
		else return false;
	}
	
	public void setStamina(float value)
	{
		this.stamina = value;
	}
	
	public void increaseStamina(float increment)
	{
		this.stamina = MathUtils.clamp(stamina + increment, 0.0F, this.getMaxStamina());
		if (increment < 0.0F && this.stamina == 0.0F) this.stamina = -5.0F;
	}
	
	public float getStamina()
	{
		return this.stamina;
	}
	
	public float getMaxStamina()
	{
		return (float)this.orgEntity.getAttributeValue(AttributeInit.MAX_STAMINA.get());
	}
	
	public boolean isCreativeOrSpectator()
	{
		return this.orgEntity.isCreative() || this.orgEntity.isSpectator();
	}
	
	@Override
	public IExtendedDamageSource getDamageSource(StunType stunType, DamageType damageType, int id, float amount, int requireddeflectionlevel)
	{
		return IExtendedDamageSource.causePlayerDamage(orgEntity, stunType, damageType, id, amount, requireddeflectionlevel);
	}
	
	public void discard()
	{
		super.aboutToDeath();
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		if(orgEntity.getControllingPassenger() != null)
		{
			return Animations.BIPED_HIT_ON_MOUNT;
		}
		else
		{
			switch(stunType)
			{
				case LONG:
					return Animations.BIPED_HIT_LONG;
					
				case SHORT:
					return Animations.BIPED_HIT_SHORT;
					
				case HOLD:
					return Animations.BIPED_HIT_SHORT;
					
				default:
					return null;
			}
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED;
	}
}