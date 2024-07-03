package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class VanillaHumanoidRenderer<T extends Mob> extends HumanoidMobRenderer<T, HumanoidModel<T>>
{
	private static final ResourceLocation TEXTURE = DarkSouls.rl("textures/entities/hollow/hollow.png");
	
	public VanillaHumanoidRenderer(EntityRendererProvider.Context context)
	{
		this(context, 0.5F);
	}
	
	public VanillaHumanoidRenderer(EntityRendererProvider.Context context, float shadowRadius)
	{
		super(context, new HumanoidModel<T>(context.bakeLayer(ModelLayers.ZOMBIE)), shadowRadius);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity)
	{
		return TEXTURE;
	}
}