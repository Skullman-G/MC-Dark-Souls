package com.skullmangames.darksouls.client.renderer.entity.model.vanilla;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.entity.HollowEntity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HollowRenderer extends HumanoidMobRenderer<HollowEntity, HumanoidModel<HollowEntity>>
{
	private static final ResourceLocation HOLLOW_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/hollow/hollow.png");
	
	public HollowRenderer(EntityRendererProvider.Context context)
	{
		super(context, new HumanoidModel<HollowEntity>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(HollowEntity p_110775_1_)
	{
		return HOLLOW_TEXTURE;
	}
}