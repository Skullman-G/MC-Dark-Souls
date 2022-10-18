package com.skullmangames.darksouls.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.ModTitleScreen;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.client.gui.screens.ModLoadingScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenOpenEvent;
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
			AbstractWidget.WIDGETS_LOCATION = DS_WIDGETS_PATH;
			GuiComponent.BACKGROUND_LOCATION = DS_BACKGROUND_PATH;
		}
		else
		{
			AbstractWidget.WIDGETS_LOCATION = WIDGETS_PATH;
			GuiComponent.BACKGROUND_LOCATION = BACKGROUND_PATH;
		}
	}

	public static void renderDarkBackground(Screen screen)
	{
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
	    RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
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
		public static void onOpenScreen(ScreenOpenEvent event)
		{
			Screen gui = event.getScreen();

			if (ConfigManager.INGAME_CONFIG.darkSoulsUI.getValue())
			{
				if (minecraft.getOverlay() instanceof ModLoadingScreen)
				{
					Overlay overlay = minecraft.getOverlay();
					if (overlay instanceof ModLoadingScreen)
					{
						((ModLoadingScreen) overlay).setCanFadeOut(true);
					}
				}
				if (gui instanceof TitleScreen)
				{
					TitleScreen screen = (TitleScreen) gui;
					event.setScreen(new ModTitleScreen(screen.fading));
				}
				else if (gui instanceof LevelLoadingScreen)
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
					event.setScreen(new TitleScreen(screen.fading));
				}
			}
		}
	}
}
