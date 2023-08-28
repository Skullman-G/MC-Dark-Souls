package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.common.entity.projectile.LightningSpear;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

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
	private final float scale;
	
	private LightningSpearRenderer(Context context, float scale)
	{
		super(context);
		this.scale = scale;
	}
	
	public static LightningSpearRenderer lightningSpear(Context context)
	{
		return new LightningSpearRenderer(context, 1.0F);
	}
	
	public static LightningSpearRenderer greatLightningSpear(Context context)
	{
		return new LightningSpearRenderer(context, 1.5F);
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
		Quaternion rot = ModMatrix4f.createModelMatrix((float) entity.xOld, (float) entity.getX(),
				(float) entity.yOld, (float) entity.getY(), (float) entity.zOld, (float) entity.getZ(), -entity.xRotO,
				-entity.xRot, entity.yRotO, entity.yRot, 1.0F, 1.0F, 1.0F, 1.0F).transpose().rotate((float)Math.toRadians(180), Vector3f.YP).toQuaternion().vanilla();
		rot.mul(Vector3f.YP.rotationDegrees(90));
		
		poseStack.scale(this.scale, this.scale, this.scale);
		poseStack.pushPose();
		poseStack.translate(0.0F, 0.25F, 0.0F);
		poseStack.mulPose(rot);
		drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
		drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		drawTexturedPlane(vertexBuilder, poseStack.last().pose(), -1.0F, -0.15F, 1.0F, 0.15F, 0, 0, 32, 5);
		poseStack.popPose();
		
		rot.mul(Vector3f.ZN.rotationDegrees(45));
		for (int i = 0; i < 7; i++)
		{
			Vector3f v = new Vector3f(-0.6F + 0.2F * i, -0.6F + 0.2F * i, 0.0F);
			v.transform(rot);
			poseStack.pushPose();
			poseStack.translate(v.x(), v.y(), v.z());
			poseStack.translate(0.0F, 0.25F, 0.0F);
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
