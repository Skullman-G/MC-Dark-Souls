package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderItemBase
{
	protected PublicMatrix4f correctionMatrix;
	
	protected static final PublicMatrix4f BACK_COORECTION;
	public static RenderEngine renderEngine;
	
	static
	{
		BACK_COORECTION = new PublicMatrix4f();
		PublicMatrix4f.translate(new Vector3f(0.5F, 1, 0.1F), BACK_COORECTION, BACK_COORECTION);
		PublicMatrix4f.rotate((float)Math.toRadians(130), new Vector3f(0, 0, 1), BACK_COORECTION, BACK_COORECTION);
		PublicMatrix4f.rotate((float)Math.toRadians(100), new Vector3f(0, 1, 0), BACK_COORECTION, BACK_COORECTION);
	}
	
	public RenderItemBase()
	{
		correctionMatrix = new PublicMatrix4f();
		PublicMatrix4f.rotate((float)Math.toRadians(-80), new Vector3f(1,0,0), correctionMatrix, correctionMatrix);
		PublicMatrix4f.translate(new Vector3f(0,0.1F,0), correctionMatrix, correctionMatrix);
	}
	
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLight, float scale, Vector3d translation)
	{
		PublicMatrix4f modelMatrix = this.getCorrectionMatrix(stack, itemHolder, hand);
		String heldingHand = hand == Hand.MAIN_HAND ? "Tool_R" : "Tool_L";
		PublicMatrix4f jointTransform = itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().findJointByName(heldingHand).getAnimatedTransform();
		PublicMatrix4f.mul(jointTransform, modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		PublicMatrix4f.rotateStack(matrixStackIn, transpose);
		
		matrixStackIn.scale(scale, scale, scale);
		matrixStackIn.translate(translation.x, translation.y, translation.z);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, buffer, packedLight);
		GlStateManager._enableDepthTest();
	}
	
	public void renderItemBack(ItemStack stack, LivingData<?> itemHolder, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f(BACK_COORECTION);
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().findJointById(0).getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer);
	}
	
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight, float partialTicks)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f();
		PublicMatrix4f.translate(new Vector3f(0F, 0.2F, 0F), modelMatrix, modelMatrix);
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().findJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f.scale(0.6F, 0.6F, 0.6F, modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, ItemCameraTransforms.TransformType.HEAD, false, viewMatrixStack, buffer, packedLight);
	}
	
	public PublicMatrix4f getCorrectionMatrix(ItemStack stack, LivingData<?> itemHolder, Hand hand)
	{
		return new PublicMatrix4f(correctionMatrix);
	}
}