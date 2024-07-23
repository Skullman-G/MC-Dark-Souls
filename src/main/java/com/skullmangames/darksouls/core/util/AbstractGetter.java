package com.skullmangames.darksouls.core.util;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractGetter<T>
{
	private final ResourceLocation id;
	
	public AbstractGetter(ResourceLocation id)
	{
		this.id = id;
	}
	
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	public abstract T get();
}
