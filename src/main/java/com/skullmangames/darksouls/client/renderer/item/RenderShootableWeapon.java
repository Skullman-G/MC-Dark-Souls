package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3d;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShootableWeapon extends RenderItemBase
{
	@Override
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight, float scale, Vector3d translation)
	{
		super.renderItemInHand(stack, itemHolder, Hand.OFF_HAND, buffer, viewMatrixStack, packedLight, scale, translation);
	}
}