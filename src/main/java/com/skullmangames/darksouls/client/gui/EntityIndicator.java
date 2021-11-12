package com.skullmangames.darksouls.client.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityIndicator extends ModIngameGui
{
	public static final List<EntityIndicator> ENTITY_INDICATOR_RENDERERS = Lists.newArrayList();
	public static final ResourceLocation BATTLE_ICON = new ResourceLocation(DarkSouls.MOD_ID, "textures/guis/battle_icons.png");
	
	public static void init()
	{
		new HealthBarIndicator();
	}
	
	public void drawTexturedModalRect2DPlane(Matrix4f matrix, IVertexBuilder vertexBuilder, 
			float minX, float minY, float maxX, float maxY, float minTexU, float minTexV, float maxTexU, float maxTexV)
    {
		this.drawTexturedModalRect3DPlane(matrix, vertexBuilder, minX, minY, this.getBlitOffset(), maxX, maxY, this.getBlitOffset(), minTexU, minTexV, maxTexU, maxTexV);
    }
	
	public void drawTexturedModalRect3DPlane(Matrix4f matrix, IVertexBuilder vertexBuilder, 
			float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float minTexU, float minTexV, float maxTexU, float maxTexV)
    {
        float cor = 0.00390625F;
        
        vertexBuilder.vertex(matrix, minX, minY, maxZ).uv((minTexU * cor), (maxTexV) * cor).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, maxZ).uv((maxTexU * cor), (maxTexV) * cor).endVertex();
        vertexBuilder.vertex(matrix, maxX, maxY, minZ).uv((maxTexU * cor), (minTexV) * cor).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, minZ).uv((minTexU * cor), (minTexV) * cor).endVertex();
    }
	
	public EntityIndicator()
	{
		EntityIndicator.ENTITY_INDICATOR_RENDERERS.add(this);
	}
	
	public Matrix4f getMVMatrix(MatrixStack matStackIn, LivingEntity entityIn, float correctionX, float correctionY, float correctionZ, boolean lockRotation, boolean setupProjection, float partialTicks)
	{
		float posX = (float)MathHelper.lerp((double)partialTicks, entityIn.xOld, entityIn.getX());
		float posY = (float)MathHelper.lerp((double)partialTicks, entityIn.yOld, entityIn.getY());
		float posZ = (float)MathHelper.lerp((double)partialTicks, entityIn.zOld, entityIn.getZ());
		matStackIn.pushPose();
		matStackIn.translate(-posX, -posY, -posZ);
		matStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		
		return this.getMVMatrix(matStackIn, posX + correctionX, posY + correctionY, posZ + correctionZ, lockRotation, setupProjection);
	}
	
	public Matrix4f getMVMatrix(MatrixStack matStackIn, float posX, float posY, float posZ, boolean lockRotation, boolean setupProjection) {
		PublicMatrix4f viewMatrix = PublicMatrix4f.importMatrix(matStackIn.last().pose());
		PublicMatrix4f finalMatrix = new PublicMatrix4f();
		finalMatrix.translate(new Vector3f(-posX, posY, -posZ));
		matStackIn.popPose();
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
		if(setupProjection) PublicMatrix4f.mul(ClientEngine.INSTANCE.renderEngine.getCurrentProjectionMatrix(), finalMatrix, finalMatrix);
		
		return PublicMatrix4f.exportMatrix(finalMatrix);
	}
	
	public abstract void drawIndicator(LivingEntity entityIn, MatrixStack matStackIn, IRenderTypeBuffer ivertexBuilder, float partialTicks);
	public abstract boolean shouldDraw(LivingEntity entityIn);
}