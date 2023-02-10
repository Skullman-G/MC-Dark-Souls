package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID)
public class PlayerEvents
{
	@SubscribeEvent
	public static void itemUseStartEvent(LivingEntityUseItemEvent.Start event)
	{
		if (event.getEntity() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)event.getEntity();
			PlayerCap<?> playerCap = (PlayerCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			ItemCapability itemCap = playerCap.getHeldItemCapability(Hand.MAIN_HAND);
			
			if (playerCap.isInaction())
			{
				event.setCanceled(true);
			}
			else if (event.getItem() == player.getOffhandItem() && itemCap != null && itemCap.isTwoHanded())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event)
	{
		PlayerCap<?> playerCap = (PlayerCap<?>) event.getOriginal().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (playerCap != null && playerCap.getOriginalEntity() != null)
		{
			playerCap.discard();
		}
		
		event.getPlayer().getPersistentData().put(DarkSouls.MOD_ID, event.getOriginal().getPersistentData().getCompound(DarkSouls.MOD_ID));
	}
	
	@SubscribeEvent
	public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event)
	{
		event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
		PlayerCap<?> playerCap = (PlayerCap<?>) event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerCap == null) return;
		playerCap.setFP(playerCap.getMaxFP());
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event)
	{
		if (event.getEntity() instanceof PlayerEntity)
		{
			if (event.getItem().getItem() instanceof BowItem)
			{
				PlayerCap<?> playerCap = (PlayerCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerCap.isInaction())
				{
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void playerLogOutEvent(PlayerLoggedOutEvent event)
	{
		PlayerCap<?> playerCap = (PlayerCap<?>)event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerCap == null) return;
		playerCap.onSave();
	}
	
	@SubscribeEvent
	public static void playerDeathEvent(LivingDeathEvent event)
	{
		if (!(event.getEntityLiving() instanceof ServerPlayerEntity)) return;
		PlayerCap<?> playerCap = (PlayerCap<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerCap == null) return;
		playerCap.onSave();
	}
}