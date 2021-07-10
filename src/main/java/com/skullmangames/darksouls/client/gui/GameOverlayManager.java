package com.skullmangames.darksouls.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entities.ModEntityDataManager;
import com.skullmangames.darksouls.core.util.Timer;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class GameOverlayManager
{
	private static Minecraft minecraft = Minecraft.getInstance();
	private static Timer damageTimer = new Timer(0);
	private static Timer healTimer = new Timer(0);
	private static int lastHealth;
	private static int saveLastHealth;
	public static boolean isHealing = false;
	
	public static void render(ElementType type, MainWindow window, MatrixStack matrixstack)
	{
		if (!minecraft.player.isCreative() && !minecraft.player.isSpectator())
		{
			if (type == ElementType.ALL)
			{
				renderHumanity(window, matrixstack);
				renderHealth(window, matrixstack);
				renderSouls(window, matrixstack);
				//renderStamina(window, matrixstack);
			}
		}
	}
	
	private static void renderHumanity(MainWindow window, MatrixStack matrixstack)
	{
		int x = window.getGuiScaledWidth() / 2;
		int y = window.getGuiScaledHeight() - 45;
		int color = ModEntityDataManager.isHuman(minecraft.player) ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
		
		ForgeIngameGui.drawCenteredString(matrixstack, minecraft.font, ModEntityDataManager.getStringHumanity(minecraft.player), x, y, color);
	}
	
	private static void renderHealth(MainWindow window, MatrixStack matrixstack)
	{
		RenderSystem.enableBlend();
		int x = window.getGuiScaledWidth() / 2 - 91;
		int y = window.getGuiScaledHeight() - 39;
		
		minecraft.getTextureManager().bind(new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png"));
		minecraft.gui.blit(matrixstack, x, y, 0, 0, 90, 9);
		int healthpercentage = (int)(getCameraPlayer().getHealth() / getCameraPlayer().getMaxHealth() * 90);
		
		// Damage Animation
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
		
		// Heal Animation
		if ((lastHealth < healthpercentage && isHealing) || healTimer.isTicking())
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
		
		// Default
		else
		{
			minecraft.gui.blit(matrixstack, x, y, 0, 9, healthpercentage, 9);
		}
		
		lastHealth = healthpercentage;
	}
	
	private static void renderSouls(MainWindow window, MatrixStack matrixstack)
	{
		int x = window.getGuiScaledWidth() - 76;
		int y = window.getGuiScaledHeight() - 21;
		
		minecraft.gui.blit(matrixstack, x, y, 0, 44, 71, 17);
		
		x = window.getGuiScaledWidth() - (76 / 2);
		y = window.getGuiScaledHeight() - 17;
		int color = Color.WHITE.getRGB();
		
		ForgeIngameGui.drawCenteredString(matrixstack, minecraft.font, ModEntityDataManager.getStringSouls(getCameraPlayer()), x, y, color);
	}
	
	/*private static void renderStamina(MainWindow window, MatrixStack matrixstack)
	{
		RenderSystem.enableBlend();
		int y = window.getGuiScaledHeight() - 39;
		int x = window.getGuiScaledWidth() / 2 + 3;
		
		minecraft.getTextureManager().bind(new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png"));
		minecraft.gui.blit(matrixstack, x, y, 0, 0, 90, 9);
		double endurancepercentagedouble = (double)StaminaDataManager.getStamina(getCameraPlayer()) / (double)StaminaDataManager.getMaxStamina(getCameraPlayer()) * 90.0D;
		int endurancepercentage = (int)endurancepercentagedouble;
		
		// Loose a lot Animation
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
		
		minecraft.gui.blit(matrixstack, x, y, 0, 35, endurancepercentage, 9);
	}*/
	
	private static PlayerEntity getCameraPlayer()
	{
	    return !(Minecraft.getInstance().getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)Minecraft.getInstance().getCameraEntity();
	}
}
