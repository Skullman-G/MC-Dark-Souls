package com.skullmangames.darksouls.core.init;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.common.ModCapabilities;
import com.skullmangames.darksouls.common.entities.PlayerData;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.network.ModNetworkManager;
import com.skullmangames.darksouls.network.play.server.STCGameruleChange;
import com.skullmangames.darksouls.network.play.server.STCGameruleChange.Gamerules;
import com.skullmangames.darksouls.world.ModGamerules;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID)
public class PlayerEvents
{
	@SubscribeEvent
	public static void arrowLooseEvent(ArrowLooseEvent event)
	{
		Colliders.update();
	}
	
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
				ClientEngine.INSTANCE.renderEngine.zoomOut(0);
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
	public static void attackEntityEvent(AttackEntityEvent event)
	{
		if (!event.getEntity().level.getGameRules().getBoolean(ModGamerules.DO_VANILLA_ATTACK))
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void playerLogInEvent(PlayerLoggedInEvent event)
	{
		ModNetworkManager.sendToPlayer(new STCGameruleChange(Gamerules.HAS_FALL_ANIMATION,
					event.getEntity().level.getGameRules().getBoolean(ModGamerules.HAS_FALL_ANIMATION)), (ServerPlayerEntity)event.getPlayer());
		ModNetworkManager.sendToPlayer(new STCGameruleChange(Gamerules.SPEED_PENALTY_PERCENT,
				event.getEntity().level.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT)), (ServerPlayerEntity)event.getPlayer());
	}
}