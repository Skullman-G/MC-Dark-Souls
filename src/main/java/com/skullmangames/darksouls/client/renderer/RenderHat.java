package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.entities.LivingData;
import com.skullmangames.darksouls.core.init.ClientModelInit;
import com.skullmangames.darksouls.util.math.MathUtils;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHat extends RenderItemBase
{
	@Override
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight, float partialTicks)
	{
		EntityRenderer<?> render = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(itemHolder.getOriginalEntity());
		if(render instanceof LivingRenderer && ((LivingRenderer<?, ?>)render).getModel() instanceof BipedModel)
		{
			ModelRenderer model = ((BipedModel<?>)((LivingRenderer<?, ?>)render).getModel()).hat;
			@SuppressWarnings({ "unchecked", "rawtypes" })
			HeadLayer<LivingEntity, ?> layer = new HeadLayer(((LivingRenderer<?, ?>)render));
			LivingEntity entity = itemHolder.getOriginalEntity();
			PublicMatrix4f modelMatrix = new PublicMatrix4f();
			PublicMatrix4f.scale(new Vector3f(-0.94F, -0.94F, 0.94F), modelMatrix, modelMatrix);
			if(itemHolder.getOriginalEntity().isBaby())
			{
				PublicMatrix4f.translate(new Vector3f(0.0F, -0.65F, 0.0F), modelMatrix, modelMatrix);
			}
			PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModelInit.CLIENT).getArmature().findJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
			model.xRot = 0.0F;
			model.yRot = 0.0F;
			model.zRot = 0.0F;
			model.x = 0.0F;
			model.y = 0.0F;
			model.z = 0.0F;
			PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
			viewMatrixStack.pushPose();
			MathUtils.translateStack(viewMatrixStack, modelMatrix);
			PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
			layer.render(viewMatrixStack, buffer, packedLight, entity, 0F, 0F, 0F, 0F, 0F, 0F);
			viewMatrixStack.popPose();
		}
	}
}