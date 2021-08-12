package com.skullmangames.darksouls.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.animation.types.ActionAnimation;
import com.skullmangames.darksouls.animation.types.AimingAnimation;
import com.skullmangames.darksouls.animation.types.DynamicAnimation;
import com.skullmangames.darksouls.client.renderer.entity.ArmatureRenderer;
import com.skullmangames.darksouls.client.renderer.entity.model.Armature;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.client.renderer.layer.HeldItemLayer;
import com.skullmangames.darksouls.client.renderer.layer.WearableItemLayer;
import com.skullmangames.darksouls.common.entities.ClientPlayerData;
import com.skullmangames.darksouls.core.init.ClientModelInit;
import com.skullmangames.darksouls.util.math.vector.PublicMatrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class FirstPersonRenderer extends ArmatureRenderer<ClientPlayerEntity, ClientPlayerData>
{
	public FirstPersonRenderer()
	{
		super();
		layers.add(new HeldItemLayer<>());
		layers.add(new WearableItemLayer<>(EquipmentSlotType.CHEST));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.LEGS));
		layers.add(new WearableItemLayer<>(EquipmentSlotType.FEET));
	}
	
	@Override
	public void render(ClientPlayerEntity entityIn, ClientPlayerData entitydata, EntityRenderer<ClientPlayerEntity> renderer, IRenderTypeBuffer buffer, MatrixStack matStackIn, int packedLightIn, float partialTicks)
	{
		@SuppressWarnings("resource")
		ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vector3d projView = renderInfo.getPosition();
		double x = MathHelper.lerp(partialTicks, entityIn.xOld, entityIn.getX()) - projView.x();
		double y = MathHelper.lerp(partialTicks, entityIn.yOld, entityIn.getY()) - projView.y();
		double z = MathHelper.lerp(partialTicks, entityIn.zOld, entityIn.getZ()) - projView.z();
		ClientModel model = entitydata.getEntityModel(ClientModelInit.CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entitydata.getClientAnimator().setPoseToModel(partialTicks);
		PublicMatrix4f[] poses = armature.getJointTransforms();
		
		matStackIn.pushPose();
		Vector4f headPos = new Vector4f(0, entityIn.getEyeHeight(), 0, 1.0F);
		PublicMatrix4f.transform(poses[9], headPos, headPos);
		float pitch = renderInfo.getXRot();
		
		DynamicAnimation base = entitydata.getClientAnimator().getPlayer().getPlay();
		DynamicAnimation mix = entitydata.getClientAnimator().mixLayer.animationPlayer.getPlay();
		
		boolean flag1 = base instanceof ActionAnimation;
		boolean flag2 = mix instanceof AimingAnimation;
		
		float zCoord = flag1 ? 0 : poses[0].m32;
		float posZ = Math.min(headPos.z() - zCoord, 0);
		
		if (headPos.z() > poses[0].m32) {
			posZ += (poses[0].m32 - headPos.z());
		}
		
		if (!flag2) {
			matStackIn.mulPose(Vector3f.XP.rotationDegrees(pitch));
		}
		
		float interpolation = pitch > 0.0F ? pitch / 90.0F : 0.0F;
		matStackIn.translate(x, y - 0.1D - (0.2D * (flag2 ? 0.8D : interpolation)), z + 0.1D + (0.7D * (flag2 ? 0.0D : interpolation)) - posZ);
		
		ClientModelInit.CLIENT.ENTITY_BIPED_FIRST_PERSON.draw(matStackIn, buffer.getBuffer(ModRenderTypes.getAnimatedModel(entitydata.getOriginalEntity().getSkinTextureLocation())),
				packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		
		if(!entityIn.isSpectator())
		{
			renderLayer(entitydata, entityIn, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.popPose();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(ClientPlayerEntity entityIn)
	{
		return entityIn.getSkinTextureLocation();
	}
}