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
	private boolean active;
	private int a;
	private int r;
	private LivingEntity lastTarget;
	
	@Override
	public boolean shouldDraw(LivingEntity entity)
	{
		LivingEntity target = ClientManager.INSTANCE.getPlayerCap().getTarget();
		boolean newActive = target != null;
		if (active != newActive)
		{
			this.a = 10;
			this.active = newActive;
			
			if (this.active) this.lastTarget = target;
			else this.a = 10;
		}
		return target == entity || (a > 0 && this.lastTarget == entity);
	}

	@Override
	public void draw(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks)
	{
		Matrix4f mvMatrix = super.getMVMatrix(poseStack, entity, 0.0F, entity.getBbHeight() * (3F/5F), 0.0F, true, partialTicks);
		float scale = this.active ? 0.25F * (float)Math.sin(Math.PI * 1.5F * 0.1F * this.a) + 0.6F
				: this.a * 0.1F * 0.725F;
		mvMatrix.multiply(Matrix4f.createScaleMatrix(scale, scale, scale));
		mvMatrix.multiply(Vector3f.ZN.rotationDegrees(this.r));
		VertexConsumer vertexBuilder = bufferSource.getBuffer(RENDER_TYPE);
		
		this.drawTextured2DPlane(mvMatrix, vertexBuilder, -0.25F, -0.25F, 0.25F, 0.25F, 0, 0, 15, 15);
		
		this.r = (this.r + 1) % 360;
		if (this.a > 0) this.a -= 1;
	}
}
