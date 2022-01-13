package com.skullmangames.darksouls.client.gui;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealthBarIndicator extends EntityIndicator
{
	@Override
	public boolean shouldDraw(LivingEntity entityIn)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (!DarkSouls.CLIENT_INGAME_CONFIG.showHealthIndicator.getValue())
			return false;
		else if (entityIn.isInvisible() || entityIn == minecraft.player.getControllingPassenger())
			return false;
		else if (entityIn.distanceToSqr(minecraft.getCameraEntity()) >= 400)
			return false;
		else if (entityIn instanceof Player)
		{
			Player playerIn = (Player) entityIn;
			if (playerIn == minecraft.player)
				return false;
			else if (playerIn.isCreative() || playerIn.isSpectator())
				return false;
		}

		if (entityIn.getActiveEffects().isEmpty() && entityIn.getHealth() >= entityIn.getMaxHealth() || entityIn.deathTime >= 19)
			return false;
		return true;
	}

	@Override
	public void drawIndicator(LivingEntity entityIn, PoseStack matStack, MultiBufferSource bufferIn, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(matStack, entityIn, 0.0F, entityIn.getBbHeight() + 0.25F, 0.0F, true, false, partialTicks);

		if (!entityIn.getActiveEffects().isEmpty())
		{
			Collection<MobEffectInstance> activeEffects = entityIn.getActiveEffects();
			Iterator<MobEffectInstance> iter = activeEffects.iterator();
			int acives = activeEffects.size();
			int row = acives > 1 ? 1 : 0;
			int column = ((acives - 1) / 2);
			float startX = -0.8F + -0.3F * row;
			float startY = -0.15F + 0.15F * column;

			for (int i = 0; i <= column; i++)
			{
				for (int j = 0; j <= row; j++)
				{
					MobEffect effect = iter.next().getEffect();
					ResourceLocation rl;

					rl = new ResourceLocation("textures/mob_effect/" + effect.getRegistryName().getPath() + ".png");

					RenderSystem.setShaderTexture(0, rl);
					float x = startX + 0.3F * j;
					float y = startY + -0.3F * i;

					VertexConsumer vertexBuilder1 = bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(rl));

					this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder1, x, y, x + 0.3F, y + 0.3F, 0, 0, 256, 256);
					if (!iter.hasNext())
						break;
				}
			}
		}

		VertexConsumer vertexBuilder = bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(BATTLE_ICON));
		float ratio = entityIn.getHealth() / entityIn.getMaxHealth();
		float healthRatio = -0.5F + ratio;
		int textureRatio = (int) (62 * ratio);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, healthRatio, 0.05F, 1, 15, textureRatio, 20);
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, healthRatio, -0.05F, 0.5F, 0.05F, textureRatio, 10, 62, 15);
		float absorption = entityIn.getAbsorptionAmount();

		if (absorption > 0.0D)
		{
			float absorptionRatio = absorption / entityIn.getMaxHealth();
			int absTexRatio = (int) (62 * absorptionRatio);
			this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -0.5F, -0.05F, absorptionRatio - 0.5F, 0.05F, 1, 0, absTexRatio, 25);
		}
		
		CompoundTag nbt = entityIn.getPersistentData();
		float lastHealth = nbt.getFloat("HBI_lastHealth");
		
		if (lastHealth > entityIn.getHealth())
		{
			float damage = nbt.getFloat("HBI_damageTimer") > 0 ? lastHealth - entityIn.getHealth() + nbt.getFloat("HBI_damage")
							: lastHealth - entityIn.getHealth();
			nbt.putFloat("HBI_damage", damage);
			nbt.putFloat("HBI_damageTimer", 100.0F);
		}
		
		float damageTimer = nbt.getFloat("HBI_damageTimer");
		
		if (damageTimer > 0)
		{
			matStack.pushPose();
			Camera camera = ClientManager.INSTANCE.mainCamera;
			float scale = 0.03F;
			matStack.translate(0, entityIn.getBbHeight() + 0.5, 0);
			matStack.mulPose(Vector3f.YP.rotationDegrees(-camera.getYRot()));
			matStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
			matStack.scale(-scale, -scale, scale);
			this.renderDamageNumber(matStack, nbt.getFloat("HBI_damage"), 10, 0);
			matStack.popPose();
			nbt.putFloat("HBI_damageTimer", damageTimer - 1.0F);
		}

		nbt.putFloat("HBI_lastHealth", entityIn.getHealth());
	}

	public void renderDamageNumber(PoseStack matrix, float damage, double x, double y)
	{
		int i = Math.abs(Math.round(damage * 10));
		if (i == 0) return;
		String s = "-"+String.valueOf(i);
		Minecraft minecraft = Minecraft.getInstance();
		int color = 16777215;
		minecraft.font.draw(matrix, s, (int) x - 6 * (s.length() - 1), (int) y, color);
	}
}