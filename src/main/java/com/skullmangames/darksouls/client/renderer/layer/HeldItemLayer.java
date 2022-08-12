package com.skullmangames.darksouls.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.RenderEngine;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.common.capability.item.ItemCapability;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeldItemLayer<E extends LivingEntity, T extends LivingCap<E>> extends Layer<E, T>
{
	private final float scale;
	private final Vector3d translation;
	
	public HeldItemLayer()
	{
		this(1.0F, new Vector3d(0, 0, 0));
	}
	
	public HeldItemLayer(float scale, Vector3d translation)
	{
		this.scale = scale;
		this.translation = translation;
	}
	
	@Override
	public void renderLayer(T entityCap, E entityliving, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, PublicMatrix4f[] poses, float partialTicks)
	{
		ItemStack mainHandStack = entityCap.getOriginalEntity().getMainHandItem();
		RenderEngine renderEngine = ClientManager.INSTANCE.renderEngine;
		matrixStackIn.pushPose();
		
		if (mainHandStack.getItem() != Items.AIR)
		{
			if (entityCap.getOriginalEntity().getControllingPassenger() != null)
			{
				ItemCapability itemCap = entityCap.getHeldItemCapability(Hand.MAIN_HAND);
				if (itemCap != null && !itemCap.canUseOnMount())
				{
					renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemBack(mainHandStack, entityCap, buffer, matrixStackIn, packedLightIn);
					matrixStackIn.popPose();
					return;
				}
			}
			renderEngine.getItemRenderer(mainHandStack.getItem()).renderItemInHand(mainHandStack, entityCap, Hand.MAIN_HAND, buffer, matrixStackIn, packedLightIn, this.scale, this.translation);
		}
		matrixStackIn.popPose();
		matrixStackIn.pushPose();
		ItemStack offHandStack = entityCap.getOriginalEntity().getOffhandItem();
		
		if (offHandStack.getItem() != Items.AIR)
		{
			ItemCapability cap = entityCap.getHeldItemCapability(Hand.MAIN_HAND);
			if (cap != null)
			{
				if (cap.canBeRenderedBoth(offHandStack))
				{
					renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entityCap, Hand.OFF_HAND, buffer, matrixStackIn, packedLightIn, this.scale, this.translation);
				}
			}
			else
			{
				renderEngine.getItemRenderer(offHandStack.getItem()).renderItemInHand(offHandStack, entityCap, Hand.OFF_HAND, buffer, matrixStackIn, packedLightIn, this.scale, this.translation);
			}
		}
		matrixStackIn.popPose();
	}
}