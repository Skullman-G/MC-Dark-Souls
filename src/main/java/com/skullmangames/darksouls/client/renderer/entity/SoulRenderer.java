package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.common.entity.AbstractSoulEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SoulRenderer extends AbstractSoulRenderer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/particle/soul.png");
	
	public SoulRenderer(EntityRendererManager p_i46178_1_)
	{
		super(p_i46178_1_);
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractSoulEntity p_114482_)
	{
		return TEXTURE;
	}
}
