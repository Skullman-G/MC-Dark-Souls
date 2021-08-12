package com.skullmangames.darksouls.common.entities;

import java.util.UUID;

import com.skullmangames.darksouls.animation.LivingMotion;
import com.skullmangames.darksouls.animation.types.StaticAnimation;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.event.EntityEventListener;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.event.PlayerEvent;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModelInit;
import com.skullmangames.darksouls.core.init.Skills;
import com.skullmangames.darksouls.skill.SkillContainer;
import com.skullmangames.darksouls.skill.SkillSlot;
import com.skullmangames.darksouls.util.IExtendedDamageSource;
import com.skullmangames.darksouls.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.util.IExtendedDamageSource.StunType;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

public abstract class PlayerData<T extends PlayerEntity> extends LivingData<T>
{
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("e6beeac4-77d2-11eb-9439-0242ac130002");
	protected float yaw;
	protected EntityEventListener eventListeners;
	protected int tickSinceLastAction;
	public SkillContainer[] skills;
	
	public PlayerData()
	{
		SkillSlot[] slots = SkillSlot.values();
		this.skills = new SkillContainer[SkillSlot.values().length];
		for(SkillSlot slot : slots)
		{
			this.skills[slot.getIndex()] = new SkillContainer(this);
		}
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.eventListeners = new EntityEventListener(this);
		this.skills[SkillSlot.DODGE.getIndex()].setSkill(Skills.ROLL);
		this.orgEntity.getEntityData().define(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
		this.tickSinceLastAction = 40;
		this.eventListeners.addEventListener(EventType.ON_ACTION_EVENT, PlayerEvent.makeEvent(ACTION_EVENT_UUID, (player, args) ->
		{
			player.tickSinceLastAction = 0;
			return false;
		}));
	}
	
	@Override
	public void initAnimator(AnimatorClient animatorClient)
	{
		animatorClient.mixLayer.setJointMask("Root", "Torso");
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUNNING, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
		animatorClient.addLivingAnimation(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
		animatorClient.addLivingAnimation(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
		animatorClient.addLivingAnimation(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.FLYING, Animations.BIPED_FLYING);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.JUMPING, Animations.BIPED_JUMP);
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
		
		if (stunArmor < maxStunArmor && this.tickSinceLastAction > 60)
		{
			float stunArmorFactor = 1.0F + (stunArmor / maxStunArmor);
			float healthFactor = this.orgEntity.getHealth() / this.orgEntity.getMaxHealth();
			this.setStunArmor(stunArmor + maxStunArmor * 0.01F * healthFactor * stunArmorFactor);
		}
		
		if (maxStunArmor < stunArmor)
		{
			this.setStunArmor(maxStunArmor);
		}
	}
	
	@Override
	public void update()
	{
		if(this.orgEntity.getControllingPassenger() == null)
		{
			for(SkillContainer container : this.skills)
			{
				if(container != null)
				{
					container.update();
				}
			}
		}
		super.update();
	}
	
	public SkillContainer getSkill(SkillSlot slot)
	{
		return this.skills[slot.getIndex()];
	}
	
	public SkillContainer getSkill(int slotIndex)
	{
		return this.skills[slotIndex];
	}
	
	public float getAttackSpeed()
	{
		return (float) orgEntity.getAttributeValue(Attributes.ATTACK_SPEED);
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
		else
		{
			return false;
		}
	}
	
	@Override
	public IExtendedDamageSource getDamageSource(StunType stunType, DamageType damageType, int id)
	{
		return IExtendedDamageSource.causePlayerDamage(orgEntity, stunType, damageType, id);
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
	public <M extends Model> M getEntityModel(ModelInit<M> modelDB)
	{
		return modelDB.ENTITY_BIPED;
	}
}