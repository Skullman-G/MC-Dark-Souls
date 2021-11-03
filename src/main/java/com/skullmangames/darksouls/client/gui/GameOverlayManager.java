package com.skullmangames.darksouls.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.entity.RemoteClientPlayerData;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.Timer;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class GameOverlayManager
{
	private static Minecraft minecraft = Minecraft.getInstance();
	
	private static final Timer damageCooldown = new Timer();
	private static final Timer damageTimer = new Timer();
	private static final Timer healTimer = new Timer();
	private static int lastHealth;
	private static int saveLastHealth;
	public static boolean isHealing = false;
	
	private static final Timer staminaTimer = new Timer();
	private static final Timer stamiaDrainTimer = new Timer();
	private static final Timer stamiaDrainCooldownTimer = new Timer();
	private static int lastStamina;
	private static int saveLastStamina;
	private static int saveLastStamina2;
	
	public static void render(ElementType type, MainWindow window, MatrixStack matrixstack)
	{
		if (minecraft.player.isCreative() || minecraft.player.isSpectator()) return;
		if (type != ElementType.ALL) return;
		
		renderHumanity(window, matrixstack);
		renderHealth(window, matrixstack);
		renderSouls(window, matrixstack);
		renderStamina(window, matrixstack);
	}
	
	private static void renderHumanity(MainWindow window, MatrixStack matrixstack)
	{
		int x = window.getGuiScaledWidth() / 2;
		int y = window.getGuiScaledHeight() - 45;
		int color = MobNBTManager.isHuman(minecraft.player) ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
		
		ForgeIngameGui.drawCenteredString(matrixstack, minecraft.font, MobNBTManager.getStringHumanity(minecraft.player), x, y, color);
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
			minecraft.gui.blit(matrixstack, x, y, 0, 18, damagedHealth, 9);
			damageCooldown.drain(1.0F);
			if (!damageCooldown.isTicking() && (!damageTimer.isTicking() || flag)) damageTimer.start(damagedHealth);
		}
		else if (damageTimer.isTicking())
		{
			healTimer.stop();
			minecraft.gui.blit(matrixstack, x, y, 0, 18, damagedHealth, 9);
			damageTimer.drain(0.5F);
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
			minecraft.gui.blit(matrixstack, x, y, 0, 9, healcentage, 9);
			healTimer.drain(1.0F);
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
		
		minecraft.gui.blit(matrixstack, x, y, 0, 44, 65, 16);
		
		x = window.getGuiScaledWidth() - (76 / 2);
		y = window.getGuiScaledHeight() - 15;
		int color = Color.WHITE.getRGB();
		
		MatrixStack ms = new MatrixStack();
		float scale = 0.8F;
		ms.scale(scale, scale, scale);
		ForgeIngameGui.drawCenteredString(ms, minecraft.font, MobNBTManager.getStringSouls(getCameraPlayer()), Math.round(x / scale), Math.round(y / scale), color);
	}
	
	private static void renderStamina(MainWindow window, MatrixStack matrixstack)
	{
		RemoteClientPlayerData<?> player = getCameraPlayerData();
		if (player == null) return;
		
		RenderSystem.enableBlend();
		int y = window.getGuiScaledHeight() - 39;
		int x = window.getGuiScaledWidth() / 2 + 3;
		
		minecraft.getTextureManager().bind(new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png"));
		minecraft.gui.blit(matrixstack, x, y, 0, 0, 90, 9);
		double staminaPercentageDouble = (double)player.getStamina() / (double)player.getMaxStamina() * 90.0D;
		int staminaPercentage = (int)staminaPercentageDouble;
		
		// Yellow Bar
		if (lastStamina > staminaPercentage && !getCameraPlayer().isSprinting())
		{
			if (!stamiaDrainCooldownTimer.isTicking() && !stamiaDrainTimer.isTicking())
			{
				saveLastStamina = lastStamina;
			}
			
			stamiaDrainCooldownTimer.start(10);
		}
		
		int drainedStamina = saveLastStamina - stamiaDrainTimer.getPastTime();
		
		if (stamiaDrainCooldownTimer.isTicking())
		{
			boolean flag = false;
			if (drainedStamina <= staminaPercentage)
			{
				drainedStamina = saveLastStamina;
				flag = true;
			}
			minecraft.gui.blit(matrixstack, x, y, 0, 18, drainedStamina, 9);
			stamiaDrainCooldownTimer.drain(1.0F);
			if (!stamiaDrainCooldownTimer.isTicking() && (!stamiaDrainTimer.isTicking() || flag)) stamiaDrainTimer.start(drainedStamina);
		}
		else if (stamiaDrainTimer.isTicking())
		{
			minecraft.gui.blit(matrixstack, x, y, 0, 18, drainedStamina, 9);
			stamiaDrainTimer.drain(0.5F);
		}
		
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
			staminaTimer.drain(1.0F);
		}
		else
		{
			minecraft.gui.blit(matrixstack, x, y, 0, 35, staminaPercentage, 9);
		}
		
		lastStamina = staminaPercentage;
	}
	
	private static AbstractClientPlayerEntity getCameraPlayer()
	{
	    return !(Minecraft.getInstance().getCameraEntity() instanceof AbstractClientPlayerEntity) ? null : (AbstractClientPlayerEntity)Minecraft.getInstance().getCameraEntity();
	}
	
	private static RemoteClientPlayerData<?> getCameraPlayerData()
	{
		AbstractClientPlayerEntity player = getCameraPlayer();
		if (player == null) return null;
		EntityData<?> entitydata = player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (!(entitydata instanceof RemoteClientPlayerData<?>)) return null;
		return (RemoteClientPlayerData<?>)entitydata;
	}
}
