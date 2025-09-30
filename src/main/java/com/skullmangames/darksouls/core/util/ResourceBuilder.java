package com.skullmangames.darksouls.core.util;

import net.minecraft.resources.ResourceLocation;

public interface ResourceBuilder<T>
{
	public ResourceLocation getId();
	
	public T build();
}
