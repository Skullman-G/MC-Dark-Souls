package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

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
	
	protected ModMatrix4f transform;
	protected ModMatrix4f backTransform;
	
	public RenderItemBase()
	{
		this.transform = new ModMatrix4f();
		this.transform.rotateDeg(-85, Vector3f.XP);
		this.transform.translate(0, 0.1F, 0);
		
		this.backTransform = new ModMatrix4f();
		this.backTransform.rotateDeg(130, Vector3f.ZP);
		this.backTransform.rotateDeg(90, Vector3f.YP);
		this.backTransform.translate(-0.2F, -0.5F, -0.1F);
	}
	
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float scale, Vector3d translation)
	{
		ModMatrix4f modelMatrix = this.getTransform(stack, itemHolder, hand);
		String heldingHand = hand == InteractionHand.MAIN_HAND ? "Tool_R" : "Tool_L";
		ModMatrix4f jointTransform = itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName(heldingHand).getAnimatedTransform();
		modelMatrix.mulFront(jointTransform);
		ModMatrix4f transpose = new ModMatrix4f().transpose(modelMatrix);
		
		ModMatrix4f.translateStack(poseStack, modelMatrix);
		ModMatrix4f.rotateStack(poseStack, transpose);
		
		poseStack.scale(scale, scale, scale);
		poseStack.translate(translation.x, translation.y, translation.z);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, TransformType.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffer, packedLight);
		GlStateManager._enableDepthTest();
	}
	
	public void renderItemOnBack(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight)
	{
		ModMatrix4f modelMatrix = this.getBackTransform();
		ModMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName("Chest").getAnimatedTransform(), modelMatrix, modelMatrix);
		ModMatrix4f transpose = new ModMatrix4f().transpose(modelMatrix);
		
		ModMatrix4f.translateStack(viewMatrixStack, modelMatrix);
		ModMatrix4f.rotateStack(viewMatrixStack, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer, 0);
	}
	
	protected ModMatrix4f getBackTransform()
	{
		return new ModMatrix4f(this.backTransform);
	}
	
	public void renderItemOnHead(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight, float partialTicks)
	{
		ModMatrix4f modelMatrix = new ModMatrix4f();
		modelMatrix.translate(0F, 0.2F, 0F);
		ModMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
		modelMatrix.scale(0.6F, 0.6F, 0.6F);
		ModMatrix4f transpose = new ModMatrix4f().transpose(modelMatrix);
		ModMatrix4f.translateStack(viewMatrixStack, modelMatrix);
		ModMatrix4f.rotateStack(viewMatrixStack, transpose);
		
		Minecraft.getInstance().getItemInHandRenderer().renderItem(itemHolder.getOriginalEntity(), stack, TransformType.HEAD, false, viewMatrixStack, buffer, packedLight);
	}
	
	public ModMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		return new ModMatrix4f(this.transform);
	}
}