package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.common.entity.LightningSpear;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class LightningSpearRenderer extends EntityRenderer<LightningSpear>
{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/particle/lightning_spear.png");
	private static final RenderType RENDER_TYPE = ModRenderTypes.getEffectEntity(TEXTURE_LOCATION);
	
	public LightningSpearRenderer(Context context)
	{
		super(context);
	}
	
	@Override
	protected int getBlockLightLevel(LightningSpear entity, BlockPos blockPos)
	{
		return MathUtils.clamp(super.getBlockLightLevel(entity, blockPos) + 7, 0, 15);
	}
	
	@Override
	public void render(LightningSpear entity, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource bufferSource, int uv2)
	{
		VertexConsumer vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
		
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.5F, 0.0F);
		drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5);
		poseStack.popPose();
		
		for (int i = 0; i < 5; i++)
		{
			poseStack.pushPose();
			poseStack.translate(-0.8F + 0.4F * i, 0.5F, 0.0F);
			poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			poseStack.scale(0.15F, 0.15F, 0.15F);
			int r = entity.getParticle() + i;
			if (r > 5) r -= 5;
			drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -1.25F, 1.0F, 1.25F, 1 + 5 * r, 6, 5 + 5 * r, 12);
			poseStack.popPose();
		}
		
		super.render(entity, p_114486_, p_114487_, poseStack, bufferSource, uv2);
	}
	
	private static void drawTexturedPlane(VertexConsumer vertexBuilder, Matrix4f poseMatrix, float minX, float minY, float maxX, float maxY,
			float minU, float minV, float maxU, float maxV)
	{
		float cor = 0.00390625F;
		vertexBuilder.vertex(poseMatrix, minX, minY, 0.0F).uv(minU * cor, maxV * cor).endVertex();
		vertexBuilder.vertex(poseMatrix, maxX, minY, 0.0F).uv(maxU * cor, maxV * cor).endVertex();
		vertexBuilder.vertex(poseMatrix, maxX, maxY, 0.0F).uv(maxU * cor, minV * cor).endVertex();
		vertexBuilder.vertex(poseMatrix, minX, maxY, 0.0F).uv(minU * cor, minV * cor).endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(LightningSpear entity)
	{
		return TEXTURE_LOCATION;
	}
}
