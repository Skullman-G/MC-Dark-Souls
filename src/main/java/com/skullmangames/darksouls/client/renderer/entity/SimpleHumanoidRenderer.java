package com.skullmangames.darksouls.client.renderer.entity;

import com.skullmangames.darksouls.common.capability.entity.LivingCap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleHumanoidRenderer<E extends LivingEntity, T extends LivingCap<E>> extends BipedRenderer<E, T>
{
	public final ResourceLocation textureLocation;
	
	public SimpleHumanoidRenderer(String texturePath)
	{
		this.textureLocation = new ResourceLocation("textures/entity/" + texturePath + ".png");
	}
	
	public SimpleHumanoidRenderer(String namespace, String texturePath)
	{
		this.textureLocation = new ResourceLocation(namespace, "textures/entities/" + texturePath + ".png");
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entity)
	{
		return textureLocation;
	}
}
