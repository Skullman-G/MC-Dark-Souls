package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.common.entity.AbstractSoulEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class HumanityRenderer extends AbstractSoulRenderer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/humanity.png");
	
	public HumanityRenderer(Context p_i46178_1_)
	{
		super(p_i46178_1_);
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractSoulEntity p_114482_)
	{
		return TEXTURE;
	}
}
