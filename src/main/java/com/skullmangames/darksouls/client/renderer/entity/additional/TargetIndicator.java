package com.skullmangames.darksouls.client.renderer.entity.additional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TargetIndicator extends AdditionalEntityRenderer
{
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/additional/target_indicator.png");
	private static final RenderType RENDER_TYPE = ModRenderTypes.getEntityIndicator(TEXTURE_LOCATION);
	
	@Override
	public boolean shouldDraw(LivingEntity entity)
	{
		return ClientManager.INSTANCE.getPlayerCap().getTarget() == entity;
	}

	@Override
	public void draw(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, entity.getBbHeight() * (3F/5F), 0.0F, true, partialTicks);
		VertexConsumer vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
		
		this.drawTextured2DPlane(mvMatrix, vertexBuilder, -0.25F, -0.25F, 0.25F, 0.25F, 0, 0, 15, 15);
	}
}
