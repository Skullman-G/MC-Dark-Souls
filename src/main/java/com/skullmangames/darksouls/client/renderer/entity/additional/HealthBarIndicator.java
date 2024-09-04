package com.skullmangames.darksouls.client.renderer.entity.additional;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.config.ConfigManager;
import com.skullmangames.darksouls.core.util.timer.Timer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealthBarIndicator extends AdditionalEntityRenderer
{
	private static final ResourceLocation TEXTURE_LOCATION = DarkSouls.rl("textures/entities/additional/health_bar_indicator.png");
	private static final RenderType RENDER_TYPE = ModRenderTypes.getEntityIndicator(TEXTURE_LOCATION);
	
	private final Map<Integer, HealthInfo> healthInfoMap = new HashMap<>();
	
	@Override
	public boolean shouldDraw(LivingEntity entity)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.options.hideGui) return false;
		if (!ConfigManager.CLIENT_CONFIG.showHealthIndicator.getValue()
				|| (entity.isInvisible() || entity == minecraft.player.getControllingPassenger())
				|| entity.distanceToSqr(minecraft.getCameraEntity()) >= 400
				|| minecraft.gui.getBossOverlay().events.containsKey(entity.getUUID())
				|| entity.deathTime >= 19)
		{
			this.healthInfoMap.remove(entity.getId());
			return false;
		}
		else if (entity instanceof Player)
		{
			Player playerIn = (Player) entity;
			if (playerIn == minecraft.player || playerIn.isCreative() || playerIn.isSpectator())
				return false;
		}
		
		if (!this.healthInfoMap.containsKey(entity.getId())) this.healthInfoMap.put(entity.getId(), new HealthInfo(entity));

		if (entity.getHealth() >= entity.getMaxHealth())
		{
			HealthInfo info = this.healthInfoMap.get(entity.getId());
			info.saveLastHealthPercentage = info.lastHealthPercentage;
			info.lastHealthPercentage = entity.getHealth() / entity.getMaxHealth();
			return false;
		}
		
		if (entity.getHealth() <= 0)
		{
			HealthInfo info = this.healthInfoMap.get(entity.getId());
			if (info.lastHealth <= 0 && info.damageNumberTimer <= 0
					&& !info.damageCooldown.isTicking() && !info.damageTimer.isTicking()) return false;
		}
		
		return true;
	}

	@Override
	public void draw(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, entity.getBbHeight() + 0.25F, 0.0F, true, partialTicks);
		VertexConsumer vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
		
		float redStop = 0;
		float blackStart = 0;
		
		HealthInfo info = this.healthInfoMap.get(entity.getId());
		float healthpercentage = entity.getHealth() / entity.getMaxHealth();
		
		// Damage Animation
		if (info.lastHealthPercentage > healthpercentage)
		{
			if (!info.damageCooldown.isTicking())
			{
				info.saveLastHealthPercentage = info.lastHealthPercentage;
			}
			
			info.damageCooldown.start(40);
		}
		
		float damagedHealth = info.saveLastHealthPercentage - (info.damageTimer.getPastTime() * 0.01F);
		
		if (info.damageCooldown.isTicking())
		{
			info.healTimer.stop();
			boolean flag = false;
			if (damagedHealth <= healthpercentage)
			{
				damagedHealth = info.saveLastHealthPercentage;
				flag = true;
			}
			blackStart = damagedHealth;
			info.damageCooldown.drain(1);
			if (!info.damageCooldown.isTicking() && (!info.damageTimer.isTicking() || flag)) info.damageTimer.start((int)(damagedHealth * 100));
		}
		else if (info.damageTimer.isTicking())
		{
			info.healTimer.stop();
			blackStart = damagedHealth;
			info.damageTimer.drain(1);
		}
		
		// Heal Animation
		if ((info.lastHealthPercentage < healthpercentage) || info.healTimer.isTicking())
		{
			info.damageTimer.stop();
			if (!info.healTimer.isTicking())
			{
				info.saveLastHealthPercentage = info.lastHealthPercentage;
				info.healTimer.start((int)((healthpercentage - info.saveLastHealthPercentage) * 100));
			}
			redStop = info.saveLastHealthPercentage + (info.healTimer.getPastTime() * 0.01F);
			info.healTimer.drain(1);
		}
		
		// Default
		else redStop = healthpercentage;
		
		if (blackStart < redStop) blackStart = redStop;
		
		if (redStop != blackStart) this.drawTextured2DPlane(mvMatrix, vertexBuilder, redStop - 0.5F, -0.05F, blackStart - 0.5F, 0.05F, 2 + (int)(redStop * 60), 10, 2 + (int)(blackStart * 60), 15); // Yellow
		this.drawTextured2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, redStop - 0.5F, 0.05F, 1, 5, 2 + (int)(redStop * 60), 10); // Red
		this.drawTextured2DPlane(mvMatrix, vertexBuilder, blackStart - 0.5F, -0.05F, 0.5F, 0.05F, 2 + (int)(blackStart * 60), 0, 62, 5); // Black
		
		if (healthpercentage < info.lastHealthPercentage)
		{
			info.damage = info.damageNumberTimer > 0 ? info.lastHealth - entity.getHealth() + info.damage
							: info.lastHealth - entity.getHealth();
			info.damageNumberTimer = 100;
		}
		
		if (info.damageNumberTimer > 0)
		{
			poseStack.pushPose();
			Camera camera = ClientManager.INSTANCE.mainCamera;
			float scale = 0.03F;
			poseStack.translate(0, entity.getBbHeight() + 0.5, 0);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(-camera.getYRot()));
			poseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
			poseStack.scale(-scale, -scale, scale);
			this.renderDamageNumber(poseStack, info.damage, 10, 0);
			poseStack.popPose();
			--info.damageNumberTimer;
		}

		info.lastHealthPercentage = healthpercentage;
		info.lastHealth = entity.getHealth();
	}

	public void renderDamageNumber(PoseStack matrix, float damage, double x, double y)
	{
		int i = Math.abs(Math.round(damage));
		if (i == 0) return;
		String s = "-"+String.valueOf(i);
		Minecraft minecraft = Minecraft.getInstance();
		int color = 16777215;
		minecraft.font.draw(matrix, s, (int) x - 6 * (s.length() - 1), (int) y, color);
	}
	
	private static class HealthInfo
	{
		private final Timer damageCooldown = new Timer();
		private final Timer damageTimer = new Timer();
		private final Timer healTimer = new Timer();
		
		private float lastHealth;
		private float lastHealthPercentage;
		private float saveLastHealthPercentage;
		
		private float damage;
		private int damageNumberTimer;
		
		private HealthInfo(LivingEntity entity)
		{
			this.lastHealth = entity.getHealth();
		}
	}
}