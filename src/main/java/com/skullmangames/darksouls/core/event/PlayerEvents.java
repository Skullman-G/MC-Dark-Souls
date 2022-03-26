package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.PlayerCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player)event.getEntity();
			PlayerCap<?> playerdata = (PlayerCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			ItemCapability itemCap = playerdata.getHeldItemCapability(InteractionHand.MAIN_HAND);
			
			if (playerdata.isInaction())
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
		PlayerCap<?> playerdata = (PlayerCap<?>) event.getOriginal().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (playerdata != null && playerdata.getOriginalEntity() != null)
		{
			playerdata.discard();
		}
		
		event.getPlayer().getPersistentData().put(DarkSouls.MOD_ID, event.getOriginal().getPersistentData().getCompound(DarkSouls.MOD_ID));
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event)
	{
		if (event.getEntity() instanceof Player)
		{
			if (event.getItem().getItem() instanceof BowItem)
			{
				PlayerCap<?> playerdata = (PlayerCap<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerdata.isInaction())
				{
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void playerLogOutEvent(PlayerLoggedOutEvent event)
	{
		PlayerCap<?> playerdata = (PlayerCap<?>)event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerdata == null) return;
		playerdata.onSave();
	}
	
	@SubscribeEvent
	public static void playerDeathEvent(LivingDeathEvent event)
	{
		if (!(event.getEntityLiving() instanceof ServerPlayer)) return;
		PlayerCap<?> playerdata = (PlayerCap<?>)event.getEntityLiving().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerdata == null) return;
		playerdata.onSave();
	}
}