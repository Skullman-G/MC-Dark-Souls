package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.item.HasDarkSoulsUseAction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class FirstPersonRendererOverride
{
	public static void renderArmWithItem(HasDarkSoulsUseAction item, float swingProgress, float partialticks,
			float equipProgress, InteractionHand hand, ItemStack itemstack, PoseStack matrixstack,
			MultiBufferSource rendertypebuffer, int i)
	{
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		boolean flag = hand == InteractionHand.MAIN_HAND;
		HumanoidArm handside = flag ? player.getMainArm() : player.getMainArm().getOpposite();
		matrixstack.pushPose();
		boolean flag3 = handside == HumanoidArm.RIGHT;
		if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand)
		{
			switch (item.getDarkSoulsUseAnimation())
			{
				case SOUL_CONTAINER:
					applyConsumeTransform(matrixstack, partialticks, handside, itemstack);
					applyItemArmTransform(matrixstack, handside, equipProgress);
					break;
	
				case MIRACLE:
					applyConsumeTransform(matrixstack, partialticks, handside, itemstack);
					applyItemArmTransform(matrixstack, handside, equipProgress);
					break;
	
				case DARKSIGN:
					applyConsumeTransform(matrixstack, partialticks, handside, itemstack);
					applyItemArmTransform(matrixstack, handside, equipProgress);
					break;
	
				default:
					applyItemArmTransform(matrixstack, handside, equipProgress);
					break;
			}
		}
		else
		{
			double f5 = -0.4F * Math.sin(Math.sqrt(swingProgress) * (float) Math.PI);
			double f6 = 0.2F * Math.sin(Math.sqrt(swingProgress) * ((float) Math.PI * 2F));
			double f10 = -0.2F * Math.sin(swingProgress * (float) Math.PI);
			int l = flag3 ? 1 : -1;
			matrixstack.translate((double) ((float) l * f5), (double) f6, (double) f10);
			applyItemArmTransform(matrixstack, handside, equipProgress);
			applyItemArmAttackTransform(matrixstack, handside, swingProgress);
		}

		minecraft.getItemInHandRenderer().renderItem(player, itemstack,
				flag3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
						: ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
				!flag3, matrixstack, rendertypebuffer, i);

		matrixstack.popPose();
	}

	private static void applyItemArmAttackTransform(PoseStack matrixstack, HumanoidArm handside, float swingProgress)
	{
		int i = handside == HumanoidArm.RIGHT ? 1 : -1;
		double f = Math.sin(swingProgress * swingProgress * (float) Math.PI);
		matrixstack.mulPose(Vector3f.YP.rotationDegrees((float)(i * (45.0F + f * -20.0F))));
		double f1 = Math.sin(Math.sqrt(swingProgress) * (float) Math.PI);
		matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float)(i * f1 * -20.0F)));
		matrixstack.mulPose(Vector3f.XP.rotationDegrees((float)(f1 * -80.0F)));
		matrixstack.mulPose(Vector3f.YP.rotationDegrees((float)(i * -45.0F)));
	}

	private static void applyItemArmTransform(PoseStack matrixstack, HumanoidArm handside, float equipProgress)
	{
		int i = handside == HumanoidArm.RIGHT ? 1 : -1;
		matrixstack.translate((double) ((float) i * 0.56F), (double) (-0.52F + equipProgress * -0.6F), (double) -0.72F);
	}

	private static void applyConsumeTransform(PoseStack matrixstack, float partialticks, HumanoidArm handside,
			ItemStack itemstack)
	{
		Minecraft minecraft = Minecraft.getInstance();
		float f0 = (float) minecraft.player.getUseItemRemainingTicks() - partialticks + 1.0F;
		float f1 = f0 / (float) itemstack.getUseDuration();

		if (f1 < 0.8F)
		{
			double f2 = Math.abs(Math.cos(f0 / 50.0F * (float) Math.PI) * 0.1F);
			matrixstack.translate(0.0D, (double) f2, 0.0D);
		}

		float f3 = 1.0F - (float) Math.pow((double) f1, 27.0D);
		int i = handside == HumanoidArm.RIGHT ? 1 : -1;
		matrixstack.translate((double) (f3 * 0.6F * (float) i), (double) (f3 * -0.5F), (double) (f3 * 0.0F));
		matrixstack.mulPose(Vector3f.YP.rotationDegrees((float) i * f3 * 90.0F));
		matrixstack.mulPose(Vector3f.XP.rotationDegrees(f3 * 10.0F));
		matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float) i * f3 * 30.0F));
	}
}
