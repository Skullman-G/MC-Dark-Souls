package com.skullmangames.darksouls.core.event;

import java.util.List;

import com.google.common.collect.Lists;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.HumanoidCap;
import com.skullmangames.darksouls.common.capability.entity.EquipLoaded;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerCap;
import com.skullmangames.darksouls.common.capability.item.AttributeItemCap;
import com.skullmangames.darksouls.common.capability.item.MeleeWeaponCap;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModSoundEvents;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCPotion;
import com.skullmangames.darksouls.network.server.STCPotion.Action;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID)
public class EntityEvents
{
	private static List<EntityCapability<?>> unInitializedEntitiesClient = Lists.<EntityCapability<?>>newArrayList();
	private static List<EntityCapability<?>> unInitializedEntitiesServer = Lists.<EntityCapability<?>>newArrayList();
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void spawnEvent(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof ItemEntity && ((ItemEntity)event.getEntity()).getItem().getItem() == ModItems.DARKSIGN.get())
		{
			event.setCanceled(true);
			return;
		}
		
		if (event.getEntity() instanceof Projectile) return;
		@SuppressWarnings("rawtypes")
		EntityCapability entityCap = event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if(entityCap != null && event.getEntity().tickCount == 0)
		{
			entityCap.onEntityJoinWorld(event.getEntity());
			if(entityCap.isClientSide())
			{
				unInitializedEntitiesClient.add(entityCap);
			}
			else
			{
				unInitializedEntitiesServer.add(entityCap);
			}	
		}
	}
	
	@SubscribeEvent
	public static void onItemRightClick(PlayerInteractEvent.RightClickItem event)
	{
		MeleeWeaponCap weaponCap = ModCapabilities.getMeleeWeaponCap(event.getItemStack());
		if (weaponCap == null) return;
		if (event.getHand() == InteractionHand.MAIN_HAND && ModCapabilities.getMeleeWeaponCap(event.getEntityLiving().getOffhandItem()) != null)
		{
			event.setCanceled(true);
			return;
		}
		event.setCancellationResult(weaponCap.onUse(event.getPlayer(), event.getHand()));
	}
	
	@SubscribeEvent
	public static void onStartUsingItem(LivingEntityUseItemEvent.Start event)
	{
		if (ModCapabilities.getMeleeWeaponCap(event.getItem()) != null) event.setDuration(72000);
	}
	
	@SubscribeEvent
	public static void updateEvent(LivingUpdateEvent event)
	{
		LivingCap<?> entityCap = (LivingCap<?>) event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if(entityCap != null && entityCap.getOriginalEntity() != null)
		{
			entityCap.update();
		}
	}
	
	@SubscribeEvent
	public static void knockBackEvent(LivingKnockBackEvent event)
	{
		EntityCapability<?> cap = event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void hurtEvent(LivingHurtEvent event)
	{
		LivingEntity target = event.getEntityLiving();
		LivingCap<?> targetData = (LivingCap<?>)target.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		float amount = event.getAmount();
		boolean indirect = event.getSource() instanceof IndirectEntityDamageSource;
		boolean headshot = false;
		float poiseDamage = 0.0F;
		DamageType damageType = DamageType.REGULAR;
		StunType stunType = StunType.DEFAULT;
		
		IExtendedDamageSource extSource = null;
		if(event.getSource() instanceof IExtendedDamageSource)
		{
			extSource = (IExtendedDamageSource)event.getSource();
			headshot = extSource.isHeadshot();
			poiseDamage = extSource.getPoiseDamage();
			damageType = extSource.getDamageType();
			stunType = extSource.getStunType();
		}
		
		// Damage Calculation
		if (!indirect)
		{
			Attribute defAttribute = damageType.getDefenseAttribute();
			amount -= target.getAttribute(defAttribute) != null ? target.getAttributeValue(defAttribute) : 0.0F;
		}
		if (extSource != null) extSource.setAmount(amount);
		if (targetData == null || targetData.blockingAttack(extSource)) return;
		
		target.level.playSound(null, target.blockPosition(), ModSoundEvents.GENERIC_HIT.get(), target.getSoundSource(), 1.0F, 1.0F);
		
		// Stun Animation
		boolean poiseBroken = targetData.decreasePoiseDef(poiseDamage);
		if (!poiseBroken && !headshot) stunType = stunType.downgrade();
		StaticAnimation hitAnimation = targetData.getHitAnimation(stunType);
		
		if(hitAnimation != null)
		{
			float exTime = 0.2F;
			targetData.getAnimator().playAnimation(hitAnimation, exTime);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(hitAnimation, target.getId(), exTime), target);
			if(target instanceof ServerPlayer)
			{
				ModNetworkManager.sendToPlayer(new STCPlayAnimation(hitAnimation, target.getId(), exTime), (ServerPlayer)target);
			}
		}
	}
	
	@SubscribeEvent
	public static void damageEvent(LivingDamageEvent event)
	{
		if (!(event.getSource() instanceof IExtendedDamageSource)) return;
		event.setAmount(((IExtendedDamageSource)event.getSource()).getAmount());
	}
	
	@SubscribeEvent
	public static void attackEvent(LivingAttackEvent event)
	{
		LivingCap<?> entityCap = (LivingCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (entityCap != null && !event.getEntity().level.isClientSide && event.getEntityLiving().getHealth() > 0.0F)
		{
			if (!entityCap.attackEntityFrom(event.getSource(), event.getAmount()))
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void arrowHitEvent(ProjectileImpactEvent event)
	{
		if (event.getProjectile() instanceof Arrow && event.getRayTraceResult() instanceof EntityHitResult)
		{
			EntityHitResult rayresult = ((EntityHitResult) event.getRayTraceResult());
			if (rayresult.getEntity() != null && event.getProjectile().getOwner() != null)
			{
				if (rayresult.getEntity().equals(event.getProjectile().getOwner().getControllingPassenger()))
				{
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void equipChangeEvent(LivingEquipmentChangeEvent event)
	{
		if(event.getFrom().getItem() == event.getTo().getItem()) return;
		
		LivingCap<?> entityCap = (LivingCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (entityCap == null || entityCap.getOriginalEntity() == null) return;
		
		AttributeItemCap fromCap = ModCapabilities.getAttributeItemCap(event.getFrom());
		AttributeItemCap toCap = ModCapabilities.getAttributeItemCap(event.getTo());
		
		if(fromCap != null)
		{
			event.getEntityLiving().getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(event.getSlot()));
		}
		
		if(toCap != null)
		{
			event.getEntityLiving().getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(event.getSlot()));
		}
		
		if (event.getSlot().getType() == EquipmentSlot.Type.ARMOR)
		{
			entityCap.onArmorSlotChanged(fromCap, toCap, event.getSlot());
		}
		else
		{
			entityCap.cancelUsingItem();
			if (entityCap instanceof ServerPlayerCap)
			{
				((ServerPlayerCap)entityCap).onHeldItemChange(toCap, event.getTo(), event.getSlot() == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND
						: InteractionHand.OFF_HAND);
			}
		}
		
		if (entityCap instanceof EquipLoaded)
		{
			AttributeInstance speed = entityCap.getOriginalEntity().getAttribute(Attributes.MOVEMENT_SPEED);
			speed.removeModifier(ModAttributes.MOVEMENT_SPEED_MODIFIER_UUID);
			speed.addTransientModifier(ModAttributes.getMovementSpeedModifier(((EquipLoaded)entityCap).getEquipLoadLevel()));
		}
	}
	
	@SubscribeEvent
	public static void effectAddEvent(PotionAddedEvent event)
	{
		if(!event.getEntity().level.isClientSide)
		{
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getEffect(), Action.ACTIVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void effectRemoveEvent(PotionRemoveEvent event)
	{
		if(!event.getEntity().level.isClientSide && event.getPotionEffect() != null)
		{
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getEffect(), Action.REMOVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void effectExpiryEvent(PotionExpiryEvent event)
	{
		if(!event.getEntity().level.isClientSide)
		{
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getEffect(), Action.REMOVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void mountEvent(EntityMountEvent event)
	{
		EntityCapability<?> mountEntity = event.getEntityMounting().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		Level world = event.getWorldObj();
		if (!world.isClientSide && mountEntity instanceof HumanoidCap && mountEntity.getOriginalEntity() != null)
		{
			if (event.getEntityBeingMounted() instanceof Mob)
			{
				((HumanoidCap<?>) mountEntity).onMount(event.isMounting(), event.getEntityBeingMounted());
			}
		}
	}
	
	@SubscribeEvent
	public static void deathEvent(LivingDeathEvent event)
	{
		LivingCap<?> entityCap = (LivingCap<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if(entityCap == null) return;
		if (entityCap instanceof PlayerCap<?> && !entityCap.isClientSide())
		{
			PlayerCap<?> playerdata = (PlayerCap<?>)entityCap;
			playerdata.setHumanity(0);
			playerdata.setHuman(false);
			playerdata.setSouls(0);
			playerdata.onSave();
		}
		
		if (entityCap.isClientSide()) entityCap.playSound(ModSoundEvents.GENERIC_KILL.get(), 0.0F, 0.0F);
		entityCap.getAnimator().playDeathAnimation();
	}
	
	@SubscribeEvent
	public static void fallEvent(LivingFallEvent event)
	{
		LivingCap<?> entityCap = (LivingCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entityCap != null && !entityCap.isInaction())
		{
			float distance = event.getDistance();

			if (distance > 5.0F)
			{
				entityCap.getAnimator().playAnimation(Animations.BIPED_LAND_DAMAGE, 0);
			}
		}
	}
	
	@SubscribeEvent
	public static void onVanillaShieldBlock(ShieldBlockEvent event)
	{
		event.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void changeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		Player player = event.getPlayer();
		ServerPlayerCap playerData = (ServerPlayerCap) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		playerData.modifyLivingMotions(playerData.getHeldItemCapability(InteractionHand.MAIN_HAND));
	}
	
	@SubscribeEvent
	public static void tickClientEvent(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			for (EntityCapability<?> cap : unInitializedEntitiesClient)
			{
				cap.postInit();
			}
			unInitializedEntitiesClient.clear();
		}
	}
	
	@SubscribeEvent
	public static void tickServerEvent(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			for (EntityCapability<?> cap : unInitializedEntitiesServer)
			{
				cap.postInit();
			}
			unInitializedEntitiesServer.clear();
		}
	}
}