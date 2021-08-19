package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.capability.entity.RemoteClientPlayerData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends BipedRenderer<AbstractClientPlayerEntity, RemoteClientPlayerData<AbstractClientPlayerEntity>>
{
	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayerEntity entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
	
	@Override
	protected void renderNameTag(RemoteClientPlayerData<AbstractClientPlayerEntity> entitydata, AbstractClientPlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		
		double d0 = renderManager.distanceToSqr(entityIn);
		matrixStackIn.pushPose();
		if (d0 < 100.0D) {
			Scoreboard scoreboard = entityIn.getScoreboard();
			ScoreObjective scoreobjective = scoreboard.getDisplayObjective(2);
			if (scoreobjective != null)
			{
				Score score = scoreboard.getOrCreatePlayerScore(entityIn.getScoreboardName(), scoreobjective);
				super.renderNameTag(entitydata, entityIn, (
						new StringTextComponent(Integer.toString(score.getScore()))).append(" ").append(scoreobjective.getDisplayName()),
						matrixStackIn, bufferIn, packedLightIn);
				matrixStackIn.translate(0.0D, (double) (9.0F * 1.15F * 0.025F), 0.0D);
			}
		}

		super.renderNameTag(entitydata, entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.popPose();
	}
}