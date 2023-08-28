package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHat extends RenderItemBase
{
	@SuppressWarnings("unchecked")
	@Override
	public void renderItemOnHead(ItemStack stack, LivingCap<?> itemHolder, MultiBufferSource buffer, PoseStack viewMatrixStack, int packedLight, float partialTicks)
	{
		EntityRenderer<?> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(itemHolder.getOriginalEntity());
		if(render instanceof LivingEntityRenderer && ((LivingEntityRenderer<?, ?>)render).getModel() instanceof HumanoidModel)
		{
			ModelPart model = ((HumanoidModel<?>)((LivingEntityRenderer<?, ?>)render).getModel()).hat;
			CustomHeadLayer<LivingEntity, ?> layer = null;
			for (RenderLayer<LivingEntity, ?> l : ((LivingEntityRenderer<LivingEntity, ?>)render).layers)
			{
				if (l instanceof CustomHeadLayer<LivingEntity, ?>) layer = (CustomHeadLayer<LivingEntity, ?>)l;
			}
			if (layer == null) return;
			LivingEntity entity = itemHolder.getOriginalEntity();
			ModMatrix4f modelMatrix = new ModMatrix4f();
			modelMatrix.scale(-0.94F, -0.94F, 0.94F);
			if(itemHolder.getOriginalEntity().isBaby())
			{
				modelMatrix.translate(0.0F, -0.65F, 0.0F);
			}
			ModMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().searchJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
			model.xRot = 0.0F;
			model.yRot = 0.0F;
			model.zRot = 0.0F;
			model.x = 0.0F;
			model.y = 0.0F;
			model.z = 0.0F;
			ModMatrix4f transpose = new ModMatrix4f().transpose(modelMatrix);
			viewMatrixStack.pushPose();
			ModMatrix4f.translateStack(viewMatrixStack, modelMatrix);
			ModMatrix4f.rotateStack(viewMatrixStack, transpose);
			layer.render(viewMatrixStack, buffer, packedLight, entity, 0F, 0F, 0F, 0F, 0F, 0F);
			viewMatrixStack.popPose();
		}
	}
}