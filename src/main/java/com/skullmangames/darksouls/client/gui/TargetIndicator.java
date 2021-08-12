package com.skullmangames.darksouls.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TargetIndicator extends EntityIndicator
{
	@SuppressWarnings("resource")
	@Override
	public boolean shouldDraw(LivingEntity entityIn)
	{
		if (!DarkSouls.CLIENT_INGAME_CONFIG.showTargetIndicator.getValue()) return false;
		else if(entityIn != ClientEngine.INSTANCE.getPlayerData().getTarget()) return false;
		else if(entityIn.isInvisible() || !entityIn.isAlive() || entityIn == Minecraft.getInstance().player.getControllingPassenger()) return false;
		else if(entityIn.distanceToSqr(Minecraft.getInstance().getCameraEntity()) >= 400) return false;
		else if (entityIn instanceof PlayerEntity)
		{
			PlayerEntity playerIn = (PlayerEntity) entityIn;
			if(playerIn.isSpectator())
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void drawIndicator(LivingEntity entityIn, MatrixStack matStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
		Matrix4f mvMatrix = super.getMVMatrix(matStackIn, entityIn, 0.0F, entityIn.getBbHeight() + 0.45F, 0.0F, true, false, partialTicks);
		this.drawTexturedModalRect2DPlane(mvMatrix, bufferIn.getBuffer(ModRenderTypes.getEntityIndicator(BATTLE_ICON)),
				-0.1F, -0.1F, 0.1F, 0.1F, 65, 2, 91, 36);
	}
}