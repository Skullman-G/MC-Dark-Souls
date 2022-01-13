package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.skullmangames.darksouls.common.entity.SoulEntity;
import com.skullmangames.darksouls.core.util.math.MathUtils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulRenderer extends EntityRenderer<SoulEntity>
{
	private static final ResourceLocation SOUL_LOCATION = new ResourceLocation("textures/particle/soul.png");

	public SoulRenderer(EntityRendererProvider.Context p_i46178_1_)
	{
		super(p_i46178_1_);
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}

	@Override
	protected int getBlockLightLevel(SoulEntity p_225624_1_, BlockPos p_225624_2_)
	{
		return MathUtils.clamp(super.getBlockLightLevel(p_225624_1_, p_225624_2_) + 7, 0, 15);
	}

	@Override
	public void render(SoulEntity p_114485_, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource p_114489_, int p_114490_)
	{
		poseStack.pushPose();
		poseStack.translate(0.0D, (double) 0.1F, 0.0D);
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		poseStack.scale(0.3F, 0.3F, 0.3F);
		poseStack.popPose();
		super.render(p_114485_, p_114486_, p_114487_, poseStack, p_114489_, p_114490_);
	}

	@Override
	public ResourceLocation getTextureLocation(SoulEntity p_114482_)
	{
		return SOUL_LOCATION;
	}
}
