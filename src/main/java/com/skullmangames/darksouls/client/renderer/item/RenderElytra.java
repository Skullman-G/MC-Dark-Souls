package com.skullmangames.darksouls.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.MathUtils;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderElytra extends RenderItemBase {
	private final ElytraModel<LivingEntity> modelElytra;
	private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
	
	public RenderElytra() {
		super();
		modelElytra = new ElytraModel<>();
	}
	
	@Override
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight, float partialTicks) {
		LivingEntity entity = itemHolder.getOriginalEntity();
		PublicMatrix4f modelMatrix = new PublicMatrix4f();
		PublicMatrix4f.scale(-0.9F, -0.9F, 0.9F, modelMatrix, modelMatrix);
		PublicMatrix4f.translate(new Vector3f(0F, -0.5F, 0.125F), modelMatrix, modelMatrix);
		PublicMatrix4f.mul(itemHolder.getEntityModel(ClientModels.CLIENT).getArmature().findJointById(8).getAnimatedTransform(), modelMatrix, modelMatrix);
		PublicMatrix4f transpose = PublicMatrix4f.transpose(modelMatrix, null);
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		PublicMatrix4f.rotateStack(viewMatrixStack, transpose);
		
		float f = MathUtils.interpolateRotation(entity.yBodyRotO, entity.yBodyRot, partialTicks);
        float f1 = MathUtils.interpolateRotation(entity.yHeadRotO, entity.yHeadRot, partialTicks);
        float f2 = f1 - f;
		float f7 = entity.getViewXRot(partialTicks);
		
		ResourceLocation resourcelocation;
        if (entity instanceof AbstractClientPlayerEntity) {
           AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)entity;
           if (abstractclientplayerentity.isReducedDebugInfo() && abstractclientplayerentity.getElytraTextureLocation() != null) {
              resourcelocation = abstractclientplayerentity.getElytraTextureLocation();
           } else if (abstractclientplayerentity.isReducedDebugInfo() && abstractclientplayerentity.getCloakTextureLocation() != null && abstractclientplayerentity.isModelPartShown(PlayerModelPart.CAPE)) {
              resourcelocation = abstractclientplayerentity.getCloakTextureLocation();
           } else {
              resourcelocation = TEXTURE_ELYTRA;
           }
        } else {
           resourcelocation = TEXTURE_ELYTRA;
        }
		
		this.modelElytra.young = entity.isBaby();
        this.modelElytra.setupAnim(entity, entity.swingTime, entity.animationSpeed, entity.tickCount, f2, f7);
	    IVertexBuilder ivertexbuilder = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCull(resourcelocation), false, stack.isEnchanted());
	    this.modelElytra.renderToBuffer(viewMatrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
