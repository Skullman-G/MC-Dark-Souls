package com.skullmangames.darksouls.client.renderer.entity.additional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
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
	private static boolean active;
	private static int a;
	private static int r;
	
	@Override
	public boolean shouldDraw(LivingEntity entity)
	{
		LivingEntity target = ClientManager.INSTANCE.getPlayerCap().getTarget();
		boolean newActive = target != null;
		if (active != newActive)
		{
			a = 10;
			active = newActive;
		}
		return target == entity;
	}

	@Override
	public void draw(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, entity.getBbHeight() * (3F/5F), 0.0F, true, partialTicks);
		float scale = 0.25F * (float)Math.sin(Math.PI * 1.5F * 0.1F * a) + 0.6F;
		mvMatrix.multiply(Matrix4f.createScaleMatrix(scale, scale, scale));
		mvMatrix.multiply(Vector3f.ZN.rotationDegrees(r));
		VertexConsumer vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
		
		this.drawTextured2DPlane(mvMatrix, vertexBuilder, -0.25F, -0.25F, 0.25F, 0.25F, 0, 0, 15, 15);
		
		r = (r + 1) % 360;
		if (a > 0) a -= 1;
	}
}
