package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.ModRenderTypes;
import com.skullmangames.darksouls.client.renderer.entity.model.ClientModel;
import com.skullmangames.darksouls.common.entity.TerracottaVase;
import com.skullmangames.darksouls.core.init.ClientModels;
import com.skullmangames.darksouls.core.util.math.vector.PublicMatrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TerracottaVaseRenderer extends EntityRenderer<TerracottaVase>
{
	private static final ResourceLocation TEXTURE = DarkSouls.rl("textures/entities/terracotta_vase.png");
	
	public TerracottaVaseRenderer(Context ctx)
	{
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(TerracottaVase entity)
	{
		return TEXTURE;
	}
	
	@Override
	public void render(TerracottaVase entity, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
	{
		super.render(entity, p_114486_, p_114487_, poseStack, buffer, packedLight);
		
		RenderType renderType = ModRenderTypes.getAnimatedModel(this.getTextureLocation(entity));
		VertexConsumer builder = buffer.getBuffer(renderType);
		ClientModel model = ClientModels.CLIENT.TERRACOTTA_VASE;
		PublicMatrix4f[] poses = new PublicMatrix4f[1];
		poses[0] = new PublicMatrix4f();
		poseStack.pushPose();
		float scale = 0.9F;
		poseStack.scale(scale, scale, scale);
		model.draw(poseStack, builder, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		poseStack.popPose();
	}
}