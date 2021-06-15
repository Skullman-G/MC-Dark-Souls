package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.items.IHaveDarkSoulsUseAction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class FirstPersonRendererOverride
{
	public static void renderArmWithItem(IHaveDarkSoulsUseAction item, float swingProgress, float partialticks, float equipProgress, Hand hand, ItemStack itemstack, MatrixStack matrixstack, IRenderTypeBuffer rendertypebuffer, int i)
	{
		Minecraft minecraft = Minecraft.getInstance();
		AbstractClientPlayerEntity player = minecraft.player;
		boolean flag = hand == Hand.MAIN_HAND;
		HandSide handside = flag ? player.getMainArm() : player.getMainArm().getOpposite();
		matrixstack.pushPose();
		boolean flag3 = handside == HandSide.RIGHT;
        if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand)
        {
        	switch (item.getDarkSoulsUseAnimation(itemstack))
        	{
        	case NONE:
        		applyItemArmTransform(matrixstack, handside, equipProgress);
        		break;
        		
        	case SOUL_CONTAINER:
        		applyConsumeTransform(matrixstack, partialticks, handside, itemstack);
            	applyItemArmTransform(matrixstack, handside, equipProgress);
            	break;
        	}
        }
        else
        {
        	float f5 = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
            float f6 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
            float f10 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
            int l = flag3 ? 1 : -1;
            matrixstack.translate((double)((float)l * f5), (double)f6, (double)f10);
            applyItemArmTransform(matrixstack, handside, equipProgress);
            applyItemArmAttackTransform(matrixstack, handside, swingProgress);
        }
        
        minecraft.itemInHandRenderer.renderItem(player, itemstack, flag3 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, matrixstack, rendertypebuffer, i);
        
        matrixstack.popPose();
	}
	
	private static void applyItemArmAttackTransform(MatrixStack matrixstack, HandSide handside, float swingProgress)
	{
	    int i = handside == HandSide.RIGHT ? 1 : -1;
	    float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
	    matrixstack.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
	    float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
	    matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
	    matrixstack.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
	    matrixstack.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));
	}
	
	private static void applyItemArmTransform(MatrixStack matrixstack, HandSide handside, float equipProgress)
	{
	    int i = handside == HandSide.RIGHT ? 1 : -1;
	    matrixstack.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProgress * -0.6F), (double)-0.72F);
	}
	
	private static void applyConsumeTransform(MatrixStack matrixstack, float partialticks, HandSide handside, ItemStack itemstack)
	{
		Minecraft minecraft = Minecraft.getInstance();
		float f0 = (float)minecraft.player.getUseItemRemainingTicks() - partialticks + 1.0F;
		float f1 = f0 / (float)itemstack.getUseDuration();
		
		if (f1 < 0.8F)
		{
	       float f2 = MathHelper.abs(MathHelper.cos(f0 / 50.0F * (float)Math.PI) * 0.1F);
	       matrixstack.translate(0.0D, (double)f2, 0.0D);
	    }
		
		float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
	    int i = handside == HandSide.RIGHT ? 1 : -1;
	    matrixstack.translate((double)(f3 * 0.6F * (float)i), (double)(f3 * -0.5F), (double)(f3 * 0.0F));
	    matrixstack.mulPose(Vector3f.YP.rotationDegrees((float)i * f3 * 90.0F));
	    matrixstack.mulPose(Vector3f.XP.rotationDegrees(f3 * 10.0F));
	    matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float)i * f3 * 30.0F));
	}
}
