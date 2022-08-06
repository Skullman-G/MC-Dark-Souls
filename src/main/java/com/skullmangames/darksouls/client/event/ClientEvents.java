package com.skullmangames.darksouls.client.event;

import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.client.gui.screens.AttunementScreen;
import com.skullmangames.darksouls.client.renderer.FirstPersonRendererOverride;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.common.item.IHaveDarkSoulsUseAction;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.client.event.ContainerScreenEvent.DrawForeground;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.ScreenEvent.MouseReleasedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents
{
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final Pair<ResourceLocation, ResourceLocation> OFFHAND_TEXTURE = Pair.of(InventoryMenu.BLOCK_ATLAS,
			InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);

	@SubscribeEvent
	public static void itemUseStopEvent(LivingEntityUseItemEvent.Stop event)
	{
		if (event.getEntity() instanceof LocalPlayer)
		{
			ClientManager.INSTANCE.renderEngine.zoomOut(0);
		}
	}
	
	@SubscribeEvent
	public static void onLivingHeal(final LivingHealEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (event.getEntityLiving() instanceof Player && event.getEntityLiving().getUUID() == minecraft.player.getUUID() && !event.getEntityLiving().isSpectator())
		{
			GameOverlayManager.isHealing = true;
		}
	}
	
	@SubscribeEvent
	public static void onRenderHand(final RenderHandEvent event)
	{
		if (event.getItemStack().getItem() instanceof IHaveDarkSoulsUseAction)
		{
			event.setCanceled(true);
			Minecraft minecraft = Minecraft.getInstance();
			IHaveDarkSoulsUseAction item = (IHaveDarkSoulsUseAction)event.getItemStack().getItem();
			FirstPersonRendererOverride.renderArmWithItem(item, event.getSwingProgress(), event.getPartialTicks(), event.getEquipProgress(), event.getHand(), event.getItemStack(), event.getPoseStack(), event.getMultiBufferSource(), minecraft.getEntityRenderDispatcher().getPackedLightCoords(minecraft.player, event.getPartialTicks()));
		}
	}
	
	@SubscribeEvent
	public static void mouseClickEvent(MouseClickedEvent.Pre event)
	{
		if (event.getScreen() instanceof ContainerScreen)
		{
			Slot slotUnderMouse = ((ContainerScreen) event.getScreen()).getSlotUnderMouse();

			if (slotUnderMouse != null)
			{
				ItemCapability cap = minecraft.player.inventoryMenu.getCarried().getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);

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
		if (event.getScreen() instanceof ContainerScreen)
		{
			Slot slotUnderMouse = ((ContainerScreen) event.getScreen()).getSlotUnderMouse();

			if (slotUnderMouse != null)
			{
				ItemCapability cap = minecraft.player.inventoryMenu.getCarried().getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);

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
		if (event.getContainerScreen() instanceof AttunementScreen)
		{
			((AttunementScreen)event.getContainerScreen()).renderFg(event.getPoseStack(), event.getMouseX(), event.getMouseY());
		}
	}
}
