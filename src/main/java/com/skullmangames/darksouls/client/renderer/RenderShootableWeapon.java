package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.entities.LivingData;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShootableWeapon extends RenderItemBase
{
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight)
	{
		super.renderItemInHand(stack, itemHolder, Hand.OFF_HAND, buffer, viewMatrixStack, packedLight);
	}
}