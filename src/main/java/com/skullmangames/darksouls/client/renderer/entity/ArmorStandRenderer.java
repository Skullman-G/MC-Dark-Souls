package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.LivingCap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandRenderer extends BipedRenderer<ArmorStand, LivingCap<ArmorStand>>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/armor_stand.png");
	
	@Override
	protected ResourceLocation getEntityTexture(ArmorStand entityIn)
	{
		return TEXTURE;
	}
}
