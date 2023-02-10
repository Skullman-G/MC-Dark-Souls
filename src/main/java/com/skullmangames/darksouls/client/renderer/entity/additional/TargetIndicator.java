package com.skullmangames.darksouls.client.renderer.entity.additional;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.ClientManager;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.LivingEntity;
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
	public void draw(LivingEntity entity, MatrixStack poseStack, IRenderTypeBuffer bufferSource, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, entity.getBbHeight() * (3F/5F), 0.0F, true, partialTicks);
		IVertexBuilder vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
		
		this.drawTextured2DPlane(mvMatrix, vertexBuilder, -0.25F, -0.25F, 0.25F, 0.25F, 0, 0, 15, 15);
	}
}
