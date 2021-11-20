package com.skullmangames.darksouls.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.gui.screens.ModMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class ScreenManager
{
	public static final ResourceLocation WIDGETS_PATH = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation DS_WIDGETS_PATH = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/widgets.png");
	
	@SuppressWarnings("deprecation")
	public static void renderDarkBackground(Screen screen)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		minecraft.getTextureManager().bind(Screen.BACKGROUND_LOCATION);
		RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.vertex(0.0D, (double) screen.height, 0.0D).uv(0.0F, (float) screen.height / 32.0F + (float) 0)
				.color(0, 0, 0, 255).endVertex();
		bufferbuilder.vertex((double) screen.width, (double) screen.height, 0.0D)
				.uv((float) screen.width / 32.0F, (float) screen.height / 32.0F + (float) 0).color(0, 0, 0, 255)
				.endVertex();
		bufferbuilder.vertex((double) screen.width, 0.0D, 0.0D).uv((float) screen.width / 32.0F, (float) 0)
				.color(0, 0, 0, 255).endVertex();
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
			if (DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			{
				if (event.getGui() instanceof MainMenuScreen)
				{
					MainMenuScreen screen = (MainMenuScreen)event.getGui();
					event.setGui(new ModMainMenuScreen(screen.fading));
				}
			}
			else
			{
				if (event.getGui() instanceof ModMainMenuScreen)
				{
					MainMenuScreen screen = (MainMenuScreen)event.getGui();
					event.setGui(new MainMenuScreen(screen.fading));
				}
			}
		}
		
		@SubscribeEvent
		public static void onInitScreen(GuiScreenEvent.InitGuiEvent.Post event)
		{
			if (Widget.WIDGETS_LOCATION.getNamespace() == WIDGETS_PATH.getNamespace()
					&& DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			{
				Widget.WIDGETS_LOCATION = DS_WIDGETS_PATH;
			}
			else if (Widget.WIDGETS_LOCATION.getNamespace() == DS_WIDGETS_PATH.getNamespace()
					&& !DarkSouls.CLIENT_INGAME_CONFIG.darkSoulsUI.getValue())
			{
				Widget.WIDGETS_LOCATION = WIDGETS_PATH;
			}
		}
		
		@SuppressWarnings("deprecation")
		@SubscribeEvent
		public static void onDirtBackgroundDrawn(BackgroundDrawnEvent event)
		{
			if (minecraft.level != null) return;
			
			int height = event.getGui().height;
			int width = event.getGui().width;
			Tessellator tessellator = Tessellator.getInstance();
		    BufferBuilder bufferbuilder = tessellator.getBuilder();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		    bufferbuilder.vertex(0.0D, (double)height, 0.0D).uv(0.0F, (float)height / 32.0F + 0).color(0, 0, 0, 255).endVertex();
		    bufferbuilder.vertex((double)width, (double)height, 0.0D).uv((float)width / 32.0F, (float)height / 32.0F + 0).color(0, 0, 0, 255).endVertex();
		    bufferbuilder.vertex((double)width, 0.0D, 0.0D).uv((float)width / 32.0F, 0).color(0, 0, 0, 255).endVertex();
		    bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, 0).color(0, 0, 0, 255).endVertex();
		    tessellator.end();
		}
	}
}
