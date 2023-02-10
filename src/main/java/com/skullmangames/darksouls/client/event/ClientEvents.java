package com.skullmangames.darksouls.client.event;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.client.gui.screens.AttunementScreen;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent.DrawForeground;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents
{
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final Pair<ResourceLocation, ResourceLocation> OFFHAND_TEXTURE = Pair.of(PlayerContainer.BLOCK_ATLAS,
			PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);

	@SubscribeEvent
	public static void itemUseStopEvent(LivingEntityUseItemEvent.Stop event)
	{
		if (event.getEntity() instanceof ClientPlayerEntity)
		{
			ClientManager.INSTANCE.renderEngine.zoomOut(0);
		}
	}
	
	@SubscribeEvent
	public static void onLivingHeal(final LivingHealEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving().getUUID() == minecraft.player.getUUID() && !event.getEntityLiving().isSpectator())
		{
			GameOverlayManager.isHealing = true;
		}
	}
	
	@SubscribeEvent
	public static void mouseClickEvent(MouseClickedEvent.Pre event)
	{
		if (event.getGui() instanceof ContainerScreen)
		{
			Slot slotUnderMouse = ((ContainerScreen<?>) event.getGui()).getSlotUnderMouse();

			if (slotUnderMouse != null)
			{
				ItemCapability cap = minecraft.player.inventory.getCarried().getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);

				if (cap != null && !cap.canUsedInOffhand())
				{
					if (slotUnderMouse.getNoItemIcon() != null && slotUnderMouse.getNoItemIcon().equals(OFFHAND_TEXTURE))
						event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void mouseReleaseEvent(MouseReleasedEvent.Pre event)
	{
		if (event.getGui() instanceof ContainerScreen)
		{
			Slot slotUnderMouse = ((ContainerScreen<?>) event.getGui()).getSlotUnderMouse();

			if (slotUnderMouse != null)
			{
				ItemCapability cap = minecraft.player.inventory.getCarried().getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);

				if (cap != null && !cap.canUsedInOffhand())
				{
					if (slotUnderMouse.getNoItemIcon() != null && slotUnderMouse.getNoItemIcon().equals(OFFHAND_TEXTURE))
						event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onDrawForeground(DrawForeground event)
	{
		if (event.getGuiContainer() instanceof AttunementScreen)
		{
			((AttunementScreen)event.getGuiContainer()).renderFg(event.getMatrixStack(), event.getMouseX(), event.getMouseY());
		}
	}
}
