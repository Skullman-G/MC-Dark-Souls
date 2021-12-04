package com.skullmangames.darksouls.core.event;

import java.util.List;

import com.google.common.collect.Lists;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.animation.types.StaticAnimation;
import com.skullmangames.darksouls.common.capability.entity.BipedMobData;
import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.common.capability.entity.ServerPlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.common.potion.effect.UndeadCurse;
import com.skullmangames.darksouls.common.world.ModGamerules;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.ModAttributes;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.core.init.ModItems;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.DamageSourceExtended;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IndirectDamageSourceExtended;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.DamageType;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCPlayAnimation;
import com.skullmangames.darksouls.network.server.STCPotion;
import com.skullmangames.darksouls.network.server.STCPotion.Action;

import net.minecraft.command.arguments.EntityAnchorArgument.Type;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID)
public class EntityEvents
{
	private static List<EntityData<?>> unInitializedEntitiesClient = Lists.<EntityData<?>>newArrayList();
	private static List<EntityData<?>> unInitializedEntitiesServer = Lists.<EntityData<?>>newArrayList();
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public static void spawnEvent(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof ItemEntity && ((ItemEntity)event.getEntity()).getItem().getItem() == ModItems.DARKSIGN.get())
		{
			event.setCanceled(true);
			return;
		}
		
		@SuppressWarnings("rawtypes")
		EntityData entitydata = event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if(entitydata != null && event.getEntity().tickCount == 0)
		{
			entitydata.onEntityJoinWorld(event.getEntity());
			if(entitydata.isClientSide())
			{
				unInitializedEntitiesClient.add(entitydata);
			}
			else
			{
				unInitializedEntitiesServer.add(entitydata);
			}	
		}
		
		if (event.getEntity() instanceof ProjectileEntity)
		{
			ProjectileEntity projectileentity = (ProjectileEntity)event.getEntity();
			CapabilityProjectile<ProjectileEntity> projectileData = event.getEntity().getCapability(ModCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
			if(projectileData != null && event.getEntity().tickCount == 0)
			{
				projectileData.onJoinWorld(projectileentity);
			}
		}
	}
	
	@SubscribeEvent
	public static void updateEvent(LivingUpdateEvent event)
	{
		LivingData<?> entitydata = (LivingData<?>) event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if(entitydata != null && entitydata.getOriginalEntity() != null)
		{
			entitydata.update();
		}
	}
	
	@SubscribeEvent
	public static void knockBackEvent(LivingKnockBackEvent event)
	{
		EntityData<?> cap = event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void hurtEvent(LivingHurtEvent event)
	{
		IExtendedDamageSource extSource = null;
		Entity trueSource = event.getSource().getEntity();
		
		if(event.getSource() instanceof IExtendedDamageSource) extSource = (IExtendedDamageSource)event.getSource();
		
		LivingData<?> targetData = (LivingData<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if(trueSource != null)
		{
			// Projectile Damage
			if(event.getSource() instanceof IndirectEntityDamageSource)
			{
				Entity directSource = event.getSource().getDirectEntity();
				extSource = new IndirectDamageSourceExtended("arrow", trueSource, directSource, StunType.SHORT, DamageType.THRUST);
				
				CapabilityProjectile<?> projectileCap = directSource.getCapability(ModCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
				
				if (projectileCap != null)
				{
					extSource.setImpact(projectileCap.getImpact());
				}
			}
			
			if(extSource != null)
			{
				// Damage Calculation
				LivingEntity hitEntity = event.getEntityLiving();
				float damage = event.getAmount();
				
				if (extSource instanceof DamageSourceExtended)
				{
					double defense = 0.0D;
					
					// Physical
					switch(extSource.getAttackType())
					{
						default:
						case STANDARD:
							if (hitEntity.getAttributes().hasAttribute(ModAttributes.STANDARD_DEFENSE.get()))
							{
								defense = hitEntity.getAttributeValue(ModAttributes.STANDARD_DEFENSE.get());
							}
							break;
						
						case STRIKE:
							if (hitEntity.getAttributes().hasAttribute(ModAttributes.STRIKE_DEFENSE.get()))
							{
								defense = hitEntity.getAttributeValue(ModAttributes.STRIKE_DEFENSE.get());
							}
							break;
						
						case SLASH:
							if (hitEntity.getAttributes().hasAttribute(ModAttributes.SLASH_DEFENSE.get()))
							{
								defense = hitEntity.getAttributeValue(ModAttributes.SLASH_DEFENSE.get());
							}
							break;
						
						case THRUST:
							if (hitEntity.getAttributes().hasAttribute(ModAttributes.THRUST_DEFENSE.get()))
							{
								defense = hitEntity.getAttributeValue(ModAttributes.THRUST_DEFENSE.get());
							}
							break;
					}
					
					damage *= 1 - defense;
				}
				
				event.setAmount(damage);
				extSource.setAmount(damage);
				
				if (targetData != null && targetData instanceof LivingData<?>) targetData.blockingAttack(extSource);
				
				// Stun Animation
				if(damage > 0.0F)
				{
					LivingData<?> hitEntityData = (LivingData<?>)hitEntity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					
					if(hitEntityData != null)
					{
						StaticAnimation hitAnimation = null;
						float extendStunTime = 0;
						float knockBackAmount = 0;
						float weightReduction = 40.0F / (float)hitEntityData.getWeight();
						
						float currentStunResistance = hitEntityData.getStunArmor();
						if(currentStunResistance > 0)
						{
							float impact = extSource.getImpact();
							hitEntityData.setStunArmor(currentStunResistance - impact);
						}
						
						switch(extSource.getStunType())
						{
						case SHORT:
							if(hitEntityData.getStunArmor() == 0)
							{
								int i = EnchantmentHelper.getKnockbackBonus((LivingEntity)trueSource);
								float totalStunTime = (float) ((0.25F + extSource.getImpact() * 0.1F + 0.1F * i) * weightReduction);
								totalStunTime *= (1.0F - hitEntityData.getStunTimeTimeReduction());
								
								if(totalStunTime >= 0.1F)
								{
									extendStunTime = totalStunTime - 0.1F;
									boolean flag = totalStunTime >= 0.83F;
									StunType stunType = flag ? StunType.LONG : StunType.SHORT;
									extendStunTime = flag ? 0 : extendStunTime;
									hitAnimation = hitEntityData.getHitAnimation(stunType);
									knockBackAmount = totalStunTime;
								}
							}
							break;
						case LONG:
							hitAnimation = hitEntityData.getHitAnimation(StunType.LONG);
							knockBackAmount = (extSource.getImpact() * 0.25F) * weightReduction;
							break;
						case HOLD:
							hitAnimation = hitEntityData.getHitAnimation(StunType.SHORT);
							extendStunTime = extSource.getImpact() * 0.1F;
							break;
						}
						
						if(hitAnimation != null)
						{
							if(!(hitEntity instanceof PlayerEntity))
							{
								hitEntity.lookAt(Type.FEET, trueSource.getDeltaMovement());
							}
							hitEntityData.setStunTimeReduction();
							hitEntityData.getAnimator().playAnimation(hitAnimation, extendStunTime);
							ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(hitAnimation.getId(), hitEntity.getId(), extendStunTime), hitEntity);
							if(hitEntity instanceof ServerPlayerEntity)
							{
								ModNetworkManager.sendToPlayer(new STCPlayAnimation(hitAnimation.getId(), hitEntity.getId(), extendStunTime), (ServerPlayerEntity)hitEntity);
							}
						}
						
						hitEntityData.knockBackEntity(trueSource, knockBackAmount);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void attackEvent(LivingAttackEvent event)
	{
		LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (entitydata != null && !event.getEntity().level.isClientSide && event.getEntityLiving().getHealth() > 0.0F)
		{
			if (!entitydata.attackEntityFrom(event.getSource(), event.getAmount()))
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void arrowHitEvent(ProjectileImpactEvent.Arrow event)
	{
		if (event.getRayTraceResult() instanceof EntityRayTraceResult)
		{
			EntityRayTraceResult rayresult = ((EntityRayTraceResult) event.getRayTraceResult());
			if (rayresult.getEntity() != null && event.getArrow().getOwner() != null)
			{
				if (rayresult.getEntity().equals(event.getArrow().getOwner().getControllingPassenger()))
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
		
		LivingData<?> entitycap = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitycap != null && entitycap.getOriginalEntity() != null)
		{
			if (event.getSlot() == EquipmentSlotType.MAINHAND)
			{
				CapabilityItem fromCap = ModCapabilities.stackCapabilityGetter(event.getFrom());
				CapabilityItem toCap = ModCapabilities.stackCapabilityGetter(event.getTo());
				entitycap.cancelUsingItem();
				
				if(fromCap != null)
				{
					event.getEntityLiving().getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if(toCap != null)
				{
					event.getEntityLiving().getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if (entitycap instanceof ServerPlayerData)
				{
					ServerPlayerData playercap = (ServerPlayerData)entitycap;
					playercap.onHeldItemChange(toCap, event.getTo(), Hand.MAIN_HAND);
				}
			}
			else if (event.getSlot() == EquipmentSlotType.OFFHAND)
			{
				entitycap.cancelUsingItem();
				
				if (entitycap instanceof ServerPlayerData)
				{
					ServerPlayerData playercap = (ServerPlayerData)entitycap;
					CapabilityItem toCap = event.getTo().isEmpty() ? null : entitycap.getHeldItemCapability(Hand.MAIN_HAND);
					playercap.onHeldItemChange(toCap, event.getTo(), Hand.OFF_HAND);
				}
			}
			else if (event.getSlot().getType() == EquipmentSlotType.Group.ARMOR)
			{
				CapabilityItem fromCap = ModCapabilities.stackCapabilityGetter(event.getFrom());
				CapabilityItem toCap = ModCapabilities.stackCapabilityGetter(event.getTo());
				
				if(fromCap != null)
				{
					event.getEntityLiving().getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				if(toCap != null)
				{
					event.getEntityLiving().getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(event.getSlot(), entitycap));
				}
				
				LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				entitydata.onArmorSlotChanged(fromCap, toCap, event.getSlot());
			}
		}
	}
	
	@SubscribeEvent
	public static void effectAddEvent(PotionAddedEvent event)
	{
		if (event.getPotionEffect().getEffect() instanceof UndeadCurse && /*event.getPotionEffect().getEffect() != event.getOldPotionEffect().getEffect() &&*/ event.getEntityLiving() instanceof PlayerEntity)
		{
			ModEffects.UNDEAD_CURSE.get().onPotionAdd(((PlayerEntity)event.getEntityLiving()));
		}
		
		if(!event.getEntity().level.isClientSide)
		{
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getEffect(), Action.ACTIVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void effectRemoveEvent(PotionRemoveEvent event)
	{
		if (event.getPotion() instanceof UndeadCurse && event.getEntityLiving() instanceof PlayerEntity)
		{
			event.setCanceled(true);
			return;
			//EffectInit.UNDEAD_CURSE.get().onPotionRemove(((PlayerEntity)event.getEntityLiving()));
		}
		
		if(!event.getEntity().level.isClientSide && event.getPotionEffect() != null)
		{
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getEffect(), Action.REMOVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void effectExpiryEvent(PotionExpiryEvent event)
	{
		if (event.getPotionEffect().getEffect() instanceof UndeadCurse && event.getEntityLiving() instanceof PlayerEntity)
		{
			event.setCanceled(true);
			return;
			//EffectInit.UNDEAD_CURSE.get().onPotionRemove(((PlayerEntity)event.getEntityLiving()));
		}
		
		if(!event.getEntity().level.isClientSide)
		{
			ModNetworkManager.sendToAll(new STCPotion(event.getPotionEffect().getEffect(), Action.REMOVE, event.getEntity().getId()));
		}
	}
	
	@SubscribeEvent
	public static void mountEvent(EntityMountEvent event)
	{
		EntityData<?> mountEntity = event.getEntityMounting().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		World world = event.getWorldObj();
		if (!world.isClientSide && mountEntity instanceof BipedMobData && mountEntity.getOriginalEntity() != null)
		{
			if (event.getEntityBeingMounted() instanceof MobEntity)
			{
				((BipedMobData<?>) mountEntity).onMount(event.isMounting(), event.getEntityBeingMounted());
			}
		}
	}
	
	@SubscribeEvent
	public static void deathEvent(LivingDeathEvent event)
	{
		MobNBTManager.setHumanity(event.getEntityLiving(), 0);
		
		if (event.getEntityLiving().hasEffect(ModEffects.UNDEAD_CURSE.get())) MobNBTManager.setHuman(event.getEntityLiving(), false);
		
		LivingData<?> entitydata = (LivingData<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if(entitydata != null)
		{
			if (entitydata.isClientSide()) entitydata.playSound(com.skullmangames.darksouls.core.init.ModSoundEvents.GENERIC_KILL, 0.0F, 0.0F);
			entitydata.getAnimator().playDeathAnimation();
		}
	}
	
	@SubscribeEvent
	public static void fallEvent(LivingFallEvent event)
	{
		if (event.getEntity().level.getGameRules().getBoolean(ModGamerules.HAS_FALL_ANIMATION))
		{
			LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (entitydata != null && !entitydata.isInaction())
			{
				float distance = event.getDistance();

				if (distance > 5.0F)
				{
					entitydata.getAnimator().playAnimation(Animations.BIPED_LAND_DAMAGE, 0);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void changeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event)
	{
		PlayerEntity player = event.getPlayer();
		ServerPlayerData playerData = (ServerPlayerData) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		playerData.modifiLivingMotions(playerData.getHeldItemCapability(Hand.MAIN_HAND));
	}
	
	@SubscribeEvent
	public static void tickClientEvent(TickEvent.ClientTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			for (EntityData<?> cap : unInitializedEntitiesClient)
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
			for (EntityData<?> cap : unInitializedEntitiesServer)
			{
				cap.postInit();
			}
			unInitializedEntitiesServer.clear();
		}
	}
}