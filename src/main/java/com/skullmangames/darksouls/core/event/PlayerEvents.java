package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.world.ModGamerules;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.server.STCGameruleChange;
import com.skullmangames.darksouls.network.server.STCGameruleChange.Gamerules;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
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
			PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			CapabilityItem itemCap = playerdata.getHeldItemCapability(Hand.MAIN_HAND);
			
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
		PlayerData<?> playerdata = (PlayerData<?>) event.getOriginal().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (playerdata.getOriginalEntity() != null)
		{
			playerdata.discard();
		}
	}
	
	@SubscribeEvent
	public static void itemUseStopEvent(LivingEntityUseItemEvent.Stop event)
	{
		if (!event.getEntity().level.isClientSide)
		{
			if (event.getEntity() instanceof ClientPlayerEntity)
			{
				ClientManager.INSTANCE.renderEngine.zoomOut(0);
			}
		}
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event)
	{
		if (event.getEntity() instanceof PlayerEntity)
		{
			if (event.getItem().getItem() instanceof BowItem)
			{
				PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerdata.isInaction())
				{
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void playerLogInEvent(PlayerLoggedInEvent event)
	{
		ModNetworkManager.sendToPlayer(new STCGameruleChange(Gamerules.SPEED_PENALTY_PERCENT,
				event.getEntity().level.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT)), (ServerPlayerEntity)event.getPlayer());
	}
	
	@SubscribeEvent
	public static void playerLogOutEvent(PlayerLoggedOutEvent event)
	{
		PlayerData<?> playerdata = (PlayerData<?>)event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerdata == null) return;
		playerdata.onSave();
	}
}