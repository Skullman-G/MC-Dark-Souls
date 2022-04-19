package com.skullmangames.darksouls.client.renderer.entity.model.vanilla;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.StrayDemon;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AsylumDemonRenderer extends HumanoidMobRenderer<StrayDemon, HumanoidModel<StrayDemon>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	public AsylumDemonRenderer(EntityRendererProvider.Context context)
	{
		super(context, new HumanoidModel<StrayDemon>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(StrayDemon p_110775_1_)
	{
		return TEXTURE;
	}
}