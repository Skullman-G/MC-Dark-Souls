package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entities.HollowEntity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

public class HollowRenderer extends MobRenderer<HollowEntity, BipedModel<HollowEntity>>
{
	private static final ResourceLocation HOLLOW_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	public HollowRenderer(final EntityRendererManager manager)
	{
		super(manager, new BipedModel<HollowEntity>(RenderType::entityCutoutNoCull, 0.0F, 0.0F, 64, 64), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(HollowEntity p_110775_1_)
	{
		return HOLLOW_TEXTURE;
	}
}
