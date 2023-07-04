package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
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

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

public class FirstPersonRenderer extends ArmatureRenderer<LocalPlayer, LocalPlayerCap>
{
	private Minecraft minecraft = Minecraft.getInstance();
	
	public FirstPersonRenderer()
	{
		super();
		layers.add(new HeldItemLayer<>());
		layers.add(new WearableItemLayer<>(EquipmentSlot.CHEST));
		layers.add(new WearableItemLayer<>(EquipmentSlot.LEGS));
		layers.add(new WearableItemLayer<>(EquipmentSlot.FEET));
	}
	
	@Override
	public void render(LocalPlayerCap entityCap, EntityRenderer<LocalPlayer> renderer, MultiBufferSource buffer, PoseStack matStackIn, int packedLightIn, float partialTicks)
	{
		LocalPlayer player = entityCap.getOriginalEntity();
		Camera camera = minecraft.gameRenderer.getMainCamera();
		Vec3 projView = camera.getPosition();
		double x = Mth.lerp(partialTicks, player.xOld, player.getX()) - projView.x();
		double y = Mth.lerp(partialTicks, player.yOld, player.getY()) - projView.y();
		double z = Mth.lerp(partialTicks, player.zOld, player.getZ()) - projView.z() - 0.1F;
		ClientModel model = entityCap.getEntityModel(ClientModels.CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entityCap.getClientAnimator().setPoseToModel(partialTicks);
		PublicMatrix4f[] poses = armature.getJointTransforms();
		
		matStackIn.pushPose();
		Vector4f headPos = new Vector4f(0, player.getEyeHeight(), 0, 1.0F);
		PublicMatrix4f.transform(poses[9], headPos, headPos);
		float pitch = camera.getXRot();
		
		boolean flag1 = entityCap.getClientAnimator().baseLayer.animationPlayer.getPlay() instanceof ActionAnimation;
		boolean flag2 = false;
		
		for (LayerPart layerPart : LayerPart.mixLayers())
		{
			if (entityCap.getClientAnimator().getMixLayer(layerPart).animationPlayer.getPlay() instanceof AimingAnimation)
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
		
		if(!player.isSpectator())
		{
			renderLayer(entityCap, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.popPose();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(LocalPlayer entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
}