package com.skullmangames.darksouls.client.renderer.entity.additional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MiracleEffectRenderer extends AdditionalEntityRenderer
{
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/additional/miracle_circle.png");

	@Override
	public boolean shouldDraw(LivingEntity entity)
	{
		return true;
	}
	
	@Override
	public void draw(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, 0.25F, 0.0F, -90.0F, false, partialTicks);
		VertexConsumer vertexBuilder = bufferSource.getBuffer(ModRenderTypes.getEntityEffect(TEXTURE_LOCATION));
		
		this.drawTexturedModalRect2DPlane(mvMatrix, vertexBuilder, -1.0F, -1.0F, 1.0F, 1.0F, 0, 0, 32, 32);
	}
}
