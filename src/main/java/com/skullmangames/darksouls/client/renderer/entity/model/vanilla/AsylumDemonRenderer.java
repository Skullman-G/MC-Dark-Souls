package com.skullmangames.darksouls.client.renderer.entity.model.vanilla;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.AsylumDemonEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

public class AsylumDemonRenderer extends BipedRenderer<AsylumDemonEntity, BipedModel<AsylumDemonEntity>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	public AsylumDemonRenderer(final EntityRendererManager manager)
	{
		super(manager, new BipedModel<AsylumDemonEntity>(RenderType::entityCutoutNoCull, 0.0F, 0.0F, 64, 64), 0.5F);
		this.addLayer(new BipedArmorLayer<>(this, new BipedModel<AsylumDemonEntity>(RenderType::entityCutoutNoCull, 0.5F, 0.0F, 64, 64), new BipedModel<AsylumDemonEntity>(RenderType::entityCutoutNoCull, 1.0F, 0.0F, 64, 64)));
	}

	@Override
	public ResourceLocation getTextureLocation(AsylumDemonEntity p_110775_1_)
	{
		return TEXTURE;
	}
}