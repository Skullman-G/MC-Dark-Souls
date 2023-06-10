package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.AbstractSoulEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SoulRenderer extends AbstractSoulRenderer
{
	private static final ResourceLocation TEXTURE = DarkSouls.rl("textures/particle/soul.png");
	
	public SoulRenderer(Context p_i46178_1_)
	{
		super(p_i46178_1_);
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractSoulEntity p_114482_)
	{
		return TEXTURE;
	}
}
