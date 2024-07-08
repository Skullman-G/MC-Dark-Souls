package com.skullmangames.darksouls.client.renderer.entity.model;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractModel
{
	protected ResourceLocation location;
	
	public AbstractModel(ResourceLocation location)
	{
		this.location = location;
	}
	
	public ResourceLocation getId()
	{
		return this.location;
	}
	
	public String getName()
	{
		return location.getPath();
	}
}
