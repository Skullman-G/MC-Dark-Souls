package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.HollowEntity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

public class HollowRenderer extends BipedRenderer<HollowEntity, BipedModel<HollowEntity>>
{
	private static final ResourceLocation HOLLOW_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	public HollowRenderer(final EntityRendererManager manager)
	{
		super(manager, new BipedModel<HollowEntity>(RenderType::entityCutoutNoCull, 0.0F, 0.0F, 64, 64), 0.5F);
		this.addLayer(new BipedArmorLayer<>(this, new BipedModel<HollowEntity>(RenderType::entityCutoutNoCull, 0.5F, 0.0F, 64, 64), new BipedModel<HollowEntity>(RenderType::entityCutoutNoCull, 1.0F, 0.0F, 64, 64)));
	}

	@Override
	public ResourceLocation getTextureLocation(HollowEntity p_110775_1_)
	{
		return HOLLOW_TEXTURE;
	}
}
