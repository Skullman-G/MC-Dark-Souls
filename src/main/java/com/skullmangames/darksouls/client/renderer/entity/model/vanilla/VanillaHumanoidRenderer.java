package com.skullmangames.darksouls.client.renderer.entity.model.vanilla;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;

public class VanillaHumanoidRenderer<T extends MobEntity> extends BipedRenderer<T, BipedModel<T>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	public VanillaHumanoidRenderer(EntityRendererManager context)
	{
		super(context, new BipedModel<T>(RenderType::entityCutoutNoCull, 0F, 0F, 64, 64), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity)
	{
		return TEXTURE;
	}
}