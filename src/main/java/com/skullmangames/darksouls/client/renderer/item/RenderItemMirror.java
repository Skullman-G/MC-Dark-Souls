package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderItemMirror extends RenderItemBase
{
	protected ModMatrix4f leftTransform;
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand, MultiBufferSource buffer, PoseStack matrixStack, int packedLight, float scale, Vector3d translation)
	{
		boolean isMainHand = hand == InteractionHand.MAIN_HAND;
		ModMatrix4f modelMatrix = this.getTransform(stack, itemHolder, hand);
		String handBone = isMainHand ? "Tool_R" : "Tool_L";
		ModMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName(handBone).getAnimatedTransform(), modelMatrix, modelMatrix);
		ModMatrix4f transpose = new ModMatrix4f().transpose(modelMatrix);
		
		matrixStack.pushPose();
		ModMatrix4f.translateStack(matrixStack, modelMatrix);
		ModMatrix4f.rotateStack(matrixStack, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderStatic(stack,
				isMainHand ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.THIRD_PERSON_LEFT_HAND,
				packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, -1);
        matrixStack.popPose();
	}

	@Override
	public ModMatrix4f getTransform(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand)
	{
		return new ModMatrix4f(hand == InteractionHand.MAIN_HAND ? this.transform : this.leftTransform);
	}
}