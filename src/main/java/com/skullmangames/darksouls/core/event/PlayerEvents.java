package com.skullmangames.darksouls.core.event;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.PlayerData;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.InteractionHand;
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
			PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			CapabilityItem itemCap = playerdata.getHeldItemCapability(InteractionHand.MAIN_HAND);
			
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

		if (playerdata != null && playerdata.getOriginalEntity() != null)
		{
			playerdata.discard();
		}
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event)
	{
		if (event.getEntity() instanceof Player)
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
	public static void playerLogOutEvent(PlayerLoggedOutEvent event)
	{
		PlayerData<?> playerdata = (PlayerData<?>)event.getPlayer().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		if (playerdata == null) return;
		playerdata.onSave();
	}
}