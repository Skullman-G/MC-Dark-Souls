package com.skullmangames.darksouls.client.renderer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.ArmatureRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.client.renderer.layer.WearableItemLayer;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

public class PlayerGuiRenderer extends ArmatureRenderer<ClientPlayerEntity, LocalPlayerCap>
{
	public PlayerGuiRenderer()
	{
		super();
		layers.add(new HeldItemLayer<>());
		layers.add(new WearableItemLayer<>(EquipmentSlotType.HEAD));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.CHEST));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.LEGS));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.FEET));
	}
	
	@Override
	public void render(LocalPlayerCap entityCap, @Nullable EntityRenderer<ClientPlayerEntity> renderer, IRenderTypeBuffer buffer, MatrixStack poseStack, int packedLight, float partialTicks)
	{
		ClientModel model = entityCap.getEntityModel(ClientModels.CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entityCap.getClientAnimator().setPoseToModel(partialTicks);
		PublicMatrix4f[] poses = armature.getJointTransforms();
		
		MatrixStack poseStack1 = new MatrixStack();
		poseStack1.translate(0.0D, 0.0D, 1000.0D);
		PublicMatrix4f.scaleStack(poseStack1, PublicMatrix4f.importMatrix(poseStack.last().pose()));
		poseStack1.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		IVertexBuilder vc = buffer.getBuffer(ModRenderTypes.getAnimatedModel(entityCap.getOriginalEntity().getSkinTextureLocation()));
		ClientModels.CLIENT.ENTITY_BIPED.draw(poseStack1, vc, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		this.renderLayer(entityCap, poses, buffer, poseStack1, packedLight, partialTicks);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(ClientPlayerEntity entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
}
