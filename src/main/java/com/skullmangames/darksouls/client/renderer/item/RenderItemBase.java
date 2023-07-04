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
	public static RenderEngine renderEngine;
	
	protected PublicMatrix4f transform;
	protected PublicMatrix4f backTransform;
	
	public RenderItemBase()
	{
		this.transform = new PublicMatrix4f();
		this.transform.rotate((float)Math.toRadians(-80), Vector3f.XP);
		this.transform.translate(0, 0.1F, 0);
		
		this.backTransform = new PublicMatrix4f();
		this.backTransform.rotate((float)Math.toRadians(130), Vector3f.ZP);
		this.backTransform.rotate((float)Math.toRadians(90), Vector3f.YP);
		this.backTransform.translate(-0.2F, -0.5F, -0.1F);
	}
	
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float scale, Vector3d translation)
	{
		PublicMatrix4f modelMatrix = this.getTransform(stack, itemHolder, hand);
		String heldingHand = hand == InteractionHand.MAIN_HAND ? "Tool_R" : "Tool_L";
		PublicMatrix4f jointTransform = itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName(heldingHand).getAnimatedTransform();
		modelMatrix.mulFront(jointTransform);
		PublicMatrix4f transpose = new PublicMatrix4f().transpose(modelMatrix);
		
		MathUtils.translateStack(poseStack, modelMatrix);
		PublicMatrix4f.rotateStack(poseStack, transpose);
		
		poseStack.scale(scale, scale, scale);
		poseStack.translate(translation.x, translation.y, translation.z);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, TransformType.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffer, packedLight);
		GlStateManager._enableDepthTest();
	}
	
	public void renderItemOnBack(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight)
	{
		PublicMatrix4f modelMatrix = this.getBackTransform();
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName("Chest").getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f transpose = new PublicMatrix4f().transpose(modelMatrix);
		
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer, 0);
	}
	
	protected PublicMatrix4f getBackTransform()
	{
		return new PublicMatrix4f(this.backTransform);
	}
	
	public void renderItemOnHead(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight, float partialTicks)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f();
		modelMatrix.translate(0F, 0.2F, 0F);
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
		modelMatrix.scale(0.6F, 0.6F, 0.6F);
		PublicMatrix4f transpose = new PublicMatrix4f().transpose(modelMatrix);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, TransformType.HEAD, false, viewMatrixStack, buffer, packedLight);
	}
	
	public PublicMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		return new PublicMatrix4f(this.transform);
	}
}