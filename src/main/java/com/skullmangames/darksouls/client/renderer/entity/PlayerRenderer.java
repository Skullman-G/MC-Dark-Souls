package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.capability.entity.AbstractClientPlayerCap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends BipedRenderer<AbstractClientPlayer, AbstractClientPlayerCap<AbstractClientPlayer>>
{
	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayer entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
	
	@Override
	protected void renderNameTag(AbstractClientPlayerCap<AbstractClientPlayer> entityCap, AbstractClientPlayer entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
	{
		EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		
		double d0 = renderManager.distanceToSqr(entityIn);
		matrixStackIn.pushPose();
		if (d0 < 100.0D) {
			Scoreboard scoreboard = entityIn.getScoreboard();
			Objective scoreobjective = scoreboard.getDisplayObjective(2);
			if (scoreobjective != null)
			{
				Score score = scoreboard.getOrCreatePlayerScore(entityIn.getScoreboardName(), scoreobjective);
				super.renderNameTag(entityCap, entityIn, (
						new TextComponent(Integer.toString(score.getScore()))).append(" ").append(scoreobjective.getDisplayName()),
						matrixStackIn, bufferIn, packedLightIn);
				matrixStackIn.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
			}
		}

		super.renderNameTag(entityCap, entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.popPose();
	}
}