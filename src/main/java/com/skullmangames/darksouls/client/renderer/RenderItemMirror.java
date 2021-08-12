package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.core.init.ClientModelInit;
import com.skullmangames.darksouls.util.math.MathUtils;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderItemMirror extends RenderItemBase
{
	protected PublicMatrix4f leftHandCorrectionMatrix;
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLight)
	{
		PublicMatrix4f modelMatrix = new PublicMatrix4f(hand == Hand.OFF_HAND ? leftHandCorrectionMatrix : correctionMatrix);
		String heldingHand = hand == Hand.MAIN_HAND ? "Tool_R" : "Tool_L";
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModelInit.CLIENT).getArmature().findJointByName(heldingHand).getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		
		matrixStackIn.pushPose();
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		PublicMatrix4f.rotateStack(matrixStackIn, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, matrixStackIn, buffer);
        matrixStackIn.popPose();
	}
}