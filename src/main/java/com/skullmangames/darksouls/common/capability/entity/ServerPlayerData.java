package com.skullmangames.darksouls.common.capability.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCHuman;
import com.skullmangames.darksouls.network.server.STCHumanity;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCLoadPlayerData;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCSouls;
import com.skullmangames.darksouls.network.server.STCStamina;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class ServerPlayerData extends PlayerData<ServerPlayer> implements IEquipLoaded
{
	private Map<LivingMotion, StaticAnimation> livingMotionMap = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private List<LivingMotion> modifiedLivingMotions = Lists.<LivingMotion>newArrayList();
	
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
	@Override
	public void onEntityJoinWorld(ServerPlayer entityIn)
	{
		super.onEntityJoinWorld(entityIn);
		livingMotionMap.put(LivingMotion.IDLE, Animations.BIPED_IDLE);
		livingMotionMap.put(LivingMotion.WALKING, Animations.BIPED_WALK);
		livingMotionMap.put(LivingMotion.RUNNING, Animations.BIPED_RUN);
		livingMotionMap.put(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
		livingMotionMap.put(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
		livingMotionMap.put(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
		livingMotionMap.put(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
		livingMotionMap.put(LivingMotion.FALL, Animations.BIPED_FALL);
		livingMotionMap.put(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		livingMotionMap.put(LivingMotion.DEATH, Animations.BIPED_DEATH);
		
		for (Map.Entry<LivingMotion, StaticAnimation> entry : livingMotionMap.entrySet())
		{
			defaultLivingAnimations.put(entry.getKey(), entry.getValue());
		}
		
		CompoundTag nbt = entityIn.getPersistentData().getCompound(DarkSouls.MOD_ID);
		this.humanity = nbt.getInt("Humanity");
		this.souls = nbt.getInt("Souls");
		this.human = nbt.getBoolean("IsHuman");
		ModNetworkManager.sendToPlayer(new STCLoadPlayerData(nbt), entityIn);
		
		this.stats.loadStats(this.orgEntity, nbt);
	}
	
	public void performDodge()
	{
		float e = this.getEncumbrance();
		this.animator.playAnimation(Animations.BIPED_DODGE, e);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCPlayAnimation(Animations.BIPED_DODGE, this.orgEntity.getId(), e), this.orgEntity);
		
		if (this.isCreativeOrSpectator()) return;
		this.increaseStamina(-4.0F);
		ModNetworkManager.sendToPlayer(new STCStamina(this.orgEntity.getId(), this.stamina), this.orgEntity);
	}
	
	@Override
	public void setHumanity(int value)
	{
		if (this.humanity == value) return;
		super.setHumanity(value);
		ModNetworkManager.sendToPlayer(new STCHumanity(this.orgEntity.getId(), this.humanity), this.orgEntity);
	}
	
	@Override
	public void setHuman(boolean value)
	{
		if (this.human == value) return;
		if (value)
		{
			this.playSound(ModSoundEvents.GENERIC_HUMAN_FORM.get(), -0.2F, -0.2F);
		}
		super.setHuman(value);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCHuman(this.orgEntity.getId(), this.human), this.orgEntity);
	}
	
	@Override
	public void setSouls(int value)
	{
		if (this.souls == value) return;
		super.setSouls(value);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCSouls(this.orgEntity.getId(), this.souls), this.orgEntity);
	}
	
	@Override
	public void updateOnServer()
	{
		super.updateOnServer();
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCStamina(this.orgEntity.getId(), this.stamina), this.orgEntity);
	}
	
	@Override
	public void updateMotion() {}
	
	public void onHeldItemChange(ItemCapability toChange, ItemStack stack, InteractionHand hand)
	{
		ItemCapability mainHandCap = hand == InteractionHand.MAIN_HAND ? toChange : this.getHeldItemCapability(InteractionHand.MAIN_HAND);
		if(mainHandCap != null) mainHandCap.onHeld(this);
		
		this.modifiLivingMotions(mainHandCap);
	}
	
	@Override
	public boolean blockingAttack(IExtendedDamageSource damageSource)
	{
		if (!this.isBlocking()) return false;
		else if (this.isCreativeOrSpectator()) return super.blockingAttack(damageSource);
		
		this.increaseStamina(-4.0F);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCStamina(this.orgEntity.getId(), this.stamina), this.orgEntity);
		if (this.getStamina() > 0.0F) return super.blockingAttack(damageSource);
		
		this.playSound(ModSoundEvents.PLAYER_SHIELD_DISARMED.get(), 1.0F, 1.0F);
		
		StaticAnimation disarmAnimation = Animations.BIPED_DISARM_SHIELD;
		this.animator.playAnimation(disarmAnimation, 0.0F);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCPlayAnimation(disarmAnimation, this.orgEntity.getId(), 0.0F), this.orgEntity);
		return true;
	}
	
	public void modifiLivingMotions(ItemCapability itemCap)
	{
		this.resetModifiedLivingMotions();

		if (itemCap != null)
		{
			Map<LivingMotion, StaticAnimation> motionChanger = itemCap.getLivingMotionChanges(this);
			if (motionChanger != null)
			{
				List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
				List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
				
				for (Map.Entry<LivingMotion, StaticAnimation> entry : motionChanger.entrySet())
				{
					this.addModifiedLivingMotion(entry.getKey(), entry.getValue());
					motions.add(entry.getKey());
					animations.add(entry.getValue());
				}
				
				LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
				StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
				STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getId(), motionChanger.size());
				msg.setMotions(motionarr);
				msg.setAnimations(animationarr);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
				return;
			}
		}
		
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getId(), 0);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
	}
	
	private void addModifiedLivingMotion(LivingMotion motion, StaticAnimation animation)
	{
		if(animation != null)
		{
			if (!this.modifiedLivingMotions.contains(motion))
			{
				this.modifiedLivingMotions.add(motion);
			}
			
			this.livingMotionMap.put(motion, animation);
		}
	}
	
	private void resetModifiedLivingMotions()
	{
		for(LivingMotion livingMotion : modifiedLivingMotions)
		{
			this.livingMotionMap.put(livingMotion, defaultLivingAnimations.get(livingMotion));
		}
		
		modifiedLivingMotions.clear();
	}

	public void modifiLivingMotionToAll(STCLivingMotionChange packet)
	{
		LivingMotion[] motions = packet.getMotions();
		StaticAnimation[] animations = packet.getAnimations();
		
		for(int i = 0; i < motions.length; i++)
		{
			this.addModifiedLivingMotion(motions[i], animations[i]);
		}
		
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(packet, this.orgEntity);
	}
	
	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingMotionEntrySet()
	{
		return this.livingMotionMap.entrySet();
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime)
	{
		super.playAnimationSynchronize(id, modifyTime);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(id, this.orgEntity.getId(), modifyTime), this.orgEntity);
	}
	
	@Override
	public void reserveAnimationSynchronize(StaticAnimation animation)
	{
		super.reserveAnimationSynchronize(animation);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(animation, this.orgEntity.getId(), 0.0F), this.orgEntity);
	}
	
	@Override
	public void changeYaw(float amount)
	{
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(this.orgEntity.getId(), yaw), this.orgEntity);
		ModNetworkManager.sendToPlayer(new STCNotifyPlayerYawChanged(this.orgEntity.getId(), yaw), this.orgEntity);
	}
	
	@Override
	public ServerPlayer getOriginalEntity()
	{
		return this.orgEntity;
	}
	
	@Override
	public void aboutToDeath() {}

	@Override
	public float getEncumbrance()
	{
		return (float)(this.orgEntity.getAttributeValue(ModAttributes.EQUIP_LOAD.get()) / this.orgEntity.getAttributeValue(ModAttributes.MAX_EQUIP_LOAD.get()));
	}

	@Override
	public EquipLoadLevel getEquipLoadLevel()
	{
		float e = this.getEncumbrance();
		
		if (e <= 0.0F) return EquipLoadLevel.NONE;
		else if (e <= 0.25F) return EquipLoadLevel.LIGHT;
		else if (e <= 0.50F) return EquipLoadLevel.MEDIUM;
		else if (e <= 1.00F) return EquipLoadLevel.HEAVY;
		else return EquipLoadLevel.OVERENCUMBERED;
	}
}