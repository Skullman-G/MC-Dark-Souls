package com.skullmangames.darksouls.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.ClientPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.RemoteClientPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.timer.Timer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

public class GameOverlayManager
{
	private static final Minecraft minecraft = Minecraft.getInstance();
	
	private static final ResourceLocation LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png");
	
	private static final Timer damageCooldown = new Timer();
	private static final Timer damageTimer = new Timer();
	private static final Timer healTimer = new Timer();
	private static int lastHealth;
	private static int saveLastHealth;
	public static boolean isHealing = false;
	
	private static final Timer staminaTimer = new Timer();
	private static final Timer staminaDrainTimer = new Timer();
	private static final Timer stamiaDrainCooldownTimer = new Timer();
	private static int lastStamina;
	private static int saveLastStamina;
	private static int saveLastStamina2;
	
	public static void registerOverlayElements()
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT, false);
		
		OverlayRegistry.registerOverlayTop("Mod Player Health", (gui, pStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderHealth(gui, screenWidth, screenHeight, pStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayTop("Player Stamina", (gui, pStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderStamina(gui, screenWidth, screenHeight, pStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayTop("Player Humanity", (gui, pStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderHumanity(gui, screenWidth, screenHeight, pStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayTop("Player Souls", (gui, pStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderSouls(gui, screenWidth, screenHeight, pStack);
	        }
	    });
	}
	
	private static void renderHealth(ForgeIngameGui gui, int width, int height, PoseStack posestack)
	{
		RenderSystem.enableBlend();
		int x = width / 2 - 91;
		int y = height - 39;
		gui.left_height += 10;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(posestack, x, y, 0, 0, 90, 9);
		int healthpercentage = (int)(getCameraPlayer().getHealth() / getCameraPlayer().getMaxHealth() * 90);
		
		// Damage Animation
		if (lastHealth > healthpercentage)
		{
			if (!damageCooldown.isTicking())
			{
				saveLastHealth = lastHealth;
			}
			
			damageCooldown.start(50);
		}
		
		int damagedHealth = saveLastHealth - damageTimer.getPastTime();
		
		if (damageCooldown.isTicking())
		{
			healTimer.stop();
			boolean flag = false;
			if (damagedHealth <= healthpercentage)
			{
				damagedHealth = saveLastHealth;
				flag = true;
			}
			minecraft.gui.blit(posestack, x, y, 0, 18, damagedHealth, 9);
			damageCooldown.drain(1);
			if (!damageCooldown.isTicking() && (!damageTimer.isTicking() || flag)) damageTimer.start(damagedHealth * 2);
		}
		else if (damageTimer.isTicking())
		{
			healTimer.stop();
			minecraft.gui.blit(posestack, x, y, 0, 18, damagedHealth, 9);
			damageTimer.drain(1);
		}
		
		// Heal Animation
		if ((lastHealth < healthpercentage && isHealing) || healTimer.isTicking())
		{
			damageTimer.stop();
			if (!healTimer.isTicking())
			{
				saveLastHealth = lastHealth;
				healTimer.start(healthpercentage - saveLastHealth);
			}
			int healcentage = saveLastHealth + healTimer.getPastTime();
			minecraft.gui.blit(posestack, x, y, 0, 9, healcentage, 9);
			healTimer.drain(1);
		}
		
		// Default
		else
		{
			minecraft.gui.blit(posestack, x, y, 0, 9, healthpercentage, 9);
		}
		
		lastHealth = healthpercentage;
		RenderSystem.disableBlend();
	}
	
	private static void renderStamina(ForgeIngameGui gui, int width, int height, PoseStack matrixstack)
	{
		RemoteClientPlayerCap<?> player = getCameraPlayerData();
		if (player == null) return;
		
		RenderSystem.enableBlend();
		int y = height - 39;
		int x = width / 2 + 3;
		gui.right_height += 10;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(matrixstack, x, y, 0, 0, 90, 9);
		double staminaPercentageDouble = (double)player.getStamina() / (double)player.getMaxStamina() * 90.0D;
		int staminaPercentage = (int)staminaPercentageDouble;
		
		// Yellow Bar
		if (lastStamina > staminaPercentage && !getCameraPlayer().isSprinting())
		{
			if (!stamiaDrainCooldownTimer.isTicking() && !staminaDrainTimer.isTicking())
			{
				saveLastStamina = lastStamina;
			}
			
			stamiaDrainCooldownTimer.start(10);
		}
		
		int drainedStamina = saveLastStamina - staminaDrainTimer.getPastTime();
		
		if (drainedStamina <= staminaPercentage)
		{
			staminaDrainTimer.stop();
			saveLastStamina = 0;
			drainedStamina = 0;
		}
		
		if (stamiaDrainCooldownTimer.isTicking())
		{
			stamiaDrainCooldownTimer.drain(1);
			if (!stamiaDrainCooldownTimer.isTicking() && !staminaDrainTimer.isTicking()) staminaDrainTimer.start(drainedStamina * 2);
		}
		else if (staminaDrainTimer.isTicking()) staminaDrainTimer.drain(1);
		
		minecraft.gui.blit(matrixstack, x, y, 0, 18, drainedStamina, 9);
		
		
		// Green Bar
		if (lastStamina != staminaPercentage || staminaTimer.isTicking())
		{
			if (!staminaTimer.isTicking())
			{
				saveLastStamina2 = lastStamina;
				staminaTimer.start(staminaPercentage - saveLastStamina2);
			}
			int percentage = saveLastStamina2 + staminaTimer.getPastTime();
			minecraft.gui.blit(matrixstack, x, y, 0, 35, percentage, 9);
			staminaTimer.drain(1);
		}
		else
		{
			minecraft.gui.blit(matrixstack, x, y, 0, 35, staminaPercentage, 9);
		}
		
		lastStamina = staminaPercentage;
		RenderSystem.disableBlend();
	}
	
	private static void renderHumanity(ForgeIngameGui gui, int width, int height, PoseStack matrixstack)
	{
		ClientPlayerCap playerdata = ClientManager.INSTANCE.getPlayerData();
		int x = width / 2;
		int y = height - 45;
		int color = playerdata.isHuman() ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
		
		ForgeIngameGui.drawCenteredString(matrixstack, minecraft.font, String.valueOf(playerdata.getHumanity()), x, y, color);
	}

	private static void renderSouls(ForgeIngameGui gui, int width, int height, PoseStack matrixstack)
	{
		RenderSystem.enableBlend();
		int x = width - 76;
		int y = height - 21;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(matrixstack, x, y, 0, 44, 65, 16);
		
		x = width - (76 / 2);
		y = height - 15;
		int color = Color.WHITE.getRGB();
		
		PoseStack ms = new PoseStack();
		float scale = 0.8F;
		ms.scale(scale, scale, scale);
		ForgeIngameGui.drawCenteredString(ms, minecraft.font, String.valueOf(ClientManager.INSTANCE.getPlayerData().getSouls()), Math.round(x / scale), Math.round(y / scale), color);
		RenderSystem.disableBlend();
	}
	
	private static LocalPlayer getCameraPlayer()
	{
	    return minecraft.player;
	}
	
	private static ClientPlayerCap getCameraPlayerData()
	{
		LocalPlayer player = getCameraPlayer();
		if (player == null) return null;
		EntityCapability<?> entitydata = player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (!(entitydata instanceof ClientPlayerCap)) return null;
		return (ClientPlayerCap)entitydata;
	}
}
