package com.skullmangames.darksouls.client.gui;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.core.util.timer.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealthBarIndicator extends EntityIndicator
{
	private final Map<Integer, HealthInfo> healthInfoMap = new HashMap<>();
	
	@Override
	public boolean shouldDraw(LivingEntity entityIn)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (!DarkSouls.CLIENT_INGAME_CONFIG.showHealthIndicator.getValue()
				|| (entityIn.isInvisible() || entityIn == minecraft.player.getControllingPassenger())
				|| entityIn.distanceToSqr(minecraft.getCameraEntity()) >= 400
				|| entityIn.deathTime >= 19)
		{
			this.healthInfoMap.remove(entityIn.getId());
			return false;
		}
		else if (entityIn instanceof PlayerEntity)
		{
			PlayerEntity playerIn = (PlayerEntity) entityIn;
			if (playerIn == minecraft.player || playerIn.isCreative() || playerIn.isSpectator())
				return false;
		}
		
		if (!this.healthInfoMap.containsKey(entityIn.getId())) this.healthInfoMap.put(entityIn.getId(), new HealthInfo());

		if (entityIn.getHealth() >= entityIn.getMaxHealth())
		{
			HealthInfo info = this.healthInfoMap.get(entityIn.getId());
			info.saveLastHealth = info.lastHealth;
			info.lastHealth = entityIn.getHealth() / entityIn.getMaxHealth();
			return false;
		}
		return true;
	}

	@Override
	public void drawIndicator(LivingEntity entityIn, MatrixStack matStack, IRenderTypeBuffer bufferIn, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(matStack, entityIn, 0.0F, entityIn.getBbHeight() + 0.25F, 0.0F, true, false, partialTicks);
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(BATTLE_ICON));
		
		float redStop = 0;
		float blackStart = 0;
		
		HealthInfo info = this.healthInfoMap.get(entityIn.getId());
		float maxHealth = entityIn.getMaxHealth();
		float healthpercentage = entityIn.getHealth() / maxHealth;
		
		// Damage Animation
		if (info.lastHealth > healthpercentage)
		{
			if (!info.damageCooldown.isTicking())
			{
				info.saveLastHealth = info.lastHealth;
			}
			
			info.damageCooldown.start(40);
		}
		
		float damagedHealth = info.saveLastHealth - (info.damageTimer.getPastTime() * 0.01F);
		
		if (info.damageCooldown.isTicking())
		{
			info.healTimer.stop();
			boolean flag = false;
			if (damagedHealth <= healthpercentage)
			{
				damagedHealth = info.saveLastHealth;
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
		if ((info.lastHealth < healthpercentage) || info.healTimer.isTicking())
		{
			info.damageTimer.stop();
			if (!info.healTimer.isTicking())
			{
				info.saveLastHealth = info.lastHealth;
				info.healTimer.start((int)((healthpercentage - info.saveLastHealth) * 100));
			}
			redStop = info.saveLastHealth + (info.healTimer.getPastTime() * 0.01F);
			info.healTimer.drain(1);
		}
		
		// Default
		else
		{
			redStop = healthpercentage;
		}
		
		if (blackStart < redStop) blackStart = redStop;
		
		if (redStop != blackStart) this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, redStop - 0.5F, -0.05F, blackStart - 0.5F, 0.05F, 2 + (int)(redStop * 60), 10, 2 + (int)(blackStart * 60), 15); // Yellow
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, redStop - 0.5F, 0.05F, 1, 5, 2 + (int)(redStop * 60), 10); // Red
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, blackStart - 0.5F, -0.05F, 0.5F, 0.05F, 2 + (int)(blackStart * 60), 0, 62, 5); // Black
		
		if (healthpercentage < info.lastHealth)
		{
			info.damage = info.damageNumberTimer > 0 ? (info.lastHealth - healthpercentage) * 100 + info.damage
							: (info.lastHealth - healthpercentage) * 100;
			info.damageNumberTimer = 100;
		}
		
		if (info.damageNumberTimer > 0)
		{
			matStack.pushPose();
			ActiveRenderInfo camera = ClientManager.INSTANCE.mainCamera;
			float scale = 0.03F;
			matStack.translate(0, entityIn.getBbHeight() + 0.5, 0);
			matStack.mulPose(Vector3f.YP.rotationDegrees(-camera.getYRot()));
			matStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
			matStack.scale(-scale, -scale, scale);
			this.renderDamageNumber(matStack, info.damage, 10, 0);
			matStack.popPose();
			--info.damageNumberTimer;
		}

		info.lastHealth = healthpercentage;
	}

	public void renderDamageNumber(MatrixStack matrix, float damage, double x, double y)
	{
		int i = Math.abs(Math.round(damage));
		if (i == 0) return;
		String s = "-"+String.valueOf(i)+"%";
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
		private float saveLastHealth;
		
		private float damage;
		private int damageNumberTimer;
	}
}