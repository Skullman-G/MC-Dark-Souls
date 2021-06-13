package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.client.renderer.entity.model.FireKeeperModel;
import com.skullmangames.darksouls.common.entities.FireKeeperEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class FireKeeperRenderer extends MobRenderer<FireKeeperEntity, FireKeeperModel>
{
	private static final ResourceLocation FIRE_KEEPER_TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/fire_keeper/fire_keeper.png");

	public FireKeeperRenderer(final EntityRendererManager manager)
	{
		super(manager, new FireKeeperModel(), 0.7F);
	}

	@Override
	public ResourceLocation getTextureLocation(final FireKeeperEntity entity)
	{
		return FIRE_KEEPER_TEXTURE;
	}
}
