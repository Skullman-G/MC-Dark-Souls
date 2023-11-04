package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.entity.projectile.Firebomb;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;

@OnlyIn(Dist.CLIENT)
public class FirebombRenderer extends EntityRenderer<Firebomb>
{
	private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;
	private final ItemRenderer itemRenderer;
	
	public FirebombRenderer(Context context)
	{
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}
	
	@Override
	public void render(Firebomb entity, float p_116086_, float p_116087_, PoseStack poseStack, MultiBufferSource source, int packedLight)
	{
		super.render(entity, p_116086_, p_116087_, poseStack, source, packedLight);
		
		if (entity.tickCount >= 2
				|| !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < MIN_CAMERA_DISTANCE_SQUARED))
		{
			poseStack.pushPose();
			poseStack.translate(0.0F, 0.1F, 0.0F);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			this.itemRenderer.renderStatic(entity.getItem(), ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, packedLight,
					OverlayTexture.NO_OVERLAY, poseStack, source, entity.getId());
			poseStack.popPose();
			super.render(entity, p_116086_, p_116087_, poseStack, source, packedLight);
		}
	}
	
	@SuppressWarnings("deprecation")
	public ResourceLocation getTextureLocation(Firebomb entity)
	{
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
