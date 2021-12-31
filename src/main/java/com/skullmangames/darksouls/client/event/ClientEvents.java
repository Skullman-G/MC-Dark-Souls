package com.skullmangames.darksouls.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.client.renderer.FirstPersonRendererOverride;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.common.item.IHaveDarkSoulsUseAction;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.server.IntegratedPlayerListOverride;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

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
	public static void onServerAboutToStart(final FMLServerAboutToStartEvent event)
    {
		MinecraftServer server = event.getServer();
		if (server instanceof IntegratedServer)
		{
			server.setPlayerList(new IntegratedPlayerListOverride((IntegratedServer)server, server.registryHolder, server.playerDataStorage));
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
	public static void onRenderHand(final RenderHandEvent event)
	{
		if (event.getItemStack().getItem() instanceof IHaveDarkSoulsUseAction)
		{
			event.setCanceled(true);
			Minecraft minecraft = Minecraft.getInstance();
			IHaveDarkSoulsUseAction item = (IHaveDarkSoulsUseAction)event.getItemStack().getItem();
			FirstPersonRendererOverride.renderArmWithItem(item, event.getSwingProgress(), event.getPartialTicks(), event.getEquipProgress(), event.getHand(), event.getItemStack(), event.getMatrixStack(), event.getBuffers(), minecraft.getEntityRenderDispatcher().getPackedLightCoords(minecraft.player, event.getPartialTicks()));
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
				CapabilityItem cap = minecraft.player.inventory.getCarried().getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);

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
				CapabilityItem cap = minecraft.player.inventory.getCarried().getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);

				if (cap != null && !cap.canUsedInOffhand())
				{
					if (slotUnderMouse.getNoItemIcon() != null && slotUnderMouse.getNoItemIcon().equals(OFFHAND_TEXTURE))
						event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		MainWindow window = event.getWindow();
		MatrixStack matStack = event.getMatrixStack();

		switch (event.getType())
		{
		case HEALTH:
			event.setCanceled(true);
			GameOverlayManager.renderHealth(window, matStack);
			break;

		case FOOD:
			if (!(minecraft.getCameraEntity() instanceof LivingEntity)
					|| !((LivingEntity) minecraft.getCameraEntity()).hasEffect(ModEffects.UNDEAD_CURSE.get()))
				break;
			event.setCanceled(true);
			GameOverlayManager.renderStamina(window, matStack);
			break;

		case CROSSHAIRS:
			event.setCanceled(true);
			minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			GameOverlayManager.renderCrosshair(window, matStack);
			break;

		case ALL:
			GameOverlayManager.renderAdditional(window, matStack);
			break;

		default:
			minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
			break;
		}
	}
}
