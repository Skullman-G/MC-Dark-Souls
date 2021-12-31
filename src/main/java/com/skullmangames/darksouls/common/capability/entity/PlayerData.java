package com.skullmangames.darksouls.common.capability.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCapability;
import com.skullmangames.darksouls.common.entity.DataKeys;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.common.item.WeaponItem;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.event.EntityEventListener;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.event.PlayerEvent;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.CursedFoodStats;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;

public abstract class PlayerData<T extends PlayerEntity> extends LivingData<T>
{
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("e6beeac4-77d2-11eb-9439-0242ac130002");
	protected float yaw;
	protected EntityEventListener eventListeners;
	protected int tickSinceLastAction;
	
	protected Stats stats = new Stats();
	
	protected float stamina;
	protected int humanity;
	protected boolean human;
	protected int souls;
	
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
		
		if (!this.orgEntity.hasEffect(ModEffects.UNDEAD_CURSE.get()))
		{
			EffectInstance effectinstance = new EffectInstance(ModEffects.UNDEAD_CURSE.get(), 1000000000);
			this.orgEntity.addEffect(effectinstance);
		}
		
		this.stamina = this.getMaxStamina();
	}
	
	public Stats getStats()
	{
		return this.stats;
	}
	
	public int getSoulLevel()
	{
		return this.stats.getLevel();
	}
	
	public void onSave()
	{
		CompoundNBT nbt = this.orgEntity.getPersistentData();
		nbt.putInt("Humanity", this.humanity);
		nbt.putInt("Souls", this.souls);
		nbt.putBoolean("IsHuman", this.human);
		
		this.stats.saveStats(nbt);
	}
	
	public int getSouls()
	{
		return this.souls;
	}
	
	public void setSouls(int value)
	{
		this.souls = value;
	}
	
	public void raiseSouls(int value)
	{
		this.setSouls(this.souls + value);
	}
	
	public boolean isHuman()
	{
		return this.human;
	}
	
	public void setHuman(boolean value)
	{
		this.human = value;
	}
	
	public int getHumanity()
	{
		return this.humanity;
	}
	
	public void setHumanity(int value)
	{
		this.humanity = MathUtils.clamp(value, 0, 99);
	}
	
	public void raiseHumanity(int value)
	{
		this.setHumanity(this.humanity + value);
	}
	
	@Override
	public float getWeaponDamage(Hand hand)
	{
		WeaponCapability weapon = this.getHeldWeaponCapability(hand);
		if (weapon == null || !weapon.meetRequirements(this)) return 0.0F;
		return ((WeaponItem)weapon.getOriginalItem()).getDamage();
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
		animatorClient.addLivingMixAnimation(LivingMotion.DIGGING, Animations.BIPED_DIG);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	public void changeYaw(float amount)
	{
		this.yaw = amount;
	}
	
	public void setStatValue(Stat stat, int value)
	{
		this.stats.setStatValue(this.orgEntity, stat, value);
	}
	
	public void setStatValue(String statname, int value)
	{
		this.stats.setStatValue(this.orgEntity, statname, value);
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
		return (float)this.orgEntity.getAttributeValue(ModAttributes.MAX_STAMINA.get());
	}
	
	public boolean isCreativeOrSpectator()
	{
		return this.orgEntity.isCreative() || this.orgEntity.isSpectator();
	}
	
	@Override
	public IExtendedDamageSource getDamageSource(StunType stunType, int id, float amount, int requireddeflectionlevel, DamageType damageType)
	{
		return IExtendedDamageSource.causePlayerDamage(orgEntity, stunType, id, amount, requireddeflectionlevel, damageType);
	}
	
	public void discard()
	{
		super.aboutToDeath();
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		if (orgEntity.getControllingPassenger() != null)
		{
			return Animations.BIPED_HIT_ON_MOUNT;
		}
		else
		{
			switch (stunType)
			{
				case LONG:
					return Animations.BIPED_HIT_LONG;
					
				case SHORT:
					return Animations.BIPED_HIT_SHORT;
					
				case HOLD:
					return Animations.BIPED_HIT_SHORT;
					
				case SMASH_FRONT:
					return Animations.BIPED_HIT_DOWN_FRONT;
					
				case SMASH_BACK:
					return Animations.BIPED_HIT_DOWN_BACK;
					
				default:
					return null;
			}
		}
	}
}