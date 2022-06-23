package com.skullmangames.darksouls.common.capability.entity;

import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.WeaponCap;
import com.skullmangames.darksouls.common.entity.stats.Stat;
import com.skullmangames.darksouls.common.entity.stats.Stats;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.animation.ClientAnimator;
import com.skullmangames.darksouls.client.renderer.entity.model.Model;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.Models;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.ExtendedDamageSource.StunType;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public abstract class PlayerCap<T extends Player> extends LivingCap<T>
{
	protected float yaw;
	protected int tickSinceLastAction;
	
	protected Stats stats = new Stats();
	
	protected int humanity;
	protected boolean human;
	protected int souls;
	protected float fp;
	
	@Override
	public void onEntityJoinWorld(T entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		this.tickSinceLastAction = 40;
		
		if (!this.orgEntity.getInventory().contains(new ItemStack(ModItems.DARKSIGN.get())))
		{
			this.orgEntity.getInventory().add(new ItemStack(ModItems.DARKSIGN.get()));
		}
	}
	
	public void onLoad(CompoundTag nbt)
	{
		this.humanity = nbt.getInt("Humanity");
		this.souls = nbt.getInt("Souls");
		this.human = nbt.getBoolean("IsHuman");
		this.fp = nbt.getFloat("FocusPoints");
		
		this.stats.loadStats(this.orgEntity, nbt);
	}
	
	public void onSave()
	{
		this.orgEntity.getPersistentData().put(DarkSouls.MOD_ID, new CompoundTag());
		CompoundTag nbt = this.orgEntity.getPersistentData().getCompound(DarkSouls.MOD_ID);
		
		nbt.putInt("Humanity", this.humanity);
		nbt.putInt("Souls", this.souls);
		nbt.putBoolean("IsHuman", this.human);
		nbt.putFloat("FocusPoints", this.fp);
		
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
	
	public float getFP()
	{
		return this.fp;
	}
	
	public void setFP(float value)
	{
		this.fp = value;
	}
	
	public float getMaxFP()
	{
		return (float)this.orgEntity.getAttributeValue(ModAttributes.MAX_FOCUS_POINTS.get());
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
	public void initAnimator(ClientAnimator animatorClient)
	{
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
		animatorClient.addLivingAnimation(LivingMotion.BLOCKING, Animations.BIPED_BLOCK);
		animatorClient.addLivingAnimation(LivingMotion.AIMING, Animations.BIPED_BOW_AIM);
		animatorClient.addLivingAnimation(LivingMotion.RELOADING, Animations.BIPED_CROSSBOW_RELOAD);
		animatorClient.addLivingAnimation(LivingMotion.SHOOTING, Animations.BIPED_BOW_REBOUND);
		animatorClient.addLivingAnimation(LivingMotion.DIGGING, Animations.BIPED_DIG);
		animatorClient.addLivingAnimation(LivingMotion.HOLDING_WEAPON, Animations.BIPED_HOLDING_BIG_WEAPON);
		animatorClient.setCurrentMotionsToDefault();
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
	
	@Override
	public StaticAnimation getDeflectAnimation()
	{
		return Animations.HOLLOW_DEFLECTED;
	}
	
	@Override
	public boolean hurt(DamageSource damageSource, float amount)
	{
		if(super.hurt(damageSource, amount))
		{
			this.tickSinceLastAction = 0;
			return true;
		}
		else return false;
	}
	
	public boolean isCreativeOrSpectator()
	{
		return this.orgEntity.isCreative() || this.orgEntity.isSpectator();
	}
	
	@Override
	public ExtendedDamageSource getDamageSource(int staminaDmgMul, StunType stunType, float amount, int requireddeflectionlevel, DamageType damageType, float poiseDamage)
	{
		WeaponCap weapon = ModCapabilities.getWeaponCap(this.orgEntity.getMainHandItem());
		float staminaDmg = weapon != null ? Math.max(4, weapon.getStaminaDamage()) * staminaDmgMul : 4;
		return ExtendedDamageSource.causePlayerDamage(orgEntity, stunType, amount, requireddeflectionlevel, damageType, poiseDamage, staminaDmg);
	}
	
	public void discard()
	{
		super.onDeath();
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_BIPED;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		return HumanoidCap.getHumanoidHitAnimation(this, stunType);
	}
}