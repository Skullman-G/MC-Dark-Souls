package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
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
		BACK_COORECTION.translate(0.5F, 1, 0.1F);
		BACK_COORECTION.rotate((float)Math.toRadians(130), Vector3f.ZP);
		BACK_COORECTION.rotate((float)Math.toRadians(100), Vector3f.YP);
	}
	
	public RenderItemBase()
	{
		correctionMatrix = new PublicMatrix4f();
		correctionMatrix.rotate((float)Math.toRadians(-80), Vector3f.XP);
		correctionMatrix.translate(0, 0.1F, 0);
	}
	
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand, MultiBufferSource buffer, PoseStack matrixStackIn, int packedLight, float scale, Vector3d translation)
	{
		PublicMatrix4f modelMatrix = this.getCorrectionMatrix(stack, itemHolder, hand);
		String heldingHand = hand == InteractionHand.MAIN_HAND ? "Tool_R" : "Tool_L";
		PublicMatrix4f jointTransform = itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName(heldingHand).getAnimatedTransform();
		PublicMatrix4f.mul(jointTransform, modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		PublicMatrix4f.rotateStack(matrixStackIn, transpose);
		
		matrixStackIn.scale(scale, scale, scale);
		matrixStackIn.translate(translation.x, translation.y, translation.z);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, buffer, packedLight);
		GlStateManager._enableDepthTest();
	}
	
	public void renderItemBack(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f(BACK_COORECTION);
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointById(0).getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer, 0);
	}
	
	public void renderItemOnHead(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight, float partialTicks)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f();
		modelMatrix.translate(0F, 0.2F, 0F);
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
		modelMatrix.scale(0.6F, 0.6F, 0.6F);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, TransformType.HEAD, false, viewMatrixStack, buffer, packedLight);
	}
	
	public PublicMatrix4f getCorrectionMatrix(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		return new PublicMatrix4f(correctionMatrix);
	}
}