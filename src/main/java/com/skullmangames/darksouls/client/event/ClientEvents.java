package com.skullmangames.darksouls.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.GameOverlayManager;
import com.skullmangames.darksouls.common.capability.item.CapabilityItem;
import com.skullmangames.darksouls.core.init.ModEffects;
import com.skullmangames.darksouls.core.init.ModCapabilities;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
