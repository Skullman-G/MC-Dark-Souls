package com.skullmangames.darksouls.common.capability.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.skullmangames.darksouls.common.animation.LivingMotion;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.AttributeInit;
import com.skullmangames.darksouls.core.init.SoundEvents;
import com.skullmangames.darksouls.core.util.Formulars;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IndirectDamageSourceExtended;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCLivingMotionChange;
import com.skullmangames.darksouls.network.server.STCNotifyPlayerYawChanged;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCStamina;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ServerPlayerData extends PlayerData<ServerPlayerEntity>
{
	private Map<LivingMotion, StaticAnimation> livingMotionMap = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private List<LivingMotion> modifiedLivingMotions = Lists.<LivingMotion>newArrayList();
	
	public static final UUID WEIGHT_PENALTY_MODIFIIER = UUID.fromString("414fed9e-e5e3-11ea-adc1-0242ac120002");
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
	@Override
	public void onEntityJoinWorld(ServerPlayerEntity entityIn)
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
	}
	
	@Override
	public void updateOnServer()
	{
		super.updateOnServer();
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCStamina(this.orgEntity.getId(), this.stamina), this.orgEntity);
	}
	
	@Override
	public void updateMotion() {}
	
	public void onHeldItemChange(CapabilityItem toChange, ItemStack stack, Hand hand)
	{
		CapabilityItem mainHandCap = hand == Hand.MAIN_HAND ? toChange : this.getHeldItemCapability(Hand.MAIN_HAND);
		if(mainHandCap != null) mainHandCap.onHeld(this);
		
		if (hand == Hand.MAIN_HAND)
		{
			this.orgEntity.getAttribute(Attributes.ATTACK_SPEED).removeModifier(WEIGHT_PENALTY_MODIFIIER);
			float weaponSpeed = (float) this.orgEntity.getAttribute(Attributes.ATTACK_SPEED).getBaseValue();
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
				weaponSpeed += attributeModifier.getAmount();
			
			this.orgEntity.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
					Formulars.getAttackSpeedPenalty(this.getWeight(), weaponSpeed, this), Operation.ADDITION));
		}
		else
		{
			this.orgEntity.getAttribute(AttributeInit.OFFHAND_ATTACK_SPEED.get()).removeModifier(WEIGHT_PENALTY_MODIFIIER);
			float weaponSpeed = (float) this.orgEntity.getAttribute(AttributeInit.OFFHAND_ATTACK_SPEED.get()).getBaseValue();
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
			{
				weaponSpeed += attributeModifier.getAmount();
			}
			
			this.orgEntity.getAttribute(AttributeInit.OFFHAND_ATTACK_SPEED.get()).addTransientModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
					Formulars.getAttackSpeedPenalty(this.getWeight(), weaponSpeed, this), Operation.ADDITION));
			
			this.orgEntity.getAttribute(AttributeInit.OFFHAND_ATTACK_DAMAGE.get()).removeModifier(ATTACK_DAMAGE_MODIFIER);
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE))
			{
				this.orgEntity.getAttribute(AttributeInit.OFFHAND_ATTACK_DAMAGE.get()).addTransientModifier(attributeModifier);
			}
		}
		
		this.modifiLivingMotions(mainHandCap);
	}
	
	@Override
	public boolean blockingAttack(IExtendedDamageSource damageSource)
	{
		if (!this.orgEntity.isBlocking() || damageSource == null || damageSource instanceof IndirectDamageSourceExtended) return false;
		else if (this.isCreativeOrSpectator()) return super.blockingAttack(damageSource);
		
		this.increaseStamina(-4.0F);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCStamina(this.orgEntity.getId(), this.stamina), this.orgEntity);
		if (this.getStamina() > 0.0F) return super.blockingAttack(damageSource);
		
		this.playSound(SoundEvents.PLAYER_SHIELD_DISARMED, 1.0F, 1.0F);
		
		StaticAnimation disarmAnimation = Animations.BIPED_DISARM_SHIELD;
		this.animator.playAnimation(disarmAnimation, 0.0F);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCPlayAnimation(disarmAnimation.getId(), this.orgEntity.getId(), 0.0F), this.orgEntity);
		return false;
	}
	
	@Override
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType)
	{
		ModifiableAttributeInstance mainhandAttackSpeed = this.orgEntity.getAttribute(Attributes.ATTACK_SPEED);
		ModifiableAttributeInstance offhandAttackSpeed = this.orgEntity.getAttribute(AttributeInit.OFFHAND_ATTACK_SPEED.get());
		
		mainhandAttackSpeed.removeModifier(WEIGHT_PENALTY_MODIFIIER);
		float mainWeaponSpeed = (float) mainhandAttackSpeed.getBaseValue();
		for(AttributeModifier attributeModifier : this.getOriginalEntity().getMainHandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
		{
			mainWeaponSpeed += (float)attributeModifier.getAmount();
		}
		
		mainhandAttackSpeed.addTransientModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
				Formulars.getAttackSpeedPenalty(this.getWeight(), mainWeaponSpeed, this), Operation.ADDITION));
		
		offhandAttackSpeed.removeModifier(WEIGHT_PENALTY_MODIFIIER);
		float offWeaponSpeed = (float) offhandAttackSpeed.getBaseValue();
		for(AttributeModifier attributeModifier : this.getOriginalEntity().getOffhandItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
		{
			offWeaponSpeed += (float)attributeModifier.getAmount();
		}
		
		offhandAttackSpeed.addTransientModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight penalty modifier",
				Formulars.getAttackSpeedPenalty(this.getWeight(), offWeaponSpeed, this), Operation.ADDITION));
	}
	
	public void modifiLivingMotions(CapabilityItem mainhand)
	{
		this.resetModifiedLivingMotions();

		if (mainhand != null)
		{
			Map<LivingMotion, StaticAnimation> motionChanger = mainhand.getLivingMotionChanges(this);
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
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(animation.getId(), this.orgEntity.getId(), 0.0F), this.orgEntity);
	}
	
	@Override
	public void changeYaw(float amount)
	{
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(this.orgEntity.getId(), yaw), this.orgEntity);
		ModNetworkManager.sendToPlayer(new STCNotifyPlayerYawChanged(this.orgEntity.getId(), yaw), this.orgEntity);
	}
	
	@Override
	public ServerPlayerEntity getOriginalEntity()
	{
		return orgEntity;
	}
	
	@Override
	public void aboutToDeath()
	{
		;
	}
}