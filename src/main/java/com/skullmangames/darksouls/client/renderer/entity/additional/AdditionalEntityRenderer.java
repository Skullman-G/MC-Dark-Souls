package com.skullmangames.darksouls.client.renderer.entity.additional;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AdditionalEntityRenderer extends AbstractGui
{
	public static final List<AdditionalEntityRenderer> ADDITIONAL_ENTITY_RENDERERS = new ArrayList<>();
	
	public static void init()
	{
		new HealthBarIndicator();
		new TargetIndicator();
	}
	
	public void drawTextured2DPlane(Matrix4f matrix, IVertexBuilder vertexBuilder, 
			float minX, float minY, float maxX, float maxY, float minTexU, float minTexV, float maxTexU, float maxTexV)
    {
		this.drawTextured3DPlane(matrix, vertexBuilder, minX, minY, this.getBlitOffset(), maxX, maxY, this.getBlitOffset(), minTexU, minTexV, maxTexU, maxTexV);
    }
	
	public void drawTextured3DPlane(Matrix4f matrix, IVertexBuilder vertexBuilder, 
			float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float minU, float minV, float maxU, float maxV)
    {
        float cor = 0.00390625F;
        
        vertexBuilder.vertex(matrix, minX, minY, maxZ).uv(minU * cor, maxV * cor).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, maxZ).uv(maxU * cor, maxV * cor).endVertex();
        vertexBuilder.vertex(matrix, maxX, maxY, minZ).uv(maxU * cor, minV * cor).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, minZ).uv(minU * cor, minV * cor).endVertex();
    }
	
	public AdditionalEntityRenderer()
	{
		AdditionalEntityRenderer.ADDITIONAL_ENTITY_RENDERERS.add(this);
	}
	
	public Matrix4f getMVMatrix(MatrixStack poseStack, LivingEntity entity, float correctionX, float correctionY, float correctionZ, boolean lockRotation, float partialTicks)
	{
		return this.getMVMatrix(poseStack, entity, correctionX, correctionY, correctionZ, 0.0F, lockRotation, partialTicks);
	}
	
	public Matrix4f getMVMatrix(MatrixStack poseStack, LivingEntity entity, float correctionX, float correctionY, float correctionZ, float xRot, boolean lockRotation, float partialTicks)
	{
		float posX = (float)MathHelper.lerp((double)partialTicks, entity.xOld, entity.getX());
		float posY = (float)MathHelper.lerp((double)partialTicks, entity.yOld, entity.getY());
		float posZ = (float)MathHelper.lerp((double)partialTicks, entity.zOld, entity.getZ());
		poseStack.pushPose();
		poseStack.translate(-posX, -posY, -posZ);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		
		return this.getMVMatrix(poseStack, posX + correctionX, posY + correctionY, posZ + correctionZ, xRot, lockRotation);
	}
	
	public Matrix4f getMVMatrix(MatrixStack poseStack, float posX, float posY, float posZ, float xRot, boolean lockRotation)
	{
		PublicMatrix4f viewMatrix = PublicMatrix4f.importMatrix(poseStack.last().pose());
		PublicMatrix4f finalMatrix = new PublicMatrix4f();
		finalMatrix.translate(-posX, posY, -posZ);
		poseStack.popPose();
		if (lockRotation)
		{
			finalMatrix.m00 = viewMatrix.m00;
			finalMatrix.m01 = viewMatrix.m10;
			finalMatrix.m02 = viewMatrix.m20;
			finalMatrix.m10 = viewMatrix.m01;
			finalMatrix.m11 = viewMatrix.m11;
			finalMatrix.m12 = viewMatrix.m21;
			finalMatrix.m20 = viewMatrix.m02;
			finalMatrix.m21 = viewMatrix.m12;
			finalMatrix.m22 = viewMatrix.m22;
		}
		PublicMatrix4f.mul(viewMatrix, finalMatrix, finalMatrix);
		finalMatrix.rotate((float)Math.toRadians(xRot), new Vector3f(1, 0, 0));
		
		return PublicMatrix4f.exportMatrix(finalMatrix);
	}
	
	public abstract boolean shouldDraw(LivingEntity entity);
	public abstract void draw(LivingEntity entity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, float partialTicks);
}