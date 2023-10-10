package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.HollowCap;
import com.skullmangames.darksouls.common.entity.Hollow;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HollowRenderer extends BipedRenderer<Hollow, HollowCap>
{
	public static final ResourceLocation[] TEXTURES = new ResourceLocation[]
			{
					DarkSouls.rl("textures/entities/hollow/hollow.png"),
					DarkSouls.rl("textures/entities/hollow/rotten_hollow.png"),
					DarkSouls.rl("textures/entities/hollow/lordran_hollow.png")
			};
	
	@Override
	protected ResourceLocation getEntityTexture(Hollow entity)
	{
		return TEXTURES[entity.getTextureId()];
	}
}
