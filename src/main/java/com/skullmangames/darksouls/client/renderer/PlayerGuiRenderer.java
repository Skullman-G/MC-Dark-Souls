package com.skullmangames.darksouls.client.renderer;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.client.renderer.entity.ArmatureRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.client.renderer.layer.Layer;
import com.skullmangames.darksouls.client.renderer.layer.WearableItemLayer;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.ModMatrix4f;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class PlayerGuiRenderer extends ArmatureRenderer<LocalPlayer, LocalPlayerCap>
{
	public PlayerGuiRenderer()
	{
		super();
		this.layers.add(new HeldItemLayer<>());
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.HEAD));
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.CHEST));
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.LEGS));
		this.layers.add(new WearableItemLayer<>(EquipmentSlot.FEET));
	}
	
	@Override
	public void render(LocalPlayerCap entityCap, @Nullable EntityRenderer<LocalPlayer> renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks)
	{
		ClientModel model = entityCap.getEntityModel(ClientModels.CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entityCap.getClientAnimator().setPoseToModel(partialTicks);
		ModMatrix4f[] poses = armature.getJointTransforms();
		
		PoseStack poseStack1 = new PoseStack();
		poseStack1.translate(0.0D, 0.0D, 1000.0D);
		ModMatrix4f.scaleStack(poseStack1, new ModMatrix4f(poseStack.last().pose()));
		poseStack1.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		VertexConsumer vc = buffer.getBuffer(ModRenderTypes.getAnimatedModel(entityCap.getOriginalEntity().getSkinTextureLocation()));
		
		Set<Integer> jointMask = new HashSet<>();
		for (Layer<?, ?> layer : this.layers) jointMask.addAll(layer.getJointMask(entityCap));
		
		ClientModels.CLIENT.ENTITY_BIPED.draw(poseStack1, vc, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, poses, jointMask);
		
		this.renderLayer(entityCap, poses, buffer, poseStack1, packedLight, partialTicks);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(LocalPlayer entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
}
