package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.DarkSouls;
import com.skullmangames.darksouls.common.capability.entity.LivingData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleHumanoidRenderer<E extends LivingEntity, T extends LivingData<E>> extends BipedRenderer<E, T>
{
	public final ResourceLocation textureLocation;
	
	public SimpleHumanoidRenderer(String texturePath)
	{
		this.textureLocation = new ResourceLocation(DarkSouls.MOD_ID, "textures/entities/" + texturePath + ".png");
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entity)
	{
		return textureLocation;
	}
}
