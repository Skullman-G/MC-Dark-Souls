package com.skullmangames.darksouls.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.ModTitleScreen;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.client.gui.screens.ModLoadingScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class ScreenManager
{
	public static final ResourceLocation WIDGETS_PATH = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation DS_WIDGETS_PATH = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/widgets.png");

	public static final ResourceLocation BACKGROUND_PATH = new ResourceLocation("textures/gui/options_background.png");
	public static final ResourceLocation DS_BACKGROUND_PATH = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/options_background.png");
	
	public static void onDarkSoulsUIChanged(boolean value)
	{
		if (value)
		{
			Widget.WIDGETS_LOCATION = DS_WIDGETS_PATH;
			AbstractGui.BACKGROUND_LOCATION = DS_BACKGROUND_PATH;
		}
		else
		{
			Widget.WIDGETS_LOCATION = WIDGETS_PATH;
			AbstractGui.BACKGROUND_LOCATION = BACKGROUND_PATH;
		}
	}

	@SuppressWarnings("deprecation")
	public static void renderDarkBackground(Screen screen)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(Screen.BACKGROUND_LOCATION);
	    RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.vertex(0.0D, (double) screen.height, 0.0D).uv(0.0F, (float) screen.height / 32.0F + (float) 0).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex((double) screen.width, (double) screen.height, 0.0D)
				.uv((float) screen.width / 32.0F, (float) screen.height / 32.0F + (float) 0).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex((double) screen.width, 0.0D, 0.0D).uv((float) screen.width / 32.0F, (float) 0).color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float) 0).color(0, 0, 0, 255).endVertex();
		tessellator.end();
	}

	@Mod.EventBusSubscriber(modid = DarkSouls.MOD_ID, value = Dist.CLIENT)
	public static class Events
	{
		private static Minecraft minecraft = Minecraft.getInstance();

		@SubscribeEvent
		public static void onOpenScreen(GuiOpenEvent event)
		{
			Screen gui = event.getGui();

			if (ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue())
			{
				if (minecraft.getOverlay() instanceof ModLoadingScreen)
				{
					LoadingGui overlay = minecraft.getOverlay();
					if (overlay instanceof ModLoadingScreen)
					{
						((ModLoadingScreen) overlay).setCanFadeOut(true);
					}
				}
				
				if (gui instanceof MainMenuScreen)
				{
					event.setGui(new ModTitleScreen(((MainMenuScreen)gui).fading));
				}
				else if (gui instanceof WorldLoadProgressScreen)
				{
					event.setCanceled(true);
					minecraft.setOverlay(new ModLoadingScreen());
				}
			}
			else
			{
				if (gui instanceof ModTitleScreen)
				{
					ModTitleScreen screen = (ModTitleScreen) gui;
					event.setGui(new MainMenuScreen(screen.fading));
				}
			}
		}
	}
}
