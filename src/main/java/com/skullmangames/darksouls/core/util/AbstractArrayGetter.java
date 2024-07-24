package com.skullmangames.darksouls.core.util;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractArrayGetter<T>
{
	private final ResourceLocation[] ids;
	
	public AbstractArrayGetter(ResourceLocation... ids)
	{
		this.ids = ids;
	}
	
	public ResourceLocation[] getIds()
	{
		return this.ids;
	}
	
	public abstract T get();
}
