package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.vanilla.FireKeeperModel;
import com.skullmangames.darksouls.common.entity.FireKeeperEntity;
import com.skullmangames.darksouls.core.init.ModModelLayers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FireKeeperRenderer extends MobRenderer<FireKeeperEntity, FireKeeperModel>
{
	private static final ResourceLocation FIRE_KEEPER_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/fire_keeper/fire_keeper.png");

	public FireKeeperRenderer(EntityRendererProvider.Context context)
	{
		super(context, new FireKeeperModel(context.bakeLayer(ModModelLayers.FIRE_KEEPER)), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(final FireKeeperEntity entity)
	{
		return FIRE_KEEPER_TEXTURE;
	}
}
