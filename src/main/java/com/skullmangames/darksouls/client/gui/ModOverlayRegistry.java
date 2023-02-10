package com.skullmangames.darksouls.client.gui;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.api.distmarker.Dist;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ModOverlayRegistry
{
	private static List<ModIngameOverlay> overlays = new ArrayList<>();
	private static boolean renderHealth = true;
	private static boolean renderArmor = true;
	private static boolean renderFood = true;
	private static boolean renderBossHealth = true;
	
	public static void enablePlayerHealth(boolean value)
	{
		renderHealth = value;
	}
	
	public static void enableArmorLevel(boolean value)
	{
		renderArmor = value;
	}
	
	public static void enableFoodLevel(boolean value)
	{
		renderFood = value;
	}
	
	public static void enableBossHealth(boolean value)
	{
		renderBossHealth = value;
	}
	
	public static void registerOverlayTop(String name, ModIngameOverlay overlay)
	{
		overlays.add(overlay);
	}
	
	public static void registerOverlayBottom(String name, ModIngameOverlay overlay)
	{
		overlays.add(0, overlay);
	}
	
	@SubscribeEvent
	public static void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		MainWindow window = event.getWindow();
		MatrixStack matStack = event.getMatrixStack();
		int width = window.getGuiScaledWidth();
		int height = window.getGuiScaledHeight();
		float partialTicks = event.getPartialTicks();

		switch (event.getType())
		{
			case HEALTH:
				if (!renderHealth) event.setCanceled(true);
				break;
				
			case ARMOR:
				if (!renderArmor) event.setCanceled(true);
	
			case FOOD:
				if (!(minecraft.getCameraEntity() instanceof LivingEntity)) break;
				if (!renderFood) event.setCanceled(true);
				break;
				
			case BOSSHEALTH:
				if (!renderBossHealth) event.setCanceled(true);
				break;
	
			case ALL:
				for (int i = 0; i < overlays.size(); i++) overlays.get(i).render((ForgeIngameGui)minecraft.gui, matStack, partialTicks, width, height);
				break;
	
			default:
				minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
				break;
		}
	}
	
	public interface ModIngameOverlay
	{
		void render(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight);
	}
}
