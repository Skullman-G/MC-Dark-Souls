package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderItemMirror extends RenderItemBase
{
	protected PublicMatrix4f leftHandCorrectionMatrix;
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLight, float scale, Vector3d translation)
	{
		boolean isMainHand = hand == Hand.MAIN_HAND;
		PublicMatrix4f modelMatrix = new PublicMatrix4f(isMainHand ? this.correctionMatrix : this.leftHandCorrectionMatrix);
		String heldingHand = isMainHand ? "Tool_R" : "Tool_L";
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointByName(heldingHand).getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		
		matrixStackIn.pushPose();
		MathUtils.translateStack(matrixStackIn, modelMatrix);
		PublicMatrix4f.rotateStack(matrixStackIn, transpose);
		
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, matrixStackIn, buffer);
        matrixStackIn.popPose();
	}
}