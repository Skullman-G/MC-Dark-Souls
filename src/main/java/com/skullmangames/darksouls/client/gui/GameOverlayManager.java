package com.skullmangames.darksouls.client.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.AbstractClientPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.timer.Timer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

public class GameOverlayManager
{
	private static final Minecraft minecraft = Minecraft.getInstance();
	
	private static final ResourceLocation LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png");
	private static final ResourceLocation BOSS_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
	private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/widgets.png");
	
	private static final Timer damageCooldown = new Timer();
	private static final Timer damageTimer = new Timer();
	private static final Timer healTimer = new Timer();
	private static int lastHealth;
	private static int saveLastHealth;
	public static boolean isHealing = false;
	
	private static float lastFP;
	private static float saveLastFP;
	private static final Timer fpDrainCooldown = new Timer();
	private static final Timer fpDrainTimer = new Timer();
	private static final Timer fpRiseTimer = new Timer();
	
	private static final Timer staminaTimer = new Timer();
	private static final Timer staminaDrainTimer = new Timer();
	private static final Timer stamiaDrainCooldownTimer = new Timer();
	private static float lastStamina;
	private static float saveLastStamina;
	private static float saveLastStamina2;
	
	public static int lastSouls;
	private static int soulIncr;
	public static int lerpSouls;
	private static final Timer soulGetTimer = new Timer();
	public static boolean canAnimateSouls = false;
	
	private static final Map<UUID, BossHealthInfo> bossHealthInfoMap = new HashMap<>();
	
	public static void registerOverlayElements()
	{
		OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.BOSS_HEALTH_ELEMENT, false);
		
		OverlayRegistry.registerOverlayTop("Mod Boss Health Bar", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
			if (!minecraft.options.hideGui)
	        {
	            gui.setupOverlayRenderState(true, false);
	            gui.setBlitOffset(-90);
	            
	            renderBossHealthBars(gui, poseStack);
	        }
		});
		
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "Mod Player Health", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderHealth(gui, screenWidth, screenHeight, poseStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "Player Stamina", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderStamina(gui, screenWidth, screenHeight, poseStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayTop("Player Humanity", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderHumanity(gui, screenWidth, screenHeight, poseStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayTop("Player Souls", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderSouls(screenWidth, screenHeight, poseStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "Player FP", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderFP(gui, screenWidth, screenHeight, poseStack);
	        }
	    });
		
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, "Player Attunements", (gui, poseStack, partialTicks, screenWidth, screenHeight) ->
		{
	        if (!minecraft.options.hideGui)
	        {
	            gui.setupOverlayRenderState(true, false);
	            renderAttunements(gui, screenWidth, screenHeight, poseStack, partialTicks);
	        }
	    });
	}
	
	private static void renderAttunements(ForgeIngameGui gui, int width, int height, PoseStack poseStack, float partialTicks)
	{
		LocalPlayerCap playerCap = getCameraPlayerCap();
		if (playerCap == null) return;
		LocalPlayer player = playerCap.getOriginalEntity();

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
		int middle = height / 2;
		int j = gui.getBlitOffset();
		int k = 182;
		gui.setBlitOffset(-90);
		gui.blit(poseStack, 0, middle - k / 2, 234, 74, 22, k);
		gui.blit(poseStack, 0, middle - k / 2 + playerCap.getAttunements().selected * 20, 0, 22, 24, 24);

		gui.setBlitOffset(j);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		int i1 = 1;

		for (int j1 = 0; j1 < 9; ++j1)
		{
			renderSlot(3, middle - k / 2 + j1 * 20 + 3, partialTicks, player, playerCap.getAttunements().getItem(j1), i1++);
		}

		RenderSystem.disableBlend();
	}
	
	private static void renderSlot(int x, int y, float partialTicks, Player player, ItemStack itemStack, int p_168683_)
	{
		if (!itemStack.isEmpty())
		{
			PoseStack posestack = RenderSystem.getModelViewStack();
			float f = (float) itemStack.getPopTime() - partialTicks;
			if (f > 0.0F)
			{
				float f1 = 1.0F + f / 5.0F;
				posestack.pushPose();
				posestack.translate((double) (x + 8), (double) (y + 12), 0.0D);
				posestack.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
				posestack.translate((double) (-(x + 8)), (double) (-(y + 12)), 0.0D);
				RenderSystem.applyModelViewMatrix();
			}

			ItemRenderer itemRenderer = minecraft.getItemRenderer();
			itemRenderer.renderAndDecorateItem(player, itemStack, x, y, p_168683_);
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			if (f > 0.0F)
			{
				posestack.popPose();
				RenderSystem.applyModelViewMatrix();
			}

			itemRenderer.renderGuiItemDecorations(minecraft.font, itemStack, x, y);
		}
	}
	
	private static void renderFP(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
	{
		AbstractClientPlayerCap<?> player = getCameraPlayerCap();
		if (player == null) return;
		
		RenderSystem.enableBlend();
		int y = height - 49;
		int x = width / 2 + 7;
		gui.right_height += 10;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(poseStack, x, y, 0, 0, 88, 7);
		float fpPercentage = player.getFP() / player.getMaxFP();
		
		// Drain Animation
		if (lastFP > fpPercentage)
		{
			if (!fpDrainCooldown.isTicking())
			{
				saveLastFP = lastFP;
			}

			fpDrainCooldown.start(50);
		}

		float visibleFP = saveLastFP - (fpDrainTimer.getPastTime() * 0.01F);

		if (fpDrainCooldown.isTicking())
		{
			fpRiseTimer.stop();
			boolean flag = false;
			if (visibleFP <= fpPercentage)
			{
				visibleFP = saveLastFP;
				flag = true;
			}
			minecraft.gui.blit(poseStack, x, y, 0, 14, (int)(visibleFP * 88), 7); // Yellow
			fpDrainCooldown.drain(1);
			if (!fpDrainCooldown.isTicking() && (!fpDrainTimer.isTicking() || flag))
				fpDrainTimer.start((int)(visibleFP * 200));
		} else if (fpDrainTimer.isTicking())
		{
			fpRiseTimer.stop();
			minecraft.gui.blit(poseStack, x, y, 0, 14, (int)(visibleFP * 88), 7); // Yellow
			fpDrainTimer.drain(1);
		}

		// Rise Animation
		if ((lastHealth < fpPercentage && isHealing) || fpRiseTimer.isTicking())
		{
			fpDrainTimer.stop();
			if (!fpRiseTimer.isTicking())
			{
				saveLastFP = lastFP;
				fpRiseTimer.start((int)((fpPercentage - saveLastFP) * 100));
			}
			float healcentage = saveLastFP + fpRiseTimer.getPastTime();
			minecraft.gui.blit(poseStack, x, y, 0, 28, (int)(healcentage * 88), 7); // Blue
			fpRiseTimer.drain(1);
		}

		// Default
		else
		{
			minecraft.gui.blit(poseStack, x, y, 0, 28, (int)(fpPercentage * 88), 7); // Blue
		}

		lastFP = fpPercentage;
		RenderSystem.disableBlend();
	}
	
	private static void renderBossHealthBars(ForgeIngameGui gui, PoseStack poseStack)
	{
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		RenderSystem.defaultBlendFunc();
		minecraft.getProfiler().push("bossHealth");

		Map<UUID, LerpingBossEvent> events = gui.getBossOverlay().events;

		if (!events.isEmpty())
		{
			int i = minecraft.getWindow().getGuiScaledWidth();
			int j = 12;
			
			for (UUID uuid : bossHealthInfoMap.keySet())
			{
				if (!events.containsKey(uuid)) bossHealthInfoMap.remove(uuid);
			}
			for (UUID uuid : events.keySet())
			{
				if (!bossHealthInfoMap.containsKey(uuid)) bossHealthInfoMap.put(uuid, new BossHealthInfo());
			}

			for (LerpingBossEvent lerpingbossevent : events.values())
			{
				int k = i / 2 - 91;
				net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event = net.minecraftforge.client.ForgeHooksClient
						.renderBossEventPre(poseStack, minecraft.getWindow(), lerpingbossevent, k, j,
								10 + minecraft.font.lineHeight);
				if (!event.isCanceled())
				{
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.setShaderTexture(0, BOSS_BARS_LOCATION);
					drawBossBar(gui.getBossOverlay(), poseStack, k, j, lerpingbossevent);
					Component component = lerpingbossevent.getName();
					int l = minecraft.font.width(component);
					int i1 = i / 2 - l / 2;
					int j1 = j - 9;
					minecraft.font.drawShadow(poseStack, component, (float) i1, (float) j1, 16777215);
				}
				j += event.getIncrement();
				net.minecraftforge.client.ForgeHooksClient.renderBossEventPost(poseStack, minecraft.getWindow());
				if (j >= minecraft.getWindow().getGuiScaledHeight() / 3)
				{
					break;
				}
			}

		}

		minecraft.getProfiler().pop();
	}
	
	private static void drawBossBar(BossHealthOverlay gui, PoseStack poseStack, int x, int y, LerpingBossEvent bossEvent)
	{
		gui.blit(poseStack, x, y, 0, bossEvent.getColor().ordinal() * 5 * 2, 182, 5); // Background
		if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS)
		{
			gui.blit(poseStack, x, y, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2, 182, 5); // Background Overlay
		}

		int progress = (int) (bossEvent.getProgress() * 183.0F);
		BossHealthInfo info = bossHealthInfoMap.get(bossEvent.getId());
		
		// Damage Animation
		if (info.lastHealth > progress)
		{
			if (!info.damageCooldown.isTicking())
			{
				info.saveLastHealth = info.lastHealth;
			}

			info.damageCooldown.start(30);
		}

		int damagedHealth = info.saveLastHealth - info.damageTimer.getPastTime();

		if (info.damageCooldown.isTicking())
		{
			boolean flag = false;
			if (damagedHealth <= progress)
			{
				damagedHealth = info.saveLastHealth;
				flag = true;
			}
			gui.blit(poseStack, x, y, 0, 45, damagedHealth, 5); // Yellow
			info.damageCooldown.drain(1);
			if (!info.damageCooldown.isTicking() && (!info.damageTimer.isTicking() || flag)) info.damageTimer.start(damagedHealth * 2);
		}
		else if (info.damageTimer.isTicking())
		{
			gui.blit(poseStack, x, y, 0, 45, damagedHealth, 5); // Yellow
			info.damageTimer.drain(1);
		}
		
		info.lastHealth = progress;
		
		if (progress > 0)
		{
			gui.blit(poseStack, x, y, 0, bossEvent.getColor().ordinal() * 5 * 2 + 5, progress, 5); // Foreground
			if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS)
			{
				gui.blit(poseStack, x, y, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + 5, progress, 5); // Foreground Overlay
			}
		}
	}
	
	private static class BossHealthInfo
	{
		private final Timer damageCooldown = new Timer();
		private final Timer damageTimer = new Timer();
		private int lastHealth;
		private int saveLastHealth;
	}
	
	private static void renderHealth(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
	{
		RenderSystem.enableBlend();
		int x = width / 2 - 96;
		int y = height - 39;
		gui.left_height += 10;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(poseStack, x, y, 0, 0, 88, 7); // Black
		int healthpercentage = (int)(getCameraPlayer().getHealth() / getCameraPlayer().getMaxHealth() * 88);
		
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
			minecraft.gui.blit(poseStack, x, y, 0, 14, damagedHealth, 7); // Yellow
			damageCooldown.drain(1);
			if (!damageCooldown.isTicking() && (!damageTimer.isTicking() || flag)) damageTimer.start(damagedHealth * 2);
		}
		else if (damageTimer.isTicking())
		{
			healTimer.stop();
			minecraft.gui.blit(poseStack, x, y, 0, 14, damagedHealth, 7); // Yellow
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
			minecraft.gui.blit(poseStack, x, y, 0, 7, healcentage, 7); // Red
			healTimer.drain(1);
		}
		
		// Default
		else
		{
			minecraft.gui.blit(poseStack, x, y, 0, 7, healthpercentage, 7); // Red
		}
		
		lastHealth = healthpercentage;
		RenderSystem.disableBlend();
	}
	
	private static void renderStamina(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
	{
		AbstractClientPlayerCap<?> player = getCameraPlayerCap();
		if (player == null) return;
		
		RenderSystem.enableBlend();
		int y = height - 39;
		int x = width / 2 + 7;
		gui.right_height += 10;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(poseStack, x, y, 0, 0, 88, 7);
		float staminaPercentage = player.getStamina() / player.getMaxStamina();
		
		// Yellow Bar
		if (lastStamina > staminaPercentage && !getCameraPlayer().isSprinting())
		{
			if (!stamiaDrainCooldownTimer.isTicking() && !staminaDrainTimer.isTicking())
			{
				saveLastStamina = lastStamina;
			}
			
			stamiaDrainCooldownTimer.start(10);
		}
		
		float drainedStamina = saveLastStamina - (staminaDrainTimer.getPastTime() * 0.01F);
		
		if (drainedStamina <= staminaPercentage)
		{
			staminaDrainTimer.stop();
			saveLastStamina = 0;
			drainedStamina = 0;
		}
		
		if (stamiaDrainCooldownTimer.isTicking())
		{
			stamiaDrainCooldownTimer.drain(1);
			if (!stamiaDrainCooldownTimer.isTicking() && !staminaDrainTimer.isTicking()) staminaDrainTimer.start((int)(drainedStamina * 200));
		}
		else if (staminaDrainTimer.isTicking()) staminaDrainTimer.drain(1);
		
		minecraft.gui.blit(poseStack, x, y, 0, 14, (int)(drainedStamina * 88), 7); // Yellow
		
		
		// Green Bar
		if (lastStamina != staminaPercentage || staminaTimer.isTicking())
		{
			if (!staminaTimer.isTicking())
			{
				saveLastStamina2 = lastStamina;
				staminaTimer.start((int)((staminaPercentage - saveLastStamina2) * 100));
			}
			float percentage = saveLastStamina2 + (staminaTimer.getPastTime() * 0.01F);
			minecraft.gui.blit(poseStack, x, y, 0, 21, (int)(percentage * 88), 7);
			staminaTimer.drain(1);
		}
		else
		{
			minecraft.gui.blit(poseStack, x, y, 0, 21, (int)(staminaPercentage * 88), 7);
		}
		
		lastStamina = staminaPercentage;
		RenderSystem.disableBlend();
	}
	
	private static void renderHumanity(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
	{
		LocalPlayerCap playerdata = ClientManager.INSTANCE.getPlayerCap();
		int x = width / 2;
		int y = height - 45;
		int color = playerdata.isHuman() ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
		
		ForgeIngameGui.drawCenteredString(poseStack, minecraft.font, String.valueOf(playerdata.getHumanity()), x, y, color);
	}

	private static void renderSouls(int width, int height, PoseStack poseStack)
	{
		RenderSystem.enableBlend();
		int x = width - 76;
		int y = height - 21;
		
		RenderSystem.setShaderTexture(0, LOCATION);
		minecraft.gui.blit(poseStack, x, y, 0, 46, 65, 16);
		
		x = width - (76 / 2);
		y = height - 15;
		
		PoseStack ps = new PoseStack();
		float scale = 0.8F;
		ps.scale(scale, scale, scale);
		
		int currentSouls = ClientManager.INSTANCE.getPlayerCap().getSouls();
		
		if (canAnimateSouls)
		{
			int displaySouls = MathUtils.lerp(5, lerpSouls, currentSouls);
			if (!soulGetTimer.isTicking() && currentSouls != lastSouls)
			{
				soulIncr = currentSouls - lastSouls;
				soulGetTimer.start(200);
			}
			if (soulGetTimer.isTicking())
			{
				int alpha = 255;
				if (soulGetTimer.getLeftTime() <= 10)
				{
					alpha = (int)((soulGetTimer.getLeftTime() / 10.0F) * 255);
				}
				
				ForgeIngameGui.drawCenteredString(ps, minecraft.font, soulIncr > 0 ? "+" + String.valueOf(soulIncr) : String.valueOf(soulIncr),
						Math.round(x / scale), Math.round((y - 12) / scale), new Color(255, 255, 255, alpha).getRGB());
			}
			ForgeIngameGui.drawCenteredString(ps, minecraft.font, String.valueOf(displaySouls), Math.round(x / scale), Math.round(y / scale), Color.WHITE.getRGB());
			
			if (!minecraft.isPaused())
			{
				soulGetTimer.drain(1);
				lastSouls = currentSouls;
				lerpSouls = displaySouls;
			}
		}
		else
		{
			ForgeIngameGui.drawCenteredString(ps, minecraft.font, String.valueOf(currentSouls), Math.round(x / scale), Math.round(y / scale), Color.WHITE.getRGB());
		}
		
		RenderSystem.disableBlend();
	}
	
	private static LocalPlayer getCameraPlayer()
	{
	    return minecraft.player;
	}
	
	private static LocalPlayerCap getCameraPlayerCap()
	{
		LocalPlayer player = getCameraPlayer();
		if (player == null) return null;
		EntityCapability<?> entityCap = player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (!(entityCap instanceof LocalPlayerCap)) return null;
		return (LocalPlayerCap)entityCap;
	}
}
