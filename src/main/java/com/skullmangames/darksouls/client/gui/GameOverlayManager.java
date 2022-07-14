package com.skullmangames.darksouls.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.common.capability.entity.EntityCapability;
import com.skullmangames.darksouls.common.capability.entity.AbstractClientPlayerCap;
import com.skullmangames.darksouls.core.init.ModCapabilities;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.timer.Timer;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = DarkSouls.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class GameOverlayManager
{
	private static final Minecraft minecraft = Minecraft.getInstance();
	
	private static final ResourceLocation LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/health_bar.png");
	private static final ResourceLocation BOSS_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
	
	private static final Timer damageCooldown = new Timer();
	private static final Timer damageTimer = new Timer();
	private static final Timer healTimer = new Timer();
	private static int lastHealth;
	private static int saveLastHealth;
	public static boolean isHealing = false;
	
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
	
	@SubscribeEvent
	public static void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre event)
	{
		MainWindow window = event.getWindow();
		MatrixStack matStack = event.getMatrixStack();
		int width = window.getGuiScaledWidth();
		int height = window.getGuiScaledHeight();

		switch (event.getType())
		{
			case HEALTH:
				event.setCanceled(true);
				renderHealth(width, height, matStack);
				break;
	
			case FOOD:
				if (!(minecraft.getCameraEntity() instanceof LivingEntity)) break;
				event.setCanceled(true);
				renderStamina(width, height, matStack);
				break;
				
			case BOSSHEALTH:
				event.setCanceled(true);
				renderBossHealthBars(matStack);
				break;
	
			case ALL:
				renderHumanity(width, height, matStack);
				renderSouls(width, height, matStack);
				break;
	
			default:
				minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
				break;
		}
	}
	
	private static void renderBossHealthBars(MatrixStack poseStack)
	{
		minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
		RenderSystem.defaultBlendFunc();
		minecraft.getProfiler().push("bossHealth");

		Map<UUID, ClientBossInfo> events = minecraft.gui.getBossOverlay().events;

		if (!events.isEmpty())
		{
			int i = minecraft.getWindow().getGuiScaledWidth();
			int j = 12;
			
			List<UUID> unused = new ArrayList<>();
			for (UUID uuid : bossHealthInfoMap.keySet())
			{
				if (!events.containsKey(uuid)) unused.add(uuid);
			}
			for (UUID uuid : unused) bossHealthInfoMap.remove(uuid);
			for (UUID uuid : events.keySet())
			{
				if (!bossHealthInfoMap.containsKey(uuid)) bossHealthInfoMap.put(uuid, new BossHealthInfo());
			}

			for (ClientBossInfo lerpingbossevent : events.values())
			{
				int k = i / 2 - 91;
				net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event = net.minecraftforge.client.ForgeHooksClient
						.bossBarRenderPre(poseStack, minecraft.getWindow(), lerpingbossevent, k, j, 10 + minecraft.font.lineHeight);
				if (!event.isCanceled())
				{
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					minecraft.getTextureManager().bind(BOSS_BARS_LOCATION);
					drawBossBar(minecraft.gui.getBossOverlay(), poseStack, k, j, lerpingbossevent);
					ITextComponent component = lerpingbossevent.getName();
					int l = minecraft.font.width(component);
					int i1 = i / 2 - l / 2;
					int j1 = j - 9;
					minecraft.font.drawShadow(poseStack, component, (float) i1, (float) j1, 16777215);
				}
				j += event.getIncrement();
				net.minecraftforge.client.ForgeHooksClient.bossBarRenderPost(poseStack, minecraft.getWindow());
				if (j >= minecraft.getWindow().getGuiScaledHeight() / 3) break;
			}

		}

		minecraft.getProfiler().pop();
	}
	
	private static void drawBossBar(BossOverlayGui gui, MatrixStack poseStack, int x, int y, ClientBossInfo bossEvent)
	{
		gui.blit(poseStack, x, y, 0, bossEvent.getColor().ordinal() * 5 * 2, 182, 5); // Background
		if (bossEvent.getOverlay() != BossInfo.Overlay.PROGRESS)
		{
			gui.blit(poseStack, x, y, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2, 182, 5); // Background LoadingGui
		}

		int progress = (int) (bossEvent.getPercent() * 183.0F);
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
			if (bossEvent.getOverlay() != BossInfo.Overlay.PROGRESS)
			{
				gui.blit(poseStack, x, y, 0, 80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + 5, progress, 5); // Foreground LoadingGui
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
	
	private static void renderHealth(int width, int height, MatrixStack poseStack)
	{
		RenderSystem.enableBlend();
		int x = width / 2 - 96;
		int y = height - 39;
		ForgeIngameGui.left_height += 10;
		
		minecraft.getTextureManager().bind(LOCATION);
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
	
	private static void renderStamina(int width, int height, MatrixStack poseStack)
	{
		AbstractClientPlayerCap<?> player = getCameraPlayerData();
		if (player == null) return;
		
		RenderSystem.enableBlend();
		int y = height - 39;
		int x = width / 2 + 7;
		ForgeIngameGui.right_height += 10;
		
		minecraft.getTextureManager().bind(LOCATION);
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
	
	private static void renderHumanity(int width, int height, MatrixStack poseStack)
	{
		LocalPlayerCap playerdata = ClientManager.INSTANCE.getPlayerCap();
		int x = width / 2;
		int y = height - 45;
		int color = playerdata.isHuman() ? Color.WHITE.getRGB() : Color.LIGHT_GRAY.getRGB();
		
		ForgeIngameGui.drawCenteredString(poseStack, minecraft.font, String.valueOf(playerdata.getHumanity()), x, y, color);
	}

	private static void renderSouls(int width, int height, MatrixStack poseStack)
	{
		RenderSystem.enableBlend();
		int x = width - 76;
		int y = height - 21;
		
		minecraft.getTextureManager().bind(LOCATION);
		minecraft.gui.blit(poseStack, x, y, 0, 46, 65, 16);
		
		x = width - (76 / 2);
		y = height - 15;
		
		MatrixStack ps = new MatrixStack();
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
	
	private static ClientPlayerEntity getCameraPlayer()
	{
	    return minecraft.player;
	}
	
	private static LocalPlayerCap getCameraPlayerData()
	{
		ClientPlayerEntity player = getCameraPlayer();
		if (player == null) return null;
		EntityCapability<?> entityCap = player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (!(entityCap instanceof LocalPlayerCap)) return null;
		return (LocalPlayerCap)entityCap;
	}
}
