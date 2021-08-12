package com.skullmangames.darksouls.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.client.ClientEngine;
import com.skullmangames.darksouls.client.event.engine.RenderEngine;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.common.items.CapabilityItem;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldItemLayer<E extends LivingEntity, T extends LivingData<E>> extends Layer<E, T>
{
	@Override
	public void renderLayer(T entitydata, E entityliving, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, PublicMatrix4f[] poses, float partialTicks)
	{
		ItemStack mainHandStack = entitydata.getOriginalEntity().getMainHandItem();
		RenderEngine renderEngine = ClientEngine.INSTANCE.renderEngine;
		matrixStackIn.pushPose();
		
		if (mainHandStack.getItem() != Items.AIR)
		{
			if (entitydata.getOriginalEntity().getControllingPassenger() != null)
			{
				CapabilityItem itemCap = entitydata.getHeldItemCapability(Hand.MAIN_HAND);
				if (itemCap != null && !itemCap.canUseOnMount())
				{
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemBack(mainHandStack, entitydata, buffer, matrixStackIn, packedLightIn);
					matrixStackIn.popPose();
					return;
				}
			}
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entitydata, Hand.MAIN_HAND, buffer, matrixStackIn, packedLightIn);
		}
		matrixStackIn.popPose();
		matrixStackIn.pushPose();
		ItemStack offHandStack = entitydata.getOriginalEntity().getOffhandItem();
		
		if (offHandStack.getItem() != Items.AIR)
		{
			CapabilityItem cap = entitydata.getHeldItemCapability(Hand.MAIN_HAND);
			if (cap != null)
			{
				if (cap.canBeRenderedBoth(offHandStack))
				{
					renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitydata, Hand.OFF_HAND, buffer, matrixStackIn, packedLightIn);
				}
			}
			else
			{
				renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entitydata, Hand.OFF_HAND, buffer, matrixStackIn, packedLightIn);
			}
		}
		matrixStackIn.popPose();
	}
}