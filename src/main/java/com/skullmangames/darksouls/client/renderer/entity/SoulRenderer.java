package com.skullmangames.darksouls.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.skullmangames.darksouls.common.entity.SoulEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SoulRenderer extends EntityRenderer<SoulEntity>
{
	private static final ResourceLocation SOUL_LOCATION = new ResourceLocation("textures/particle/soul.png");

	public SoulRenderer(EntityRendererManager p_i46178_1_)
	{
		super(p_i46178_1_);
		this.shadowRadius = 0.15F;
		this.shadowStrength = 0.75F;
	}

	@Override
	protected int getBlockLightLevel(SoulEntity p_225624_1_, BlockPos p_225624_2_)
	{
		return MathHelper.clamp(super.getBlockLightLevel(p_225624_1_, p_225624_2_) + 7, 0, 15);
	}

	@Override
	public void render(SoulEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_,
			int p_225623_6_)
	{
		p_225623_4_.pushPose();
		p_225623_4_.translate(0.0D, (double) 0.1F, 0.0D);
		p_225623_4_.mulPose(this.entityRenderDispatcher.cameraOrientation());
		p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		p_225623_4_.scale(0.3F, 0.3F, 0.3F);
		p_225623_4_.popPose();
		super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
	}

	@Override
	public ResourceLocation getTextureLocation(SoulEntity p_110775_1_)
	{
		return SOUL_LOCATION;
	}
}
