package com.skullmangames.darksouls.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.EntityData;
import com.skullmangames.darksouls.common.capability.entity.RemoteClientPlayerData;
import com.skullmangames.darksouls.common.entity.nbt.MobNBTManager;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.Timer;

import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
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
	
	public static void renderAdditional(MainWindow window, MatrixStack matrixstack)
	{
		if (minecraft.player.isCreative() || minecraft.player.isSpectator()) return;
		
		renderHumanity(window, matrixstack);
		renderSouls(window, matrixstack);
	}
	
	private static boolean canRenderCrosshairForSpectator(RayTraceResult raytraceresult)
	{
	      if (raytraceresult == null) return false;
	      else if (raytraceresult.getType() == RayTraceResult.Type.ENTITY)
	      {
	         return ((EntityRayTraceResult)raytraceresult).getEntity() instanceof INamedContainerProvider;
	      }
	      else if (raytraceresult.getType() == RayTraceResult.Type.BLOCK)
	      {
	         BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getBlockPos();
	         World world = minecraft.level;
	         return world.getBlockState(blockpos).getMenuProvider(world, blockpos) != null;
	      }
	      else return false;
	   }
	
	public static void renderCrosshair(MainWindow window, MatrixStack matrixstack)
	{
	   GameSettings gamesettings = minecraft.options;
	   int screenWidth = window.getGuiScaledWidth();
	   int screenHeight = window.getGuiScaledHeight();
	   
	   if (gamesettings.getCameraType().isFirstPerson())
	   {
	      if (minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || canRenderCrosshairForSpectator(minecraft.hitResult))
	      {
	         if (gamesettings.renderDebug && !gamesettings.hideGui && !minecraft.player.isReducedDebugInfo() && !gamesettings.reducedDebugInfo)
	         {
	            RenderSystem.pushMatrix();
	            RenderSystem.translatef((float)(screenWidth / 2), (float)(screenHeight / 2), (float)minecraft.gui.getBlitOffset());
	            ActiveRenderInfo activerenderinfo = minecraft.gameRenderer.getMainCamera();
	            RenderSystem.rotatef(activerenderinfo.getXRot(), -1.0F, 0.0F, 0.0F);
	            RenderSystem.rotatef(activerenderinfo.getYRot(), 0.0F, 1.0F, 0.0F);
	            RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
	            RenderSystem.renderCrosshair(10);
	            RenderSystem.popMatrix();
	         }
	         else
	         {
	            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
	            		GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            minecraft.gui.blit(matrixstack, (screenWidth - 15) / 2, (screenHeight - 15) / 2, 0, 0, 15, 15);
	            if (minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR)
	            {
	               float f = minecraft.player.getAttackStrengthScale(0.0F);
	               boolean flag = false;
	               if (minecraft.crosshairPickEntity != null && minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F)
	               {
	                  flag = minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
	                  flag = flag & minecraft.crosshairPickEntity.isAlive();
	               }

	               int j = screenHeight / 2 - 7 + 16;
	               int k = screenWidth / 2 - 8;
	               if (flag)
	               {
	                  minecraft.gui.blit(matrixstack, k, j, 68, 94, 16, 16);
	               }
	            }
	         }

	      }
	   }
	}
	
	public static void renderHumanity(MainWindow window, MatrixStack matrixstack)
	{
		int x = window.getGuiScaledWidth() / 2;
		int y = window.getGuiScaledHeight() - 45;
		int color = MobNBTManager.isHuman(minecraft.player) ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
		
		ForgeIngameGui.drawCenteredString(matrixstack, minecraft.font, MobNBTManager.getStringHumanity(minecraft.player), x, y, color);
	}
	
	public static void renderHealth(MainWindow window, MatrixStack matrixstack)
	{
		RenderSystem.enableBlend();
		int x = window.getGuiScaledWidth() / 2 - 91;
		int y = window.getGuiScaledHeight() - 39;
		ForgeIngameGui.left_height += 10;
		
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
	
	public static void renderSouls(MainWindow window, MatrixStack matrixstack)
	{
		int x = window.getGuiScaledWidth() - 76;
		int y = window.getGuiScaledHeight() - 21;
		
		minecraft.getTextureManager().bind(new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png"));
		minecraft.gui.blit(matrixstack, x, y, 0, 44, 65, 16);
		
		x = window.getGuiScaledWidth() - (76 / 2);
		y = window.getGuiScaledHeight() - 15;
		int color = Color.WHITE.getRGB();
		
		MatrixStack ms = new MatrixStack();
		float scale = 0.8F;
		ms.scale(scale, scale, scale);
		ForgeIngameGui.drawCenteredString(ms, minecraft.font, MobNBTManager.getStringSouls(getCameraPlayer()), Math.round(x / scale), Math.round(y / scale), color);
	}
	
	public static void renderStamina(MainWindow window, MatrixStack matrixstack)
	{
		RemoteClientPlayerData<?> player = getCameraPlayerData();
		if (player == null) return;
		
		RenderSystem.enableBlend();
		int y = window.getGuiScaledHeight() - 39;
		int x = window.getGuiScaledWidth() / 2 + 3;
		ForgeIngameGui.right_height += 10;
		
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
