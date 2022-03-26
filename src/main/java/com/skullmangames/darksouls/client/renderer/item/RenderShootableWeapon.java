package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShootableWeapon extends RenderItemBase
{
	@Override
	public void renderItemInHand(ItemStack stack, LivingCap<?> itemHolder, InteractionHand hand, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight, float scale, Vector3d translation)
	{
		super.renderItemInHand(stack, itemHolder, InteractionHand.OFF_HAND, buffer, viewMatrixStack, packedLight, scale, translation);
	}
}