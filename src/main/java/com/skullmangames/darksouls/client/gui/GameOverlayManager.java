package com.skullmangames.darksouls.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entities.DarkSoulsEntityData;
import com.skullmangames.darksouls.core.util.Timer;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class GameOverlayManager
{
	private static Timer damageTimer = new Timer(0);
	private static Timer healTimer = new Timer(0);
	private static int lastHealth;
	private static int saveLastHealth;
	
	public static void render(ElementType type, MainWindow window, MatrixStack matrixstack)
	{
		Minecraft minecraft = Minecraft.getInstance();
		
		if (!minecraft.player.isCreative() && !minecraft.player.isSpectator())
		{
			if (type == ElementType.ALL)
			{
				// Humanity
				DarkSoulsEntityData humanity = DarkSoulsEntityData.get(minecraft.player);
				int x = window.getGuiScaledWidth() / 2;
				int y = window.getGuiScaledHeight() - 45;
				int color = humanity.isHuman() ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
				
				ForgeIngameGui.drawCenteredString(matrixstack, minecraft.font, humanity.getStringHumanity(), x, y, color);
				
				// Health
				RenderSystem.enableBlend();
				x = window.getGuiScaledWidth() / 2 - 91;
				y = window.getGuiScaledHeight() - 39;
				
				minecraft.getTextureManager().bind(new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png"));
				minecraft.gui.blit(matrixstack, x, y, 0, 0, 90, 9);
				int healthpercentage = (int)(getCameraPlayer().getHealth() / getCameraPlayer().getMaxHealth() * 90);
				if (lastHealth > healthpercentage || damageTimer.isTicking())
				{
					healTimer.stop();
					if (!damageTimer.isTicking())
					{
						saveLastHealth = lastHealth;
						damageTimer.setTimer(saveLastHealth);
					}
					int damagepercentage = saveLastHealth - damageTimer.getPastTime();
					minecraft.gui.blit(matrixstack, x, y, 0, 18, damagepercentage, 9);
					damageTimer.drain(1F);
				}
				if (lastHealth < healthpercentage || healTimer.isTicking())
				{
					damageTimer.stop();
					if (!healTimer.isTicking())
					{
						saveLastHealth = lastHealth;
						healTimer.setTimer(healthpercentage - saveLastHealth);
					}
					int healcentage = saveLastHealth + healTimer.getPastTime();
					minecraft.gui.blit(matrixstack, x, y, 0, 9, healcentage, 9);
					healTimer.drain(1F);
				}
				else
				{
					minecraft.gui.blit(matrixstack, x, y, 0, 9, healthpercentage, 9);
				}
				lastHealth = healthpercentage;
			}
		}
	}
	
	private static PlayerEntity getCameraPlayer()
	{
	    return !(Minecraft.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)Minecraft.getInstance().getCameraEntity();
	}
}
