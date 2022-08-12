package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import com.skullmangames.darksouls.client.animation.AnimationLayer.LayerPart;
import com.skullmangames.darksouls.client.renderer.entity.ArmatureRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.client.renderer.layer.WearableItemLayer;
import com.skullmangames.darksouls.common.animation.types.ActionAnimation;
import com.skullmangames.darksouls.common.animation.types.AimingAnimation;
import com.skullmangames.darksouls.common.capability.entity.LocalPlayerCap;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;

public class FirstPersonRenderer extends ArmatureRenderer<ClientPlayerEntity, LocalPlayerCap>
{
	private Minecraft minecraft = Minecraft.getInstance();
	
	public FirstPersonRenderer()
	{
		super();
		layers.add(new HeldItemLayer<>());
		layers.add(new WearableItemLayer<>(EquipmentSlotType.CHEST));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.LEGS));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.FEET));
	}
	
	@Override
	public void render(ClientPlayerEntity entityIn, LocalPlayerCap entityCap, EntityRenderer<ClientPlayerEntity> renderer, IRenderTypeBuffer buffer, MatrixStack matStackIn, int packedLightIn, float partialTicks)
	{
		ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
		Vector3d projView = camera.getPosition();
		double x = MathHelper.lerp(partialTicks, entityIn.xOld, entityIn.getX()) - projView.x();
		double y = MathHelper.lerp(partialTicks, entityIn.yOld, entityIn.getY()) - projView.y();
		double z = MathHelper.lerp(partialTicks, entityIn.zOld, entityIn.getZ()) - projView.z() - 0.1F;
		ClientModel model = entityCap.getEntityModel(ClientModels.CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entityCap.getClientAnimator().setPoseToModel(partialTicks);
		PublicMatrix4f[] poses = armature.getJointTransforms();
		
		matStackIn.pushPose();
		Vector4f headPos = new Vector4f(0, entityIn.getEyeHeight(), 0, 1.0F);
		PublicMatrix4f.transform(poses[9], headPos, headPos);
		float pitch = camera.getXRot();
		
		boolean flag1 = entityCap.getClientAnimator().baseLayer.animationPlayer.getPlay() instanceof ActionAnimation;
		boolean flag2 = false;
		
		for (LayerPart layerPart : LayerPart.compositeLayers())
		{
			if (entityCap.getClientAnimator().getCompositeLayer(layerPart).animationPlayer.getPlay() instanceof AimingAnimation)
			{
				flag2 = true;
				break;
			}
		}
		
		float zCoord = flag1 ? 0 : poses[0].m32;
		float posZ = Math.min(headPos.z() - zCoord, 0);
		
		if (headPos.z() > poses[0].m32)
		{
			posZ += (poses[0].m32 - headPos.z());
		}
		
		if (!flag2)
		{
			matStackIn.mulPose(Vector3f.XP.rotationDegrees(pitch));
		}
		
		float interpolation = pitch > 0.0F ? pitch / 90.0F : 0.0F;
		matStackIn.translate(x, y - 0.1D - (0.2D * (flag2 ? 0.8D : interpolation)), z + 0.1D + (0.7D * (flag2 ? 0.0D : interpolation)) - posZ);
		
		ClientModels.CLIENT.ENTITY_BIPED_FIRST_PERSON.draw(matStackIn, buffer.getBuffer(ModRenderTypes.getAnimatedModel(entityCap.getOriginalEntity().getSkinTextureLocation())),
				packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		
		if(!entityIn.isSpectator())
		{
			renderLayer(entityCap, entityIn, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.popPose();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(ClientPlayerEntity entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
}