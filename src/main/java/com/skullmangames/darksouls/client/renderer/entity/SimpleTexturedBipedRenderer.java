package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.common.capability.entity.LivingData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleTexturedBipedRenderer<E extends LivingEntity, T extends LivingData<E>> extends BipedRenderer<E, T>
{
	public final ResourceLocation textureLocation;
	
	public SimpleTexturedBipedRenderer(String texturePath)
	{
		textureLocation = new ResourceLocation(texturePath);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entityIn)
	{
		return textureLocation;
	}
}
