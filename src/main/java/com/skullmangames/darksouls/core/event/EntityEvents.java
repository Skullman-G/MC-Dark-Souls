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
import com.skullmangames.darksouls.common.capability.item.IShield;
import com.skullmangames.darksouls.common.capability.projectile.CapabilityProjectile;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.common.potion.effect.UndeadCurse;
import com.skullmangames.darksouls.common.world.ModGamerules;
import com.skullmangames.darksouls.core.init.Animations;
import com.skullmangames.darksouls.core.init.EffectInit;
import com.skullmangames.darksouls.core.init.ItemInit;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource;
import com.skullmangames.darksouls.core.util.IndirectDamageSourceExtended;
import com.skullmangames.darksouls.core.util.IExtendedDamageSource.StunType;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.client.CTSPlayAnimation;
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
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.CombatRules;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
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
		if (event.getEntity() instanceof ItemEntity && ((ItemEntity)event.getEntity()).getItem().getItem() == ItemInit.DARKSIGN.get())
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
		
		if(trueSource != null)
		{
			if(event.getSource() instanceof IndirectEntityDamageSource)
			{/*
				EntityData<?> attackerdata = trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if(attackerdata != null) {
					if(attackerdata instanceof IRangedAttackMobCapability) {
						extSource = ((IRangedAttackMobCapability)attackerdata).getRangedDamageSource(event.getSource().getDirectEntity());
					} else if(event.getSource().damageType.equals("arrow")) {
						extSource = new IndirectDamageSourceExtended("arrow", trueSource, event.getSource().getDirectEntity(), StunType.SHORT);
						extSource.setImpact(1.0F);
					}
				}*/
				
				Entity directSource = event.getSource().getDirectEntity();
				extSource = new IndirectDamageSourceExtended("arrow", trueSource, directSource, StunType.SHORT);
				
				CapabilityProjectile<?> projectileCap = directSource.getCapability(ModCapabilities.CAPABILITY_PROJECTILE, null).orElse(null);
				
				if (projectileCap != null)
				{
					extSource.setArmorNegation(projectileCap.getArmorNegation());
					extSource.setImpact(projectileCap.getImpact());
				}
			}
			
			if(extSource != null)
			{
				float totalDamage = event.getAmount();
				float ignoreDamage = event.getAmount() * extSource.getArmorNegation() * 0.01F;
				float calculatedDamage = ignoreDamage;
				LivingEntity hitEntity = event.getEntityLiving();
				
			    if(hitEntity.hasEffect(Effects.DAMAGE_RESISTANCE))
			    {
			    	int i = (hitEntity.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
			        int j = 25 - i;
			        float f = calculatedDamage * (float)j;
			        float f1 = calculatedDamage;
			        calculatedDamage = Math.max(f / 25.0F, 0.0F);
			        float f2 = f1 - calculatedDamage;
			        if (f2 > 0.0F && f2 < 3.4028235E37F)
			        {
			        	if (hitEntity instanceof ServerPlayerEntity)
			        	{
			        		((ServerPlayerEntity)hitEntity).awardStat(Stats.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
			        	} else if(event.getSource().getDirectEntity() instanceof ServerPlayerEntity) {
			                ((ServerPlayerEntity)event.getSource().getDirectEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
			        	}
			        }
			    }
			    
			    if(calculatedDamage > 0.0F)
			    {
			    	int k = EnchantmentHelper.getDamageProtection(hitEntity.getArmorSlots(), event.getSource());
			        if(k > 0)
			        {
			        	calculatedDamage = CombatRules.getDamageAfterMagicAbsorb(calculatedDamage, (float)k);
			        }
			    }
			    
			    float absorpAmount = hitEntity.getAbsorptionAmount() - calculatedDamage;
			    hitEntity.setAbsorptionAmount(Math.max(absorpAmount, 0.0F));
		        float realHealthDamage = Math.max(-absorpAmount, 0.0F);
		        if (realHealthDamage > 0.0F && realHealthDamage < 3.4028235E37F && event.getSource().getDirectEntity() instanceof ServerPlayerEntity)
		        {
		        	((ServerPlayerEntity)event.getSource().getDirectEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(realHealthDamage * 10.0F));
		        }
		        
		        if(absorpAmount < 0.0F)
		        {
		        	hitEntity.setHealth(hitEntity.getHealth() + absorpAmount);
		        	LivingData<?> attacker = (LivingData<?>)trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
					if(attacker != null)
					{
						attacker.gatherDamageDealt(extSource, calculatedDamage);
					}
		        }
		        
				event.setAmount(totalDamage - ignoreDamage);
				
				if(event.getAmount() + ignoreDamage > 0.0F)
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
							if(!hitEntity.hasEffect(ModEffects.STUN_IMMUNITY) && (hitEntityData.getStunArmor() == 0))
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
							hitAnimation = hitEntity.hasEffect(ModEffects.STUN_IMMUNITY) ? null : hitEntityData.getHitAnimation(StunType.LONG);
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
		
		if(event.getEntityLiving().isBlocking())
		{
			if(event.getEntityLiving() instanceof PlayerEntity)
			{
				event.getEntityLiving().level.playSound((PlayerEntity)event.getEntityLiving(), event.getEntityLiving().blockPosition(), SoundEvents.SHIELD_BLOCK,
						event.getEntityLiving().getSoundSource(), 1.0F, 0.8F + event.getEntityLiving().getRandom().nextFloat() * 0.4F);
			}
			
			if (extSource != null && !(extSource instanceof IndirectEntityDamageSource))
			{
				LivingEntity target = event.getEntityLiving();
				CapabilityItem cap = ModCapabilities.stackCapabilityGetter(target.getUseItem());
				
				if (cap instanceof IShield)
				{
					IShield shield = (IShield)cap;
					float health = target.getHealth() - (extSource.getAmount() * (1 - shield.getPhysicalDefense()));
					if (health < 0) health = 0;
					target.setHealth(health);
					
					if (extSource.getRequiredDeflectionLevel() <= shield.getDeflectionLevel())
					{
						LivingData<?> attacker = (LivingData<?>)trueSource.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
						if (attacker != null)
						{
							StaticAnimation deflectAnimation = attacker.getDeflectAnimation();
							if (deflectAnimation != null)
							{
								float stuntime = 0.0F;
								attacker.setStunTimeReduction();
								attacker.getAnimator().playAnimation(deflectAnimation, stuntime);
								ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(deflectAnimation.getId(), trueSource.getId(), stuntime), trueSource);
								if(trueSource instanceof ServerPlayerEntity)
								{
									ModNetworkManager.sendToPlayer(new STCPlayAnimation(deflectAnimation.getId(), trueSource.getId(), stuntime), (ServerPlayerEntity)trueSource);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void damageEvent(LivingDamageEvent event) {}
	
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
			EffectInit.UNDEAD_CURSE.get().onPotionAdd(((PlayerEntity)event.getEntityLiving()));
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
	
	/*@SubscribeEvent
	public static void tpEvent(EnderTeleportEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (event.getEntityLiving() instanceof EndermanEntity) {
			EndermanEntity enderman = (EndermanEntity)entity;
			EndermanData endermandata = (EndermanData) enderman.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (endermandata != null) {
				if (endermandata.isInaction()) {
					for (Entity collideEntity : enderman.world.getEntitiesWithinAABB(Entity.class, enderman.getBoundingBox().grow(0.2D, 0.2D, 0.2D))) {
						if (collideEntity instanceof ProjectileEntity) {
	                    	return;
	                    }
	                }
					
					event.setCanceled(true);
				} else if (endermandata.isRaging()) {
					event.setCanceled(true);
				}
			}
		}
	}*/
	
	@SubscribeEvent
	public static void jumpEvent(LivingJumpEvent event)
	{
		LivingData<?> entitydata = (LivingData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (entitydata != null && entitydata.isClientSide())
		{
			if (!entitydata.isInaction() && !event.getEntity().isInWater())
			{
				StaticAnimation jumpAnimation = entitydata.getClientAnimator().getJumpAnimation();
				entitydata.getAnimator().playAnimation(jumpAnimation, 0);
				ModNetworkManager.sendToServer(new CTSPlayAnimation(jumpAnimation.getId(), 0, true, false));
			}
		}
	}
	
	@SubscribeEvent
	public static void deathEvent(LivingDeathEvent event)
	{
		MobNBTManager.setHumanity(event.getEntityLiving(), 0);
		
		if (event.getEntityLiving().hasEffect(EffectInit.UNDEAD_CURSE.get())) MobNBTManager.setHuman(event.getEntityLiving(), false);
		
		LivingData<?> entitydata = (LivingData<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if(entitydata != null)
		{
			if (entitydata.isClientSide()) entitydata.playSound(com.skullmangames.darksouls.core.init.SoundEvents.GENERIC_KILL, 0.0F, 0.0F);
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