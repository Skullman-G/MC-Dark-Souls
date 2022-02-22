package com.skullmangames.darksouls.common.capability.entity;

import java.util.UUID;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.AnimatorClient;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.event.EntityEventListener;
import com.skullmangames.darksouls.core.event.EntityEventListener.EventType;
import com.skullmangames.darksouls.core.event.PlayerEvent;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public abstract class PlayerData<T extends Player> extends LivingData<T>
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
		this.eventListeners = new EntityEventListener(this);
		this.tickSinceLastAction = 40;
		this.eventListeners.addEventListener(EventType.ON_ACTION_EVENT, PlayerEvent.makeEvent(ACTION_EVENT_UUID, (player, args) ->
		{
			player.tickSinceLastAction = 0;
			return false;
		}));
		
		if (!this.orgEntity.getInventory().contains(new ItemStack(ModItems.DARKSIGN.get())))
		{
			this.orgEntity.getInventory().add(new ItemStack(ModItems.DARKSIGN.get()));
		}
		
		this.stamina = this.getMaxStamina();
	}
	
	public void onLoad(CompoundTag nbt)
	{
		this.humanity = nbt.getInt("Humanity");
		this.souls = nbt.getInt("Souls");
		this.human = nbt.getBoolean("IsHuman");
	}
	
	public void onSave()
	{
		this.orgEntity.getPersistentData().put(DarkSouls.MOD_ID, new CompoundTag());
		CompoundTag nbt = this.orgEntity.getPersistentData().getCompound(DarkSouls.MOD_ID);
		nbt.putInt("Humanity", this.humanity);
		nbt.putInt("Souls", this.souls);
		nbt.putBoolean("IsHuman", this.human);
		
		this.stats.saveStats(nbt);
	}
	
	public Stats getStats()
	{
		return this.stats;
	}
	
	public int getSoulLevel()
	{
		return this.stats.getLevel();
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
	
	public boolean hasEnoughHumanity(int cost)
	{
		return this.isCreativeOrSpectator() ? true : this.humanity >= cost;
	}
	
	public boolean hasEnoughSouls(int cost)
	{
		return this.isCreativeOrSpectator() ? true : this.souls >= cost;
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
	
	public void setStatValue(int index, int value)
	{
		this.stats.setStatValue(this.orgEntity, index, value);
	}
	
	@Override
	public void updateOnServer()
	{
		super.updateOnServer();
		this.tickSinceLastAction++;
	}
	
	@Override
	public void update()
	{
		super.update();
		this.orgEntity.getFoodData().setFoodLevel(15);
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
		this.setStamina(MathUtils.clamp(this.stamina + increment, -5F, this.getMaxStamina()));
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
	public IExtendedDamageSource getDamageSource(StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage)
	{
		return IExtendedDamageSource.causePlayerDamage(orgEntity, stunType, amount, requireddeflectionlevel, damageType, poiseDamage);
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
				case DEFAULT:
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